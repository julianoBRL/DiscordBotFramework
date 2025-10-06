package io.github.julianobrl.discordbots.factories;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import io.github.julianobrl.discordbots.configs.BotDeployConfigs;
import io.github.julianobrl.discordbots.configs.YamlConfigManager;
import io.github.julianobrl.discordbots.entities.Bot;
import io.github.julianobrl.discordbots.entities.enums.BotStatus;
import io.github.julianobrl.discordbots.exceptions.BotException;
import io.github.julianobrl.discordbots.services.DockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotFactory {

    private final DockerClient dockerClient;
    private final BotDeployConfigs botDeployConfigs;
    private final DockerService dockerService;

    public Bot create(Bot bot){
        log.info("Creating new bot: {}", bot.getName());
        String botId = UUID.randomUUID().toString().substring(0, 7);
        String deployName = botDeployConfigs.getDeployNamePrefix() + botId;
        bot.setId(botId);
        log.info("New bot[{}] id: {}", bot.getName(), deployName);

        String dockerImage = botDeployConfigs.getBotImageName()+":"+botDeployConfigs.getBotImageVersion();

        // Define volume paths
        log.info("Creating folders for: {}", deployName);
        String volumeBasePath = botDeployConfigs.getVolumeBasePath() + deployName;
        String pluginsPath = volumeBasePath + botDeployConfigs.getVolumePluginsPath();
        String configsPath = volumeBasePath + botDeployConfigs.getVolumeConfigsPath();
        String dataPath = volumeBasePath + botDeployConfigs.getDataConfigsPath();

        log.info("Creating volumes for: {}", deployName);
        HostConfig hostConfig = HostConfig.newHostConfig()
            .withBinds(
                new Bind(pluginsPath, new Volume("/app/plugins")),
                new Bind(configsPath, new Volume("/app/configs")),
                new Bind(dataPath, new Volume("/app/data"))
            )
            .withRestartPolicy(RestartPolicy.unlessStoppedRestart());

        log.info("Creating labels for: {}", deployName);
        Map<String, String> labels = Map.of(
        "discord-bot", "true",
        "bot-id", botId,
        "bot-name", bot.getName()
        );

        log.info("Pulling container image for: {}", deployName);
        dockerClient.pullImageCmd(dockerImage);

        log.info("Crating container for: {}", deployName);
        CreateContainerResponse container = dockerClient.createContainerCmd(dockerImage)
            .withName(deployName)
            .withHostConfig(hostConfig)
            .withLabels(labels)
            .exec();

        log.info("Generating config file for: {}", deployName);
        YamlConfigManager yamlConfigManager = new YamlConfigManager(configsPath + "\\application.yml");
        try {
            yamlConfigManager.createKey("discord.bot.prefix", bot.getPrefix());
            yamlConfigManager.createKey("discord.bot.token", bot.getToken());
        } catch (Exception e) {
            throw new BotException("Error while creating configs for bot!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Starting bot: {}", deployName);
        dockerClient.startContainerCmd(container.getId()).exec();
        bot.setStatus(BotStatus.STARTING);

        return bot;
    }

    public void delete(Bot bot){
        Container container = dockerService.stopContainer(bot.getId());
        dockerService.delete(container);
    }

}
