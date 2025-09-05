package io.github.julianobrl.demobotplugin.commands.prefix;

import io.github.julianobrl.discordbots.framework.annotations.commands.PrefixCommand;
import io.github.julianobrl.discordbots.framework.commands.IExecuteCommands;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@PrefixCommand(
        name = "test"
)
public class TestCommand implements IExecuteCommands {

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().asTextChannel().sendMessage("Command added by a plugin!").queue();
    }

}
