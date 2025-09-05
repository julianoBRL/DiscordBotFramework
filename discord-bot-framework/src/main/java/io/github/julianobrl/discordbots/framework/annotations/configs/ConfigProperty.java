package io.github.julianobrl.discordbots.framework.annotations.configs;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigProperty {
    String value();
}
