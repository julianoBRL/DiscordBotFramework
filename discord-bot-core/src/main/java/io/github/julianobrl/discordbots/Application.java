package io.github.julianobrl.discordbots;

import io.github.julianobrl.discordbots.framework.DiscordBotApplication;
import io.github.julianobrl.discordbots.framework.annotations.BotApplication;
import net.dv8tion.jda.api.requests.GatewayIntent;

@BotApplication(
    intents = {
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.MESSAGE_CONTENT
    }
)
public class Application {
    public static void main(String[] args) {
        DiscordBotApplication.run(Application.class);
    }
}
