package io.github.julianobrl.discordbots.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.julianobrl.discordbots.entities.enums.ProfileRoles;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Profile extends BaseEntity implements UserDetails {
    private String username;
    private String email;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ProfileRoles role = ProfileRoles.USER;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled = true;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of((GrantedAuthority) () -> role.toString());
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
