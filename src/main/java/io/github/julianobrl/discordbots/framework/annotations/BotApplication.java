package io.github.julianobrl.discordbots.framework.annotations;

import net.dv8tion.jda.api.requests.GatewayIntent;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BotApplication {
    GatewayIntent[] intents();
}
