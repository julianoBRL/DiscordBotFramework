package io.github.julianobrl.discordbots.framework.plugins;

import io.github.julianobrl.discordbots.framework.annotations.BotPlugin;
import org.pf4j.ExtensionPoint;
import org.pf4j.Plugin;
import org.pf4j.PluginDependency;
import org.pf4j.PluginDescriptor;

import java.util.List;

public abstract class AbstractBotPlugin extends Plugin implements PluginDescriptor, ExtensionPoint {
    public void onEnable(){}
    public void onDisable(){}

    public String getPluginId() {
        return  this.getClass().getAnnotation(BotPlugin.class).id();
    }

    public String getPluginName() {
        return this.getClass().getAnnotation(BotPlugin.class).name();
    }

    @Override
    public String getVersion() {
        return this.getClass().getAnnotation(BotPlugin.class).version();
    }

    @Override
    public String getPluginDescription() {
        return this.getClass().getAnnotation(BotPlugin.class).description();
    }

    @Override
    public String getPluginClass() {
        return this.getClass().getName();
    }

    @Override
    public String getRequires() {
        return this.getClass().getAnnotation(BotPlugin.class).requires();
    }

    @Override
    public String getProvider() {
        return this.getClass().getAnnotation(BotPlugin.class).author();
    }

    @Override
    public String getLicense() {
        return this.getClass().getAnnotation(BotPlugin.class).license();
    }

    @Override
    public List<PluginDependency> getDependencies() {
        return List.of();
    }

}
