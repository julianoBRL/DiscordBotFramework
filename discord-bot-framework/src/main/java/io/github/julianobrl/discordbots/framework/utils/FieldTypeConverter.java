package io.github.julianobrl.discordbots.framework.utils;

import io.github.julianobrl.discordbots.framework.exceptions.FieldParameterInjectionException;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class FieldTypeConverter {

    public static Object convertOptionToFieldType(OptionMapping option, Class<?> targetType) {

        if (targetType == String.class) {
            return option.getAsString();
        } else if (targetType == Integer.class || targetType == int.class) {
            return option.getAsInt();
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return option.getAsBoolean();
        } else if (targetType == Double.class || targetType == double.class) {
            return option.getAsDouble();
        } else if (targetType == Long.class || targetType == long.class) {
            return option.getAsLong();
        } else if (targetType == User.class) {
            return option.getAsUser();
        } else if (targetType == Member.class) {
            return option.getAsMember();
        } else if (targetType == Role.class) {
            return option.getAsRole();
        } else if (targetType == Channel.class) {
            return option.getAsChannel();
        } else if (targetType == Message.Attachment.class) {
            return option.getAsAttachment();
        } else if (targetType == IMentionable.class) {
            return option.getAsMentionable();
        }

        throw new FieldParameterInjectionException(
                "Unsupported parameter type: " + targetType.getName());
    }

    public static Object convertStringToFieldType(String value, Class<?> targetType) {
        if (value == null) return null;

        try {
            if (targetType == String.class) {
                return value;
            } else if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(value);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(value);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(value);
            } else if (targetType.isEnum()) {
                return Enum.valueOf((Class<Enum>) targetType, value.toUpperCase());
            }

            throw new FieldParameterInjectionException("Unsupported parameter type: " + targetType.getName());
        } catch (NumberFormatException e) {
            throw new FieldParameterInjectionException("Invalid value '" + value + "' for type " + targetType.getName());
        }
    }

}
