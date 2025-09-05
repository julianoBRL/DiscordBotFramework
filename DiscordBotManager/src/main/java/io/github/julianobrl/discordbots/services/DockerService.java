package io.github.julianobrl.discordbots.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.*;
import io.github.julianobrl.discordbots.configs.YamlConfigManager;
import io.github.julianobrl.discordbots.entities.Bot;
import io.github.julianobrl.discordbots.entities.enums.BotStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Slf4j
@Service
public class DockerService {

    @Autowired
    private DockerClient dockerClient;

    private final String DEPLOY_NAME_PREFIX = "discord-bot-";
    private final String VOLUME_BASE_PATH = "D:\\Projetos\\Docker\\Volumes\\";
    private final String VOLUME_PLUGINS_PATH = "\\plugins";
    private final String VOLUME_CONFIGS_PATH = "\\configs";
    private final String BASE_IMAGE = "wdgaster/discord-bot-core";
    private final String BASE_IMAGE_VERSION = "1.0.0";

    public Bot deployDiscordBot(Bot bot) {

        log.info("Creating new bot: {}", bot.getName());
        String botId = UUID.randomUUID().toString().substring(0, 7);
        String deployName = DEPLOY_NAME_PREFIX + botId;
        bot.setId(botId);
        log.info("New bot[{}] id: {}", bot.getName(), deployName);

        log.info("Creating folders for: {}", deployName);
        // Define volume paths
        String volumeBasePath = VOLUME_BASE_PATH + deployName;
        String pluginsPath = volumeBasePath + VOLUME_PLUGINS_PATH;
        String configsPath = volumeBasePath + VOLUME_CONFIGS_PATH;

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
                "bot-name", bot.getName(),
                "bot-prefix", bot.getPrefix()
        );

        log.info("Pulling container image for: {}", deployName);
        dockerClient.pullImageCmd(BASE_IMAGE+":"+BASE_IMAGE_VERSION);

        log.info("Crating container for: {}", deployName);
        CreateContainerResponse container = dockerClient.createContainerCmd(BASE_IMAGE+":"+BASE_IMAGE_VERSION)
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
            throw new RuntimeException(e);
        }

        log.info("Starting bot: {}", deployName);
        dockerClient.startContainerCmd(container.getId()).exec();
        bot.setStatus(BotStatus.STARTING);

        return bot;
    }

    public List<Bot> listBots(){
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true) // Incluir containers parados
                .withLabelFilter(Map.of("discord-bot", "true")) // Filtro por label
                .exec();

        return containers.stream().map(container -> new Bot().map(container)).toList();
    }

    public Bot getById(String id){
        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true) // Incluir containers parados
                .withNameFilter(Collections.singleton("/" + DEPLOY_NAME_PREFIX + id)) // Filtro por nome exato (Docker adiciona "/" no início)
                .exec();

        if (containers.isEmpty()) {
            log.warn("Nenhum container encontrado com nome: {}", DEPLOY_NAME_PREFIX + id);
            return null; // Ou lance uma exceção, se preferir
        }

        Container container = containers.getFirst();

        return new Bot().map(container);
    }

    public void deleteById(String id){

        String name = DEPLOY_NAME_PREFIX + id;

        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true) // Incluir containers parados
                .withNameFilter(Collections.singleton("/" + name)) // Filtro por nome exato (Docker adiciona "/" no início)
                .exec();

        if (containers.isEmpty()) {
            log.warn("No container found with id: {}", name);
            return; // Ou lance uma exceção, se preferir
        }

        Container container = containers.getFirst();

        try {
            dockerClient.stopContainerCmd(container.getId()).exec();
            RemoveContainerCmd removeCmd = dockerClient.removeContainerCmd(container.getId());
            removeCmd.withForce(true).exec();
            log.info("Container [{}] deleted!.", name);

            FileUtils.deleteDirectory(new File(VOLUME_BASE_PATH+name));
            log.info("Volume for [{}] deleted!.", name);

        } catch (Exception e) {
            log.error("Erro ao deletar container: {}", e.getMessage());
        }

    }

}
