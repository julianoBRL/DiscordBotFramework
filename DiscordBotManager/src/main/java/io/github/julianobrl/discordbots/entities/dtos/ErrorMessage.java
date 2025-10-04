package io.github.julianobrl.discordbots.entities.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ErrorMessage {
    private HttpStatus status;
    private String message;
}
