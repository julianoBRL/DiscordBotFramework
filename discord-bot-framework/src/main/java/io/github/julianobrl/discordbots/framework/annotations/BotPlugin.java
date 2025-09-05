package io.github.julianobrl.discordbots.framework.annotations;

import org.pf4j.Extension;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Extension
public @interface BotPlugin {
    String id() default "";
    String name() default "";
    String author() default "";
    String version() default "1.0.0";
    String requires() default "";
    String[] dependsOn() default {};
    String description() default "";
    String license() default "";
}
