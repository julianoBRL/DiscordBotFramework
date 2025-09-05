package io.github.julianobrl.discordbots.framework.annotations.parameters;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SlashParameter {
    String name();
    String description();
    OptionType optionType();
    boolean required() default true;
    boolean autocomplete() default false;
}
