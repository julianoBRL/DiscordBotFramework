package io.github.julianobrl.discordbots.framework.plugins;

import org.pf4j.*;

import java.nio.file.Path;

public class CustomDefaultPluginManager extends DefaultPluginManager {

    public CustomDefaultPluginManager(Path pluginsPath) {
        super(pluginsPath);
    }

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new BotPluginDescriptorFinder();
    }

}
