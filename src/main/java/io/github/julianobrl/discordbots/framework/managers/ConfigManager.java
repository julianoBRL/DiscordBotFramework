package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.annotations.configs.ConfigProperty;
import io.github.julianobrl.discordbots.framework.config.CoreConfigurable;
import io.github.julianobrl.discordbots.framework.config.IConfigurable;
import io.github.julianobrl.discordbots.framework.exceptions.ConfigLoaderException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConfigManager {
    private static final Map<String, IConfigurable> configurables = new ConcurrentHashMap<>();
    private static ConfigManager instance;

    private ConfigManager() {}

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void registerConfigurable(IConfigurable configurable) {
        configurables.put(configurable.getId(), configurable);
    }

    public IConfigurable getConfigurable(String id) {
        return configurables.get(id);
    }

    public void loadAllConfigs(String... basePackages) {

        CoreConfigurable coreConfig = new CoreConfigurable();
        try {
            coreConfig.loadConfigurations();
            registerConfigurable(coreConfig);
        } catch (ConfigLoaderException e) {
            log.error("CRITICAL: Failed to load core configurations: " + e.getMessage());
        }

        //load all @ConfigProperty
        log.info("Starting @ConfigProperty injection...");
        injectProperties(basePackages);
        log.info("@ConfigProperty injection finished.");

    }

    /**
     * Injeta valores de configuração em campos anotados com @ConfigProperty.
     * @param basePackages Pacotes base para escanear em busca de classes.
     */
    public void injectProperties(String... basePackages) {
        if (basePackages == null || basePackages.length == 0) {
            log.warn("No base packages provided for @ConfigProperty injection. Skipping injection.");
            return;
        }

        // Configura o Reflections para escanear os pacotes fornecidos
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackages)
                .addScanners(Scanners.FieldsAnnotated) // Escaneia campos anotados
        );

        // Obtém todos os campos anotados com @ConfigProperty
        Set<Field> annotatedFields = reflections.getFieldsAnnotatedWith(ConfigProperty.class);

        for (Field field : annotatedFields) {
            ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
            String configKey = annotation.value();

            // Resolve o valor da propriedade (com lógica para plugin ID e default)
            String resolvedValue = resolveConfigProperty(configKey);

            if (resolvedValue == null) {
                log.warn("Could not find value for config property '{}' at field {}.{}",
                        configKey, field.getDeclaringClass().getName(), field.getName());
                continue; // Pula para o próximo campo se o valor não for encontrado
            }

            try {
                // Tenta tornar o campo acessível, caso seja privado
                field.setAccessible(true);

                // Obtém a instância da classe onde o campo está declarado.
                // ATENÇÃO: Se o campo for static, passe 'null'.
                // Se o campo for de instância, você precisará de uma instância da classe.
                // Para simplificar, assumimos que os campos @ConfigProperty são static.
                // Se precisar injetar em instâncias, o fluxo fica mais complexo,
                // geralmente exigindo um Container de DI.
                Object instanceToInject = null; // Assume static por enquanto

                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    log.error("Field {}.{} is annotated with @ConfigProperty but is not static. " +
                                    "Automatic injection for non-static fields is not supported by this ConfigManager.",
                            field.getDeclaringClass().getName(), field.getName());
                    continue; // Pula se não for static e não puder injetar
                }

                // Tenta converter o valor para o tipo do campo
                Object castedValue = convertValue(resolvedValue, field.getType());
                field.set(instanceToInject, castedValue);
                log.debug("Injected config property '{}' with value '{}' into field {}.{}",
                        configKey, resolvedValue, field.getDeclaringClass().getName(), field.getName());

            } catch (IllegalAccessException e) {
                log.error("Failed to access field {}.{} for config property '{}': {}",
                        field.getDeclaringClass().getName(), field.getName(), configKey, e.getMessage(), e);
            } catch (ClassCastException | NumberFormatException e) {
                log.error("Failed to convert value '{}' to type {} for field {}.{}: {}",
                        resolvedValue, field.getType().getName(), field.getDeclaringClass().getName(), field.getName(), e.getMessage(), e);
            } catch (Exception e) {
                log.error("An unexpected error occurred during injection of config property '{}' into field {}.{}: {}",
                        configKey, field.getDeclaringClass().getName(), field.getName(), e.getMessage(), e);
            } finally {
                // Sempre defina setAccessible de volta para false para boas práticas
                field.setAccessible(false);
            }
        }
    }

    /**
     * Resolve o valor de uma propriedade de configuração, lidando com chaves de plugin e valores default.
     * Formatos esperados:
     * - core.key
     * - {plugin_id}@{key}
     * - core.key:defaultValue
     * - {plugin_id}@{key}:defaultValue
     */
    private String resolveConfigProperty(String configKey) {
        String keyToSearch;
        String defaultValue = null;
        String pluginId = "application"; // Default para o core

        // 1. Separar chave e valor default (se existir)
        int defaultSeparatorIndex = configKey.indexOf(":");
        if (defaultSeparatorIndex != -1) {
            defaultValue = configKey.substring(defaultSeparatorIndex + 1);
            keyToSearch = configKey.substring(0, defaultSeparatorIndex);
        } else {
            keyToSearch = configKey;
        }

        int pluginSeparatorIndex = keyToSearch.indexOf("@");
        if (pluginSeparatorIndex != -1) {
            pluginId = keyToSearch.substring(0, pluginSeparatorIndex);
            keyToSearch = keyToSearch.substring(pluginSeparatorIndex + 1);
        }

        IConfigurable configurable = configurables.get(pluginId);
        if (configurable == null) {
            log.warn("Configurable with ID '{}' not found for key '{}'. Returning default value if available.", pluginId, configKey);
            return defaultValue;
        }

        String value = configurable.getProperty(keyToSearch);

        return value != null ? value : defaultValue;
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }
        log.warn("Unsupported target type for @ConfigProperty injection: {}. Returning raw string value.", targetType.getName());
        return value;
    }

    public String getCoreProperty(String key) {
        IConfigurable core = getConfigurable("application");
        return core != null ? core.getProperty(key) : null;
    }

    public String getCoreProperty(String key, String defaultValue) {
        IConfigurable core = getConfigurable("application");
        return core != null ? core.getProperty(key, defaultValue) : defaultValue;
    }

    public String getPluginProperty(String pluginId, String key) {
        IConfigurable pluginConfig = getConfigurable(pluginId);
        return pluginConfig != null ? pluginConfig.getProperty(key) : null;
    }

    public String getPluginProperty(String pluginId, String key, String defaultValue) {
        IConfigurable pluginConfig = getConfigurable(pluginId);
        return pluginConfig != null ? pluginConfig.getProperty(key, defaultValue) : defaultValue;
    }
}
