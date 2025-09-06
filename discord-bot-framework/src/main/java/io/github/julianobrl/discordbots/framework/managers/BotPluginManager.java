package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.exceptions.PluginLoaderException;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class BotPluginManager {
    private static final String PLUGINS_FOLDER = "plugins";

    private static BotPluginManager instance;

    private final PluginManager pf4jPluginManager;

    private BotPluginManager() {
        Path pluginsPath = Paths.get(PLUGINS_FOLDER);
        if (!pluginsPath.toFile().exists()) {
            if(!pluginsPath.toFile().mkdirs()){
                throw new PluginLoaderException("Error while criating one or more folders!");
            }
            log.warn("Plugins folder '{}' not found. Created it.", PLUGINS_FOLDER);
        }

        this.pf4jPluginManager = new DefaultPluginManager(pluginsPath);
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
        log.info("Plugins started!");
    }
}
