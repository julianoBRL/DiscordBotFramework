package io.github.julianobrl.demobotplugin;

import io.github.julianobrl.demobotplugin.commands.slash.TestCommand;
import io.github.julianobrl.discordbots.framework.annotations.BotPlugin;
import io.github.julianobrl.discordbots.framework.managers.PrefixCommandsManager;
import io.github.julianobrl.discordbots.framework.managers.SlashCommandsManager;
import io.github.julianobrl.discordbots.framework.plugins.AbstractBotPlugin;

@BotPlugin(
    id = "demo",
    name = "demo",
    author = "Shintarobrl",
    version = "1.0.1",
    description = "Just a demo plugin"
)
public class DemoPlugin extends AbstractBotPlugin {

    @Override
    public void onEnable(){
        System.out.println("demo onEnable-1");
        SlashCommandsManager.getInstance().addCommand(TestCommand.class);
        System.out.println("demo onEnable-2");
        PrefixCommandsManager.getInstance().addCommand(io.github.julianobrl.demobotplugin.commands.prefix.TestCommand.class);
        System.out.println("demo onEnable-3");
    }

}
