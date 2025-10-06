package io.github.julianobrl.discordbots.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.julianobrl.discordbots.entities.enums.PluginStatus;
import io.github.julianobrl.discordbots.entities.serializables.PluginId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Plugin{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long pluginId;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(unique = true)
    private String id;
    private String name;
    private String description;

    @Column(unique = true)
    private String owner;
    private String url;
    private String logo;

    @Enumerated(EnumType.STRING)
    private PluginStatus status = PluginStatus.AVAILABLE;

    @OneToMany
    @Builder.Default
    @Cascade(CascadeType.ALL)
    private List<Version> versions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plugin that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner);
    }

}
