package io.github.julianobrl.discordbots.framework.config;

public class CoreConfigurable extends AbstractConfigurable {
    public CoreConfigurable() {
        super("application");
    }

    @Override
    protected String getConfigPath() {
        return "application.yml";
    }
}
