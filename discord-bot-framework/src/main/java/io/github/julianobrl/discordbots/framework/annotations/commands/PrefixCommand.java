package io.github.julianobrl.discordbots.framework.annotations.commands;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrefixCommand {
    String name();
    String description() default "";
    boolean allowPrivateUsage() default false;
}
