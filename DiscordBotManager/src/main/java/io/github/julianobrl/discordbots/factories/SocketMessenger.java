package io.github.julianobrl.discordbots.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.julianobrl.discordbots.configs.BotDeployConfigs;
import io.github.julianobrl.discordbots.entities.dtos.socket.CommandDTO;
import io.github.julianobrl.discordbots.exceptions.BotException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketMessenger {

    private final ObjectMapper mapper;
    private final BotDeployConfigs deployConfigs;

    public <T> T sendMessage(String botId, CommandDTO cmd, Class<T> responseClass){
        try {
            UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketPathBuilder(botId));

            // Abre o canal e conecta
            try (SocketChannel clientChannel = SocketChannel.open(address)) {

                ByteBuffer buffer = ByteBuffer.wrap(mapper.writeValueAsString(cmd).getBytes());
                clientChannel.write(buffer);
                log.info("Mensagem enviada. Aguardando resposta...");

                // Recebe a resposta
                ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
                int bytesRead = clientChannel.read(responseBuffer);

                if (bytesRead > 0) {
                    String response = new String(responseBuffer.array(), 0, bytesRead).trim();
                    log.info("Resposta do Servidor: {}", response);
                    return mapper.readValue(response, responseClass);
                }

                return null;

            }

        } catch (Exception e) {
            throw new BotException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    private Path socketPathBuilder(String botId){
        String deployName = deployConfigs.getDeployNamePrefix() + botId;
        Path volumePath = Paths.get(deployConfigs.getVolumeBasePath(), deployName);
        Path dataPath = volumePath.resolve(deployConfigs.getDataConfigsPath());
        return dataPath.resolve("bot.socket");
    }

}
