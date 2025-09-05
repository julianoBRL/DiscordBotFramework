package io.github.julianobrl.discordbots.framework.plugins;

import io.github.julianobrl.discordbots.framework.annotations.BotPlugin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pf4j.PluginDependency;
import org.pf4j.PluginDescriptor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BotPluginInfo implements PluginDescriptor {
    private String id = "";
    private String name = "";
    private String author = "";
    private String version = "";
    private String description = "";
    private String pluginClass = "";
    private String requires = "";
    private String provider = "";
    private String licence = "";

    public BotPluginInfo fromAnnotation(BotPlugin annotation){
        this.id = annotation.id();
        this.author = annotation.author();
        this.name = annotation.name();
        this.version = annotation.version();
        return this;
    }

    @Override
    public String getPluginId() {
        return id;
    }

    @Override
    public String getPluginDescription() {
        return description;
    }

    @Override
    public String getPluginClass() {
        return pluginClass;
    }

    @Override
    public String getRequires() {
        return requires;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getLicense() {
        return licence;
    }

    @Override
    public List<PluginDependency> getDependencies() {
        return List.of();
    }
}
