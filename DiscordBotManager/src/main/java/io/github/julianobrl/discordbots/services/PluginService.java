package io.github.julianobrl.discordbots.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.julianobrl.discordbots.entities.*;
import io.github.julianobrl.discordbots.entities.enums.PluginStatus;
import io.github.julianobrl.discordbots.exceptions.PluginException;
import io.github.julianobrl.discordbots.exceptions.VersionException;
import io.github.julianobrl.discordbots.factories.PluginFactory;
import io.github.julianobrl.discordbots.repositories.PluginRepository;
import io.github.julianobrl.discordbots.services.interfaces.IService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PluginService implements IService<Plugin> {

    private final PluginRepository repository;
    private final PluginFactory factory;

    @Override
    public List<Plugin> list() {
        return repository.findAll();
    }

    public Page<Plugin> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Plugin> search() {
        return List.of();
    }

    @Override
    public Plugin create(Plugin object) {
        try {
            return repository.save(object);
        } catch (DataIntegrityViolationException e) {
            throw new PluginException("Plugin already exists!", HttpStatus.BAD_REQUEST);
        }
    }

    public Plugin createByUrl(String url) {
        Plugin foundPlugin = getManifestFromUrl(url);
        log.info("{}",foundPlugin);
        return create(foundPlugin);
    }

    @Override
    public Plugin getById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new PluginException("Plugin not found!", HttpStatus.NOT_FOUND));
    }

    @Override
    public Plugin update(String id, Plugin updated) {
        return null;
    }

    @Transactional
    @Override
    public void delete(String id) {
        Plugin plugin = getById(id);

        List<Pair<String, String>> toUninstall = plugin.getVersions().stream()
                .flatMap(version -> version.getBotsId().stream()
                        .map(botId -> Pair.of(id, botId)))
                .toList();

        toUninstall.forEach(pair -> uninstall(pair.getFirst(), pair.getSecond()));

        repository.deleteById(id);
    }

    public Plugin install(String pluginId, String botId, String versionStr){

        Plugin plugin = repository.findByIdAndVersionsVersion(pluginId, versionStr)
                .orElseThrow(()->new PluginException("Plugin not found!", HttpStatus.NOT_FOUND));

        if(isAlreadyInstalled(plugin, botId)){
            throw new PluginException("Plugin is already installed!", HttpStatus.BAD_REQUEST);
        }

        Version version = plugin.getVersions().stream()
                .filter(v -> versionStr.equals(v.getVersion()))
                .findFirst()
                .orElseThrow(()->new VersionException("Version not found!", HttpStatus.NOT_FOUND));

        plugin.getVersions().remove(version);
        plugin.getVersions().add(factory.install(version, botId));

        plugin.setStatus(PluginStatus.INSTALLED);

        return repository.saveAndFlush(plugin);
    }

    public Plugin uninstall(String pluginId, String botId) {

        Plugin plugin = getById(pluginId);

        if(!isAlreadyInstalled(plugin, botId)){
            throw new PluginException("Plugin is not installed on informed bot!", HttpStatus.BAD_REQUEST);
        }

        Version version = plugin.getVersions().stream()
                .filter(v -> v.getBotsId().contains(botId))
                .findFirst()
                .orElseThrow(()->new VersionException("Version not found!", HttpStatus.NOT_FOUND));

        plugin.getVersions().remove(version);
        plugin.getVersions().add(factory.uninstall(plugin, version, botId));

        return repository.saveAndFlush(plugin);
    }

    private Plugin getManifestFromUrl(String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            try {
                Plugin plugin = new ObjectMapper().readValue(response.getBody(), Plugin.class);
                plugin.setUrl(url);
                return plugin;
            } catch (JsonMappingException e) {
                throw new PluginException("Error while mapping repo from URL!", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (JsonProcessingException e) {
                throw new PluginException("Error while processing repo from URL!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new PluginException("Error while getting repo from URL!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean isAlreadyInstalled(Plugin plugin, String botId) {
        return plugin.getVersions().stream()
                .anyMatch(versao ->
                        versao.getBotsId().contains(botId)  // Checa se o botId está na lista de bots compatíveis
                );
    }

    public List<Plugin> getInstalledPluginsByBotId(String botId){
        return repository.findInstalledPluginsByBotsId(botId)
                .orElseThrow(()-> new PluginException("No plugins installed for this bot!", HttpStatus.NOT_FOUND));
    }

}
