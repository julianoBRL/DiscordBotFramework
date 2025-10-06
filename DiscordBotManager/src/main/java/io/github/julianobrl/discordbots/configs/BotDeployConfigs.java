package io.github.julianobrl.discordbots.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class BotDeployConfigs {

    @Value("${docker.deploy.name.prefix}")
    private String deployNamePrefix;

    @Value("${docker.volume.base.path}")
    private String volumeBasePath;

    @Value("${docker.volume.plugins.path}")
    private String volumePluginsPath;

    @Value("${docker.volume.configs.path}")
    private String volumeConfigsPath;

    @Value("${docker.volume.data.path}")
    private String dataConfigsPath;

    @Value("${docker.bot.image.name}")
    private String botImageName;

    @Value("${docker.bot.image.version}")
    private String botImageVersion;

}
