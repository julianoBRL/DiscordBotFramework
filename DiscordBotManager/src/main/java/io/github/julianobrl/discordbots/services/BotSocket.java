package io.github.julianobrl.discordbots.services;

import io.github.julianobrl.discordbots.entities.dtos.socket.CommandDTO;
import io.github.julianobrl.discordbots.entities.dtos.socket.SelfInfoDto;
import io.github.julianobrl.discordbots.factories.SocketMessenger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotSocket {

    private final SocketMessenger messenger;

    public SelfInfoDto getBotInfo(String botId){
        return messenger.sendMessage(botId, new CommandDTO("SELF", null), SelfInfoDto.class);
    }

}
