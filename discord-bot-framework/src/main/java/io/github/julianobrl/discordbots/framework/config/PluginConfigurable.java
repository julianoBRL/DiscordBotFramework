package io.github.julianobrl.discordbots.framework.config;

public class PluginConfigurable extends AbstractConfigurable {
    private final String pluginId;

    public PluginConfigurable(String pluginId) {
        super(pluginId);
        this.pluginId = pluginId;
    }

    @Override
    protected String getConfigPath() {
        return pluginId + "/configs.yml";
    }
}
