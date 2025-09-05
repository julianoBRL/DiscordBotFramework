package io.github.julianobrl.discordbots.services;

import io.github.julianobrl.discordbots.entities.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BotService implements IService<Bot> {

    @Autowired
    private DockerService dockerService;

    @Override
    public List<Bot> list() {
        return dockerService.listBots();
    }

    @Override
    public List<Bot> search() {
        return List.of();
    }

    @Override
    public Bot create(Bot bot) {
        return dockerService.deployDiscordBot(bot);
    }

    @Override
    public Bot getById(String id) {
        return dockerService.getById(id);
    }

    @Override
    public Bot update(String id, Bot updated) {
        return null;
    }

    @Override
    public void delete(String id) {
        dockerService.deleteById(id);
    }

}
