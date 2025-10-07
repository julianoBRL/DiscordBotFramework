package io.github.julianobrl.discordbots.framework.config;

import io.github.julianobrl.discordbots.framework.exceptions.ConfigLoaderException;

public interface IConfigurable {
    String getId();
    void loadConfigurations() throws ConfigLoaderException;
    String getProperty(String key);
    String getProperty(String key, String defaultValue);
}
