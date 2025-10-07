package io.github.julianobrl.discordbots.framework.annotations.parameters;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrefixParameter {
    String name();
    String description();
    int order();
    boolean required() default true;
}
