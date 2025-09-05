package io.github.julianobrl.demobotplugin.commands.slash;

import io.github.julianobrl.discordbots.framework.annotations.commands.SlashCommand;
import io.github.julianobrl.discordbots.framework.commands.IExecuteCommands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommand(
        name = "test",
        description = "Just a test command"
)
public class TestCommand implements IExecuteCommands {

    @Override
    public void execute(SlashCommandInteractionEvent event){
        event.reply("This command was added by a plugin!").queue();
    }

}
