package io.github.julianobrl.discordbots.entities.dtos.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandDTO {
    private String command;
    private Object data;
}
