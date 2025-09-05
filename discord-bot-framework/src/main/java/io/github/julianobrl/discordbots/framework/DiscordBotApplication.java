package io.github.julianobrl.discordbots.framework;

import io.github.julianobrl.discordbots.framework.annotations.BotApplication;
import io.github.julianobrl.discordbots.framework.exceptions.BotLoaderException;
import io.github.julianobrl.discordbots.framework.managers.*;
import io.github.julianobrl.discordbots.framework.managers.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

@Slf4j
public class DiscordBotApplication {

    private DiscordBotApplication(){}

    public static void run(Class<?> primarySource) {

        // Check starter class
        if (!primarySource.isAnnotationPresent(BotApplication.class)) {
            throw new IllegalArgumentException("Main class must be annotated with @BotApplication");
        }

        // Load configs
        ConfigManager.getInstance().loadAllConfigs(primarySource.getPackageName());

        // Load Token
        String token  = ConfigManager.getInstance().getCoreProperty("discord.bot.token");
        if (token.isEmpty()) {
            throw new BotLoaderException("Discord token not found in configuration!");
        }

        try {
            log.info("Starting Discord bot application.");

            // Config and Instantiate JDA
            JDAManager.setToken(token);
            JDAManager.setPrimarySource(primarySource);
            JDA jda = JDAManager.getInstance().getJda();

            // Load Plugins
            BotPluginManager.getInstance().loadPlugins();

            // Init and Add Event Listeners
            EventListenersManager.getInstance().loadEventListeners(jda, primarySource.getPackageName());
            jda.addEventListener(PrefixCommandsManager.getInstance());
            jda.addEventListener(SlashCommandsManager.getInstance());

            // Init and Add Exception Handlers
            ExceptionHandlerManager.getInstance().loadHandlers(primarySource.getPackageName());

            // Load Commands
            SlashCommandsManager.getInstance().loadCommands(primarySource.getPackageName());
            PrefixCommandsManager.getInstance().loadCommands(primarySource.getPackageName());

            // Enable Plugins
            BotPluginManager.getInstance().enablePlugins();

            // Update Commands
            jda.updateCommands().addCommands(
                    SlashCommandsManager.getInstance().getJDACommands()
            ).queue();

            log.info("System Ready!");

        } catch (Exception e) {
            throw new BotLoaderException("Failed to start Discord bot!",e);
        }

    }
}
