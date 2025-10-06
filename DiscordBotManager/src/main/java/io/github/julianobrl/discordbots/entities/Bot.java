package io.github.julianobrl.discordbots.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dockerjava.api.model.Container;
import io.github.julianobrl.discordbots.entities.enums.BotStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bot{

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotNull
    private String name;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BotStatus status = BotStatus.INACTIVE;

    @NotNull
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String token;

    @Transient
    private String prefix;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String containerName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String containerId;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt = LocalDateTime.now();

}


