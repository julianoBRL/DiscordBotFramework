package io.github.julianobrl.demobotplugin;

import io.github.julianobrl.demobotplugin.commands.slash.TestCommand;
import io.github.julianobrl.discordbots.framework.managers.PrefixCommandsManager;
import io.github.julianobrl.discordbots.framework.managers.SlashCommandsManager;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Plugin;

@Slf4j
public class DemoPlugin extends Plugin {

    @Override
    public void start() {
        log.info("DemoPlugin-Start");
        SlashCommandsManager.getInstance().addCommand(TestCommand.class);
        PrefixCommandsManager.getInstance().addCommand(io.github.julianobrl.demobotplugin.commands.prefix.TestCommand.class);
    }

    @Override
    public void stop() {
        log.info("DemoPlugin-Stop");
    }

    @Override
    public void delete() {
        log.info("DemoPlugin-Delete");
    }

}
