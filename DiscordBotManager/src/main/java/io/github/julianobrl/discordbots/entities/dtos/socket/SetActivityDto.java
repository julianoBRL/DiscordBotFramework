package io.github.julianobrl.discordbots.entities.dtos.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetActivityDto {
    private Activity.ActivityType type;
    private String name;

    public static SetActivityDto fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, SetActivityDto.class);
    }

}
