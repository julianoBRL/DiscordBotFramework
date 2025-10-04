package io.github.julianobrl.discordbots.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import io.github.julianobrl.discordbots.configs.BotDeployConfigs;
import io.github.julianobrl.discordbots.exceptions.DockerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class DockerService {

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private BotDeployConfigs botDeployConfigs;

    public List<Container> listContainers() {
        return dockerClient.listContainersCmd()
                .withShowAll(true)
                .withLabelFilter(Map.of("discord-bot", "true"))
                .exec();
    }

    public Container getContainerById(String id){
        String name = botDeployConfigs.getDeployNamePrefix() + id;

        List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .withNameFilter(Collections.singleton("/" + name))
                .exec();

        if (containers.isEmpty()) {
            log.warn("No container found with id: {}", name);
            throw new DockerException("Container not found!", HttpStatus.NOT_FOUND);
        }

        return containers.getFirst();
    }

    public Container restartContainerById(String id){
        Container container = getContainerById(id);
        if(container == null) throw new DockerException("Container not found!", HttpStatus.NOT_FOUND);
        RestartContainerCmd restartCmd = dockerClient.restartContainerCmd(container.getId());
        restartCmd.exec();
        return container;
    }

    public Container stopContainer(String id){
        Container container = getContainerById(id);
        if(container == null) throw new DockerException("Container not found!", HttpStatus.NOT_FOUND);
        StopContainerCmd stopCmd = dockerClient.stopContainerCmd(container.getId());
        stopCmd.exec();
        return container;
    }

    public Container startContainer(String id){
        Container container = getContainerById(id);
        if(container == null) throw new DockerException("Container not found!", HttpStatus.NOT_FOUND);
        StartContainerCmd startCmd = dockerClient.startContainerCmd(container.getId());
        startCmd.exec();
        return container;
    }

    public void delete(Container container) {
        dockerClient.removeContainerCmd(container.getId()).exec();
    }
}
