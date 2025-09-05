package io.github.julianobrl.discordbots.framework.utils;

import io.github.julianobrl.discordbots.framework.annotations.parameters.PrefixParameter;
import io.github.julianobrl.discordbots.framework.annotations.parameters.SlashParameter;
import io.github.julianobrl.discordbots.framework.commands.IExecuteCommands;
import io.github.julianobrl.discordbots.framework.exceptions.FieldParameterInjectionException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class ParameterInjector {

    public static void injectParameters(IExecuteCommands command, SlashCommandInteractionEvent event) {
        for (Field field : command.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(SlashParameter.class)) {
                SlashParameter slashParameter = field.getAnnotation(SlashParameter.class);
                try {

                    OptionMapping option = event.getOption(slashParameter.name());
                    if (option == null && slashParameter.required()) {
                        throw new FieldParameterInjectionException(
                                "Required parameter [" + slashParameter.name() + "] not provided");
                    }

                    Object value = FieldTypeConverter.convertOptionToFieldType(option, field.getType());

                    field.setAccessible(true);
                    field.set(command, value);

                } catch (IllegalAccessException e) {
                    throw new FieldParameterInjectionException(
                            "Error while injecting parameter [" + field.getName() +
                                    "] from command [" + event.getCommandString() + "]", e);
                }
            }
        }
    }

    public static void injectParameters(IExecuteCommands command, MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        Map<Integer, Field> orderedFields = new TreeMap<>();

        for (Field field : command.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrefixParameter.class)) {
                PrefixParameter param = field.getAnnotation(PrefixParameter.class);
                orderedFields.put(param.order(), field);
            }
        }

        for (Map.Entry<Integer, Field> entry : orderedFields.entrySet()) {
            int paramOrder = entry.getKey();
            Field field = entry.getValue();
            PrefixParameter param = field.getAnnotation(PrefixParameter.class);

            try {
                Object value = null;
                if (args.length > paramOrder) {
                    value = FieldTypeConverter.convertStringToFieldType(args[paramOrder], field.getType());
                } else if (param.required()) {
                    throw new FieldParameterInjectionException(
                            "Missing required parameter at position " + paramOrder + " for field " + field.getName());
                }

                // Injeta o valor (pode ser null se o parâmetro for opcional)
                field.setAccessible(true);
                field.set(command, value);

            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new FieldParameterInjectionException(
                        "Error injecting parameter " + field.getName(), e);
            }
        }
    }


}
