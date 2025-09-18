package io.github.julianobrl.discordbots.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dockerjava.api.model.Container;
import io.github.julianobrl.discordbots.entities.enums.BotStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class Bot {

    @NotNull
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BotStatus status = BotStatus.INACTIVE;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String token;

    private String prefix;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String containerName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String containerId;

    public Bot map(Container container) {
        this.id = container.getLabels().get("bot-id");
        this.name = container.getLabels().get("bot-name");
        this.prefix = container.getLabels().get("bot-prefix");
        this.status = BotStatus.fromDockerState(container.getState());
        this.containerName = container.getNames()[0].replaceFirst("/", "");
        this.createdAt = Instant.ofEpochSecond(container.getCreated())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return this;
    }

}


