package io.github.julianobrl.discordbots.framework.annotations.commands;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SlashCommand {
    String name();
    String description();
    boolean allowPrivateUsage() default false;
}
