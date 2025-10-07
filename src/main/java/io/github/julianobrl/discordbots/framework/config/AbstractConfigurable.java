package io.github.julianobrl.discordbots.framework.config;

import io.github.julianobrl.discordbots.framework.exceptions.ConfigLoaderException;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractConfigurable implements IConfigurable {
    protected final String configId;
    protected final Map<String, Object> configurations = new ConcurrentHashMap<>();
    protected final Yaml yaml = new Yaml();

    protected AbstractConfigurable(String configId) {
        this.configId = configId;
    }

    protected abstract String getConfigPath();

    @Override
    public void loadConfigurations() throws ConfigLoaderException {
        InputStream input = null;
        try {
            File externalFile = new File("configs/" + getConfigPath());
            File configDir = new File("configs");

            // Ensure the configs directory exists
            if (!configDir.exists()) {
                configDir.mkdirs();
                log.info("Created configs directory: {}", configDir.getAbsolutePath());
            }

            // Try to load from external file
            if (externalFile.exists()) {
                input = new FileInputStream(externalFile);
                log.info("Loading config for {} from external file: {}", configId, externalFile.getAbsolutePath());
            } else {
                // If external file doesn't exist, copy from internal resource
                input = getClass().getClassLoader().getResourceAsStream(getConfigPath());
                if (input != null) {
                    log.info("External config file not found for {}. Copying from internal resource: {}", configId, getConfigPath());

                    // Copy internal resource to external file
                    try (FileOutputStream output = new FileOutputStream(externalFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        log.info("Successfully copied internal resource to external file: {}", externalFile.getAbsolutePath());
                    }

                    // Reopen the external file for reading
                    input.close();
                    input = new FileInputStream(externalFile);
                }
            }

            if (input == null) {
                throw new ConfigLoaderException("Config file not found for " + configId + ": " + getConfigPath());
            }

            configurations.clear();
            configurations.putAll(yaml.load(input));
            log.info("Successfully loaded configurations for {}.", configId);

        } catch (Exception e) {
            throw new ConfigLoaderException("Failed to load configurations for " + configId, e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("Error closing config input stream for {}: {}", configId, e.getMessage());
                }
            }
        }
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getProperty(String key, String defaultValue) {
        String[] keys = key.split("\\.");
        Map<String, Object> current = configurations;
        Object value = null;

        for (int i = 0; i < keys.length; i++) {
            if (current == null || !current.containsKey(keys[i])) {
                return defaultValue;
            }
            value = current.get(keys[i]);
            if (i < keys.length - 1) {
                if (!(value instanceof Map)) {
                    System.err.println("Warning: Expected a Map for key part '" + keys[i] + "' but found a " + value.getClass().getSimpleName() + " for " + configId);
                    return defaultValue;
                }
                current = (Map<String, Object>) value;
            }
        }
        return value != null ? value.toString() : defaultValue;
    }

    @Override
    public String getId() {
        return configId;
    }
}
