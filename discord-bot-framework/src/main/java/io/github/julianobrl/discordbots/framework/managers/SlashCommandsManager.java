package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.annotations.commands.SlashCommand;
import io.github.julianobrl.discordbots.framework.annotations.parameters.SlashParameter;
import io.github.julianobrl.discordbots.framework.commands.IExecuteCommands;
import io.github.julianobrl.discordbots.framework.commands.SlashCommandData;
import io.github.julianobrl.discordbots.framework.exceptions.CommandLoaderException;
import io.github.julianobrl.discordbots.framework.utils.ParameterInjector;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class SlashCommandsManager extends ListenerAdapter {

    private static SlashCommandsManager instance;
    private static final Map<String, SlashCommandData> slashCommands = new HashMap<>();
    List<
            net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
            > commands = new ArrayList<>();

    private SlashCommandsManager(){}

    public void loadCommands(String basePackage){
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SlashCommand.class);

        if (classes.isEmpty()) {
            log.info("No slash commands found!");
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

    public void loadCommands(String basePackage, ClassLoader classLoader){
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(basePackage, classLoader)
                .addScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated)
        );
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SlashCommand.class);

        if (classes.isEmpty()) {
            log.info("No slash commands found!");
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

    public void addCommand(Class<? extends IExecuteCommands> clazz){
        try {
            SlashCommand annotation = clazz.getAnnotation(SlashCommand.class);

            net.dv8tion.jda.api.interactions.commands.build.SlashCommandData command = Commands.slash(annotation.name(),annotation.description());
            for (Field field : clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(SlashParameter.class)){
                    SlashParameter parameter = field.getAnnotation(SlashParameter.class);
                    command.addOption(parameter.optionType(),parameter.name(),parameter.description(), parameter.required(), parameter.autocomplete());
                }
            }

            commands.add(command);

            IExecuteCommands commandInstance = clazz.getDeclaredConstructor().newInstance();
            slashCommands.put(annotation.name(), SlashCommandData.builder()
                            .baseClass(clazz)
                            .command(commandInstance)
                            .info(annotation)
                    .build());
            log.info("Slash Command: {} loaded!", annotation.name());
        } catch (Exception e) {
            throw new CommandLoaderException("Error while loading slash commands!", e);
        }
    }

    public void removeCommand(String commandName){
        slashCommands.remove(commandName);
    }

    public Map<String, SlashCommandData> getCommands(){
        return slashCommands;
    }

    public List<
            net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
            > getJDACommands(){
        return commands;
    }

    private void handle(String commandName, SlashCommandInteractionEvent event){
        try {
            IExecuteCommands command = slashCommands.get(commandName).getCommand();
            ParameterInjector.injectParameters(command, event);
            command.execute(event);
        } catch (Exception ex){
            ExceptionHandlerManager.getInstance().handle(ex, event);
        }
    }

    public static SlashCommandsManager getInstance(){
        if(instance == null)
            instance = new SlashCommandsManager();
        return instance;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        handle(event.getName(), event);
    }


}
