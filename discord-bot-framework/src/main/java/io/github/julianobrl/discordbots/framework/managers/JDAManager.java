package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.annotations.BotApplication;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.List;

public class JDAManager {

    private static JDAManager instance;

    @Getter
    private JDA jda;

    @Setter
    private static String token;

    @Setter
    private static Class<?> primarySource;

    private JDAManager(){
        jda = JDABuilder
                .createDefault(token)
                .enableIntents(
                        List.of(primarySource.getAnnotation(BotApplication.class).intents())
                ).build();
    }

    public static JDAManager getInstance(){
        if(instance == null){
            instance = new JDAManager();
        }
        return instance;
    }

}
