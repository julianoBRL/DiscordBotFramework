package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.annotations.BotPlugin;
import io.github.julianobrl.discordbots.framework.exceptions.PluginLoaderException;
import io.github.julianobrl.discordbots.framework.plugins.AbstractBotPlugin;
import io.github.julianobrl.discordbots.framework.plugins.BotPluginInfo;
import io.github.julianobrl.discordbots.framework.plugins.CustomDefaultPluginManager;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BotPluginManager {
    private static final String PLUGINS_FOLDER = "plugins";

    private static BotPluginManager instance;

    private final PluginManager pf4jPluginManager;
    private final Map<String, AbstractBotPlugin> activePlugins = new ConcurrentHashMap<>(); // Map de plugins ativos por ID

    private BotPluginManager() {
        Path pluginsPath = Paths.get(PLUGINS_FOLDER);
        if (!pluginsPath.toFile().exists()) {
            if(!pluginsPath.toFile().mkdirs()){
                throw new PluginLoaderException("Error while criating one or more folders!");
            }
            log.warn("Plugins folder '{}' not found. Created it.", PLUGINS_FOLDER);
        }

        this.pf4jPluginManager = new CustomDefaultPluginManager(pluginsPath);
    }

    public static synchronized BotPluginManager getInstance() {
        if (instance == null) {
            instance = new BotPluginManager();
        }
        return instance;
    }

    public void loadPlugins() {
        log.info("Loading plugins from '{}'...", PLUGINS_FOLDER);
        pf4jPluginManager.loadPlugins();
        log.info("Finished loading plugin JARs. Found {} plugins.", pf4jPluginManager.getPlugins().size());
    }

    public void enablePlugins() {
        log.info("Starting plugins...");
        pf4jPluginManager.startPlugins();

        List<AbstractBotPlugin> extensions = pf4jPluginManager.getExtensions(AbstractBotPlugin.class);
        if (extensions.isEmpty()) {
            log.info("No IBotPlugin extensions found to enable.");
            return;
        }

        log.info("Enabling {} bot plugin extensions.", extensions.size());
        for (AbstractBotPlugin plugin : extensions) {
            BotPlugin botPluginAnnotation = plugin.getClass().getAnnotation(BotPlugin.class);
            if (botPluginAnnotation == null) {
                log.error("Plugin extension '{}' does not have @BotPlugin annotation. Skipping.", plugin.getClass().getName());
                continue;
            }

            BotPluginInfo pluginInfo = new BotPluginInfo().fromAnnotation(botPluginAnnotation);

            if (activePlugins.containsKey(pluginInfo.getId())) {
                log.error("Duplicate plugin ID '{}' found. Plugin '{}' from {} will not be enabled.",
                        pluginInfo.getId(), pluginInfo.getName(), plugin.getClass().getName());
                continue;
            }

            try {
                log.info("Enabling plugin '{}' (ID: {}, Version: {}, Author: {})", pluginInfo.getName(), pluginInfo.getId(), pluginInfo.getVersion(), pluginInfo.getAuthor());
                plugin.onEnable();
                activePlugins.put(pluginInfo.getId(), plugin);
                log.info("Plugin '{}' (ID: {}) enabled successfully.", pluginInfo.getName(), pluginInfo.getId());
            } catch (Exception e) {
                log.error("Failed to enable plugin '{}' (ID: {}): {}", pluginInfo.getName(), pluginInfo.getId(), e.getMessage(), e);
            }
        }
        log.info("All bot plugin extensions enabled.");
    }

    public void disablePlugins() {
        log.info("Disabling bot plugin extensions...");

        for (AbstractBotPlugin plugin : activePlugins.values()) {
            BotPlugin botPluginAnnotation = plugin.getClass().getAnnotation(BotPlugin.class);
            String pluginId = (botPluginAnnotation != null) ? botPluginAnnotation.id() : plugin.getClass().getName();
            String pluginName = (botPluginAnnotation != null) ? botPluginAnnotation.name() : pluginId;

            try {
                log.info("Disabling plugin '{}' (ID: {})", pluginName, pluginId);
                plugin.onDisable(); // Chama o método onDisable() do plugin
            } catch (Exception e) {
                log.error("Failed to disable plugin '{}' (ID: {}): {}", pluginName, pluginId, e.getMessage(), e);
            }
        }
        activePlugins.clear(); // Limpa o mapa

        log.info("Stopping and unloading PF4J plugins...");
        pf4jPluginManager.stopPlugins();
        pf4jPluginManager.unloadPlugins();
        log.info("PF4J plugins stopped and unloaded.");
    }

    public AbstractBotPlugin getPlugin(String id) {
        return activePlugins.get(id);
    }

    public Map<String, AbstractBotPlugin> getActivePlugins() {
        return new ConcurrentHashMap<>(activePlugins);
    }

    public PluginWrapper getPluginWrapper(String pluginId) {
        // Isso requer buscar o wrapper pelo ID do plugin do PF4J
        // O PF4J gerencia PluginWrapper por ID do plugin (não do Jar)
        for (PluginWrapper wrapper : pf4jPluginManager.getPlugins()) {
            AbstractBotPlugin plugin = (AbstractBotPlugin) pf4jPluginManager.getExtensions(AbstractBotPlugin.class, wrapper.getPluginId());
            if (plugin != null && plugin.getClass().isAnnotationPresent(BotPlugin.class)) {
                if (plugin.getClass().getAnnotation(BotPlugin.class).id().equals(pluginId)) {
                    return wrapper;
                }
            }
        }
        return null;
    }

}
