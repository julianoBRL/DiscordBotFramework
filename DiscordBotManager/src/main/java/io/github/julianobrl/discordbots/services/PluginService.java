package io.github.julianobrl.discordbots.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.julianobrl.discordbots.configs.BotDeployConfigs;
import io.github.julianobrl.discordbots.entities.*;
import io.github.julianobrl.discordbots.entities.enums.PluginStatus;
import io.github.julianobrl.discordbots.exceptions.BotException;
import io.github.julianobrl.discordbots.exceptions.PluginException;
import io.github.julianobrl.discordbots.exceptions.RepoException;
import io.github.julianobrl.discordbots.exceptions.VersionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class PluginService {

    @Autowired
    private BotDeployConfigs botDeployConfigs;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private BotService botService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Repo getManifestFromUrl(String url) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            try {
                Repo repo = objectMapper.readValue(response.getBody(), Repo.class);
                repo.setUrl(url);
                return repo;
            } catch (JsonMappingException e) {
                throw new RepoException("Error while mapping repo from URL!", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (JsonProcessingException e) {
                throw new RepoException("Error while processing repo from URL!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new RepoException("Error while getting repo from URL!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Path getPluginsFilePath() {
        return Path.of(botDeployConfigs.getVolumeBasePath(), "plugins.json");
    }

    private Version getVersion(Repo repo, String version){
        return repo.getVersions().stream()
                .filter(v -> v.getVersion().equals(version))
                .findFirst()
                .orElseThrow(() -> new VersionException("Version not found!", HttpStatus.NOT_FOUND));
    }

    public List<Plugin> readPlugins() {
        try {
            File file = getPluginsFilePath().toFile();
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<>() {});
        } catch (Exception e) {
            throw new PluginException("Error while reading plugins.json", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void writePlugins(List<Plugin> plugins) {
        try {
            File file = getPluginsFilePath().toFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, plugins);
        } catch (Exception e) {
            throw new PluginException("Error while writing in plugins.json", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Plugin add(String url) {
        log.info("Adding new plugin: {}",url);
        List<Plugin> plugins = readPlugins();
        Repo repo = getManifestFromUrl(url);
        Plugin newplugin = new Plugin().map(repo);
        plugins.removeIf(p -> p.getId().equals(newplugin.getId()));
        plugins.add(newplugin);
        writePlugins(plugins);
        return newplugin;
    }

    public Plugin getById(String pluginId){
        log.info("Getting plugin by id: {}",pluginId);
        return readPlugins().stream()
                .filter(p -> p.getId().equals(pluginId))
                .findFirst()
                .orElseThrow(() -> new PluginException("Plugin not found!", HttpStatus.NOT_FOUND));
    }

    public Plugin delete(String pluginId) {
        List<Plugin> plugins = readPlugins();
        Plugin plugin = plugins.stream()
                .filter(p -> p.getId().equals(pluginId))
                .findFirst()
                .orElseThrow(() -> new PluginException("Plugin not found!", HttpStatus.NOT_FOUND));

        if (plugin == null) {
            throw new PluginException("Plugin not found!", HttpStatus.NOT_FOUND);
        }

        boolean inUse = plugin.getInstalls() != null &&
                plugin.getInstalls().stream()
                        .anyMatch(install -> install.getBots() != null && !install.getBots().isEmpty());

        if (inUse) {
            throw new PluginException("Unable to delete plugin (in use)!", HttpStatus.BAD_REQUEST);
        }

        // Ninguém usando, pode remover
        plugins.remove(plugin);
        writePlugins(plugins);
        plugin.setStatus(PluginStatus.DELETED);
        return plugin;
    }

    public Plugin uninstall(String pluginId, String botId) {
        log.info("Uninstalling plugin: {}", pluginId);
        Plugin plugin = getById(pluginId);
        if (plugin == null) throw new PluginException("Plugin not found!", HttpStatus.NOT_FOUND);

        log.info("Locating bot: {}", botId);
        Bot bot = botService.getById(botId);
        if (bot == null) throw new BotException("Bot not found!", HttpStatus.NOT_FOUND);

        List<PluginInstall> installs = plugin.getInstalls();
        if (installs == null || installs.isEmpty()) return plugin;

        // Encontrar o install referente a esse bot
        PluginInstall targetInstall = null;
        for (PluginInstall install : installs) {
            if (install.getBots().contains(botId)) {
                targetInstall = install;
                break;
            }
        }

        if (targetInstall == null) {
            log.warn("No installation found for plugin {} on bot {}", pluginId, botId);
            throw new PluginException("No installation found for plugin ["+pluginId+"] on bot ["+botId+"]!", HttpStatus.NOT_FOUND);
        }

        log.info("Stopping container: {}", botId);
        dockerService.stopContainer(botId);

        // Construir caminho do plugin jar a partir do volume
        try {
            String jarName = plugin.getName().toLowerCase(Locale.ROOT) + "-" + targetInstall.getVersion() + ".jar";
            // você pode ajustar o padrão do nome do jar, se precisar
            Path pluginPath = Path.of(botDeployConfigs.getVolumeBasePath(),
                    bot.getContainerName(),
                    botDeployConfigs.getVolumePluginsPath(),
                    jarName);

            if (Files.exists(pluginPath)) {
                log.info("Deleting plugin jar: {}", pluginPath);
                Files.delete(pluginPath);
            } else {
                log.warn("Plugin jar not found: {}", pluginPath);
                throw new PluginException("Plugin jar not found ["+pluginPath+"]!", HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            throw new PluginException("Error deleting plugin jar", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Starting container: {}", botId);
        dockerService.startContainer(botId);

        log.info("Updating plugin registry...");
        // Remover o bot da lista
        targetInstall.getBots().remove(botId);
        if (targetInstall.getBots().isEmpty()) {
            installs.remove(targetInstall);
        }

        if (installs.isEmpty()) {
            plugin.setStatus(PluginStatus.AVAILABLE);
        }

        List<Plugin> plugins = readPlugins();
        plugins.removeIf(p -> p.getId().equals(plugin.getId()));
        plugins.add(plugin);
        writePlugins(plugins);

        return plugin;
    }

    public Plugin install(String pluginId, String botId, String versionStr) throws JsonProcessingException {

        log.info("Installing plugin: {}",pluginId);
        Plugin plugin = getById(pluginId);
        if(plugin == null) throw new PluginException("Plugin not found!", HttpStatus.BAD_REQUEST);

        log.info("Locating bot: {}", botId);
        Bot bot = botService.getById(botId);
        if(bot == null) throw new BotException("Bot not found!", HttpStatus.BAD_REQUEST);

        log.info("Getting plugin repo...");
        Repo repo = getManifestFromUrl(plugin.getRepo());

        log.info("Getting repo version...");
        Version version = getVersion(repo, versionStr);
        if(version == null) throw new VersionException("Version not found!", HttpStatus.BAD_REQUEST);

        log.info("Preparing downloading link...");
        String[] downloadLinkSplit = version.getSourceUrl().split("/");
        Path fullPath = Path.of(botDeployConfigs.getVolumeBasePath(),
                                bot.getContainerName(),
                                botDeployConfigs.getVolumePluginsPath(),
                                downloadLinkSplit[downloadLinkSplit.length - 1]);

        log.info("Downloading plugin...");
        try {
            Files.createDirectories(fullPath.getParent());
            URI uri = new URI(version.getSourceUrl());
            try (InputStream in = uri.toURL().openStream()) {
                Files.copy(in, fullPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new PluginException("Error while downloading plugin!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Download complete.");
        } catch (URISyntaxException | IOException e) {
            throw new PluginException("Error while downloading plugin!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("Restarting docker.");
        dockerService.restartById(botId);

        log.info("Updating plugin registry.");
        return addOrUpdateInstall(plugin, versionStr, botId);

    }

    private Plugin addOrUpdateInstall(Plugin plugin, String version, String botId) {
        List<PluginInstall> installs = plugin.getInstalls();
        if (installs == null) {
            installs = new ArrayList<>();
            plugin.setInstalls(installs);
        }

        boolean found = false;

        for (PluginInstall install : installs) {
            if (install.getVersion().equals(version)) {
                if (!install.getBots().contains(botId)) {
                    install.getBots().add(botId);
                }
                found = true;
                break;
            }
        }

        if (!found) {
            List<String> bots = new ArrayList<>();
            bots.add(botId);
            PluginInstall newInstall = new PluginInstall();
            newInstall.setVersion(version);
            newInstall.setBots(bots);
            installs.add(newInstall);
        }

        plugin.setStatus(PluginStatus.INSTALLED);
        List<Plugin> plugins = readPlugins();
        plugins.removeIf(p -> p.getId().equals(plugin.getId()));
        plugins.add(plugin);
        writePlugins(plugins);
        return plugin;
    }

}
