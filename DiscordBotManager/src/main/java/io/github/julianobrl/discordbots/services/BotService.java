package io.github.julianobrl.discordbots.services;

import io.github.julianobrl.discordbots.entities.Bot;
import io.github.julianobrl.discordbots.entities.Plugin;
import io.github.julianobrl.discordbots.entities.dtos.socket.SelfInfoDto;
import io.github.julianobrl.discordbots.factories.BotFactory;
import io.github.julianobrl.discordbots.mappers.ContainerBotMapper;
import io.github.julianobrl.discordbots.services.interfaces.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotService implements IService<Bot> {

    private final DockerService dockerService;
    private final BotFactory botFactory;
    private final PluginService pluginService;
    private final BotSocket botSocket;

    @Override
    public List<Bot> list() {
        return dockerService.listContainers().stream()
                .map(ContainerBotMapper::map).toList();
    }

    @Override
    public List<Bot> search() {
        return List.of();
    }

    @Override
    public Bot create(Bot bot) {
        return botFactory.create(bot);
    }

    @Override
    public Bot getById(String id) {
        return ContainerBotMapper.map(dockerService.getContainerById(id));
    }

    @Override
    public Bot update(String id, Bot updated) {
        return null;
    }

    @Override
    public void delete(String botId) {
        pluginService.getInstalledPluginsByBotId(botId).forEach((plugin) ->{
            pluginService.uninstall(plugin.getId(), botId);
        });
        botFactory.delete(ContainerBotMapper.map(dockerService.getContainerById(botId)));
    }

    public Bot restartBot(String id){
        return ContainerBotMapper.map(dockerService.restartContainerById(id));
    }

    public Bot stopBot(String id){
        return ContainerBotMapper.map(dockerService.stopContainer(id));
    }

    public Bot startBot(String id){
        return ContainerBotMapper.map(dockerService.startContainer(id));
    }

    public SelfInfoDto getBotInfo(String id){
        return botSocket.getBotInfo(id);
    }

}
