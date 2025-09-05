package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.annotations.commands.PrefixCommand;
import io.github.julianobrl.discordbots.framework.commands.IExecuteCommands;
import io.github.julianobrl.discordbots.framework.commands.PrefixCommandData;
import io.github.julianobrl.discordbots.framework.exceptions.CommandLoaderException;
import io.github.julianobrl.discordbots.framework.utils.ParameterInjector;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;

import java.util.*;

@Slf4j
public class PrefixCommandsManager extends ListenerAdapter {
    private final String prefix;
    private static PrefixCommandsManager instance;
    private static final Map<String, PrefixCommandData> prefixCommands = new HashMap<>();

    private PrefixCommandsManager() {
        this.prefix = ConfigManager.getInstance().getCoreProperty("discord.bot.prefix");
    }

    public void loadCommands(String basePackage){
        if(this.prefix == null || this.prefix.isEmpty() || this.prefix.isBlank()) {
            log.info("Prefix not defined, prefix commands disabled!");
            return;
        }

        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(PrefixCommand.class);

        if (classes.isEmpty()) {
            log.info("Prefix enabled but no prefix commands found!");
        } else {
            for (Class<?> clazz : classes) {
                if (IExecuteCommands.class.isAssignableFrom(clazz)) {
                    addCommand((Class<? extends IExecuteCommands>) clazz);
                }else{
                    throw new CommandLoaderException("Command "+clazz.getCanonicalName()+" does not implements IExecuteCommands!");
                }
            }
        }

    }

    public void addCommand(Class<? extends IExecuteCommands> command){
        try {
            PrefixCommand annotation = command.getAnnotation(PrefixCommand.class);

            log.info("Prefix Command: {} loaded!", annotation.name());
            IExecuteCommands commandInstance = command.getDeclaredConstructor().newInstance();
            prefixCommands.put(annotation.name(), PrefixCommandData.builder()
                            .command(commandInstance)
                            .info(annotation)
                            .baseClass(command)
                    .build());
        } catch (Exception e) {
            throw new CommandLoaderException("Error while loading prefix commands!", e);
        }
    }

    public void removeCommand(String commandName){
        prefixCommands.remove(commandName);
    }

    public Map<String, PrefixCommandData> getCommands(){
        return prefixCommands;
    }

    private void handle(String commandName, MessageReceivedEvent event){
        try{
            PrefixCommandData commandData = prefixCommands.get(commandName);
            if (commandData != null) {
                IExecuteCommands command = commandData.getCommand();
                ParameterInjector.injectParameters(command, event);
                command.execute(event);
            }else{
                event.getChannel().asTextChannel().sendMessage(":x: Comando não encontrado.").queue();
            }
        } catch (Exception ex){
            ExceptionHandlerManager.getInstance().handle(ex, event);
        }
    }

    public static PrefixCommandsManager getInstance(){
        if(instance == null)
            instance = new PrefixCommandsManager();
        return instance;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (prefix != null) {
            String content = event.getMessage().getContentRaw();
            if (content.startsWith(prefix)) {
                String commandName = content.split(" ")[0].substring(prefix.length());
                handle(commandName,event);
            }
        }
    }
}
