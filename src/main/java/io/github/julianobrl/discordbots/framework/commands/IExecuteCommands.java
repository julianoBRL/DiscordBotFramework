package io.github.julianobrl.discordbots.framework.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IExecuteCommands {
    default void execute(MessageReceivedEvent event){}
    default void execute(SlashCommandInteractionEvent event){}
}
