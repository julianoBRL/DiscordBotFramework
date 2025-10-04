package io.github.julianobrl.discordbots.factories;

import io.github.julianobrl.discordbots.configs.BotDeployConfigs;
import io.github.julianobrl.discordbots.entities.Bot;
import io.github.julianobrl.discordbots.entities.Plugin;
import io.github.julianobrl.discordbots.entities.Version;
import io.github.julianobrl.discordbots.entities.enums.PluginStatus;
import io.github.julianobrl.discordbots.exceptions.PluginException;
import io.github.julianobrl.discordbots.services.BotService;
import io.github.julianobrl.discordbots.services.DockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class PluginFactory {

    private final BotDeployConfigs botDeployConfigs;
    private final DockerService dockerService;

    public Version install(Version version, String botId){


        log.info("Preparing downloading link...");
        String[] downloadLinkSplit = version.getSourceUrl().split("/");
        Path fullPath = Path.of(botDeployConfigs.getVolumeBasePath(),
                (botDeployConfigs.getDeployNamePrefix()+botId),
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
        dockerService.restartContainerById(botId);
        version.getBotsId().add(botId);
        return version;
    }

    public Version uninstall(Plugin plugin, Version version, String botId){


        log.info("Stopping container: {}", botId);
        dockerService.stopContainer(botId);

        // Construir caminho do plugin jar a partir do volume
        try {
            String jarName = plugin.getName().toLowerCase(Locale.ROOT) + "-" + version.getVersion() + ".jar";
            // você pode ajustar o padrão do nome do jar, se precisar
            Path pluginPath = Path.of(botDeployConfigs.getVolumeBasePath(),
                    (botDeployConfigs.getDeployNamePrefix()+botId),
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
        version.getBotsId().remove(botId);

        return version;
    }

}
