package io.github.julianobrl.discordbots.entities.serializables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginId implements Serializable {
    private String id;
    private String owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PluginId that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner);
    }
}
