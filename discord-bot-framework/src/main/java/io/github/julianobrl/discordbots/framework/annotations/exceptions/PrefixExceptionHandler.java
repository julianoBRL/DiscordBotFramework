package io.github.julianobrl.discordbots.framework.annotations.exceptions;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrefixExceptionHandler {
    Class<? extends Throwable> value();
}
