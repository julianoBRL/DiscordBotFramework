package io.github.julianobrl.discordbots.entities.dtos;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
