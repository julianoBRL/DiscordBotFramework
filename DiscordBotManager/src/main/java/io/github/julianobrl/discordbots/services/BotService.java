package io.github.julianobrl.discordbots.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.*;
import io.github.julianobrl.discordbots.configs.BotDeployConfigs;
import io.github.julianobrl.discordbots.configs.YamlConfigManager;
import io.github.julianobrl.discordbots.entities.Bot;
import io.github.julianobrl.discordbots.entities.enums.BotStatus;
import io.github.julianobrl.discordbots.exceptions.BotException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class BotService implements IService<Bot> {

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private BotDeployConfigs botDeployConfigs;

    @Autowired
    private DockerService dockerService;

    @Override
    public List<Bot> list() {
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true) // Incluir containers parados
                .withLabelFilter(Map.of("discord-bot", "true")) // Filtro por label
                .exec();

        return containers.stream().map(container -> new Bot().map(container)).toList();
    }

    @Override
    public List<Bot> search() {
        return List.of();
    }

    @Override
    public Bot create(Bot bot) {
        log.info("Creating new bot: {}", bot.getName());
        String botId = UUID.randomUUID().toString().substring(0, 7);
        String deployName = botDeployConfigs.getDeployNamePrefix() + botId;
        bot.setId(botId);
        log.info("New bot[{}] id: {}", bot.getName(), deployName);

        String dockerImage = botDeployConfigs.getBotImageName()+":"+botDeployConfigs.getBotImageVersion();

        log.info("Creating folders for: {}", deployName);
        // Define volume paths
        String volumeBasePath = botDeployConfigs.getVolumeBasePath() + deployName;
        String pluginsPath = volumeBasePath + botDeployConfigs.getVolumePluginsPath();
        String configsPath = volumeBasePath + botDeployConfigs.getVolumeConfigsPath();

        log.info("Creating volumes for: {}", deployName);
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withBinds(
                        new Bind(pluginsPath, new Volume("/app/plugins")),
                        new Bind(configsPath, new Volume("/app/configs"))
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

    @Override
    public Bot getById(String id) {
        Container container = dockerService.getContainerById(id);
        if(container == null) throw new BotException("Bot not found!", HttpStatus.NOT_FOUND);
        return new Bot().map(container);
    }

    @Override
    public Bot update(String id, Bot updated) {
        return null;
    }

    @Override
    public void delete(String id) {
        Container container = dockerService.getContainerById(id);
        if(container == null) throw new BotException("Bot not found!", HttpStatus.NOT_FOUND);
        String name = botDeployConfigs.getDeployNamePrefix() + id;

        try {
            dockerClient.stopContainerCmd(container.getId()).exec();
            RemoveContainerCmd removeCmd = dockerClient.removeContainerCmd(container.getId());
            removeCmd.withForce(true).exec();
            log.info("Container [{}] deleted!.", name);

            FileUtils.deleteDirectory(new File(botDeployConfigs.getVolumeBasePath()+name));
            log.info("Volume for [{}] deleted!.", name);

        } catch (IOException e) {
            throw new BotException("Error while deleting bot!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
