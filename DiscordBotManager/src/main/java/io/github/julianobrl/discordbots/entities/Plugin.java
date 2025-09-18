package io.github.julianobrl.discordbots.entities;

import io.github.julianobrl.discordbots.entities.enums.PluginStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plugin {
    private String pluginId;
    private String name;
    private String description;
    private String owner;
    private String logo;
    private String repo;
    private List<PluginInstall> installs;
    private List<String> versions;
    private String lastChecked;
    private PluginStatus status;

    public Plugin map(Repo repo){
        this.pluginId = repo.getId();
        this.name = repo.getName();
        this.description = repo.getDescription();
        this.owner = repo.getOwner();
        this.logo = repo.getLogo();
        this.repo = repo.getUrl();
        this.versions = new ArrayList<String>();
        this.installs = new ArrayList<PluginInstall>();
        this.status = PluginStatus.AVAILABLE;
        for(Version version: repo.getVersions()){
            versions.add(version.getVersion());
        }

        return this;
    }

}
