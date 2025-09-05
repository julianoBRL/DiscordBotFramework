package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.annotations.events.JDAEventListener;
import io.github.julianobrl.discordbots.framework.exceptions.CommandLoaderException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Slf4j
public class EventListenersManager {

    private static EventListenersManager instance;
    private static JDA jda;

    public void loadEventListeners(JDA jda, String basePackage){
        EventListenersManager.jda = jda;
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(JDAEventListener.class);

        if (classes.isEmpty()) {
            log.info("No annotated listeners found!");
            return;
        }

        for (Class<?> clazz : classes) {
            registerEventListener((Class<? extends ListenerAdapter>) clazz);
        }

    }

    public static void registerEventListener(Class<? extends ListenerAdapter> clazz){
        try {
            ListenerAdapter listenerInstance = clazz.getDeclaredConstructor().newInstance();
            jda.addEventListener(listenerInstance);

            log.info("Registered listener: {}", clazz.getName());

        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new CommandLoaderException("Failed to instantiate listener: " + clazz.getName(), e);
        }
    }

    public static EventListenersManager getInstance() {
        if(instance == null){
            instance = new EventListenersManager();
        }
        return instance;
    }
}
