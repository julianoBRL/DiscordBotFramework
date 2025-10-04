package io.github.julianobrl.discordbots.entities;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Profiles extends BaseEntity {
    private String username;
    private String password;
    private String email;
}
