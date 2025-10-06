package io.github.julianobrl.discordbots.entities.dtos.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.SelfUser;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelfInfoDto {
    private OffsetDateTime createdAt;
    private String name;
    private String avatarUrl;
    private boolean verified;
    private boolean bot;
    private boolean system;

    public static SelfInfoDto fromSelfUser(SelfUser self){
        return SelfInfoDto.builder()
                    .createdAt(self.getTimeCreated())
                    .name(self.getGlobalName())
                    .avatarUrl(self.getAvatarUrl())
                    .verified(self.isVerified())
                    .bot(self.isBot())
                    .system(self.isSystem())
                .build();
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

}
