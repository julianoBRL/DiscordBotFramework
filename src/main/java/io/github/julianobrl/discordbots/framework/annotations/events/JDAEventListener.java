package io.github.julianobrl.discordbots.framework.annotations.events;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface JDAEventListener {
}
