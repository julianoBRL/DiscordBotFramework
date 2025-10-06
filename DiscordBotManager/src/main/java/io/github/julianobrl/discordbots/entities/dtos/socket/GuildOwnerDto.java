package io.github.julianobrl.discordbots.entities.dtos.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildOwnerDto {
    private String name;
    private String avatarUrl;
    private String nickname;
    private boolean owner;
    private boolean boosting;

    public static GuildOwnerDto fromMember(Member member){
        if(member == null){
            return null;
        }
        return GuildOwnerDto.builder()
                .name(member.getEffectiveName())
                .nickname(member.getNickname())
                .avatarUrl(member.getAvatarUrl())
                .owner(member.isOwner())
                .boosting(member.isBoosting())
                .build();
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

}
