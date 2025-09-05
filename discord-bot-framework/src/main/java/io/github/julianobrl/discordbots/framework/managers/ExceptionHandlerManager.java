package io.github.julianobrl.discordbots.framework.managers;

import io.github.julianobrl.discordbots.framework.annotations.exceptions.ExceptionAdvicer;
import io.github.julianobrl.discordbots.framework.annotations.exceptions.PrefixExceptionHandler;
import io.github.julianobrl.discordbots.framework.annotations.exceptions.SlashExceptionHandler;
import io.github.julianobrl.discordbots.framework.exceptions.ExceptionManagerException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ExceptionHandlerManager {

    private static ExceptionHandlerManager instance;
    private static final Map<Class<? extends Throwable>, Set<Method>> exceptionHandlers = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public void loadHandlers(String basePackage){
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(basePackage)
                .setScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated));

        Set<Class<?>> advisorClasses = reflections.getTypesAnnotatedWith(ExceptionAdvicer.class);

        if(advisorClasses.isEmpty()){
            log.info("No exception advicers found!");
            return;
        }

        for (Class<?> clazz : advisorClasses) {
            addExceptionHandler(clazz);
        }
    }

    public void addExceptionHandler(Class<?> clazz){
        if (clazz.isAnnotationPresent(ExceptionAdvicer.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                Class<? extends Throwable> exceptionType = null;
                if (method.isAnnotationPresent(SlashExceptionHandler.class)) {
                    exceptionType = method.getAnnotation(SlashExceptionHandler.class).value();
                } else if (method.isAnnotationPresent(PrefixExceptionHandler.class)) {
                    exceptionType = method.getAnnotation(PrefixExceptionHandler.class).value();
                } else {
                    continue;
                }

                method.setAccessible(true);
                Set<Method> handlers = exceptionHandlers.computeIfAbsent(exceptionType, k -> Collections.synchronizedSet(new ConcurrentSkipListSet<>()));
                handlers.add(method);
            }
        }else{
            throw new ExceptionManagerException(clazz.getName()+" is no annotaded with @ExceptionAdvicer");
        }
    }

    public void handle(Throwable ex, Object event) {
        Set<Method> handlers = findHandlersForException(ex.getClass());

        if (handlers.isEmpty()) {
            ex.printStackTrace();
            return;
        }

        for (Method handlerMethod : handlers) {

            Class<?>[] parameterTypes = handlerMethod.getParameterTypes();

            if (parameterTypes.length == 2) {
                Class<?> firstParam = parameterTypes[0];
                Class<?> secondParam = parameterTypes[1];

                if (firstParam.isAssignableFrom(ex.getClass()) && secondParam.isInstance(event)) {
                    executorService.submit(() -> {
                        try {

                            Object instance = null;
                            if (java.lang.reflect.Modifier.isStatic(handlerMethod.getModifiers())) {
                                instance = null;
                            } else {
                                instance = handlerMethod.getDeclaringClass().getDeclaredConstructor().newInstance();
                            }

                            log.info("Invoking async handler: " + handlerMethod.getName());
                            handlerMethod.invoke(instance, ex, event);

                        } catch (InvocationTargetException e) {
                            log.error("Error while invoking handler " + handlerMethod.getName() + ": " + e.getTargetException().getMessage());
                            e.getTargetException().printStackTrace();
                        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                            log.error("Error while accessing/instancing/invoking handler " + handlerMethod.getName() + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private Set<Method> findHandlersForException(Class<? extends Throwable> exceptionClass) {
        Set<Method> foundHandlers = new ConcurrentSkipListSet<>();
        Class<?> currentClass = exceptionClass;

        while (currentClass != null && Throwable.class.isAssignableFrom(currentClass)) {
            Set<Method> handlers = exceptionHandlers.get(currentClass);
            if (handlers != null) {
                foundHandlers.addAll(handlers);
            }
            currentClass = currentClass.getSuperclass();
        }
        return foundHandlers;
    }

    public static ExceptionHandlerManager getInstance() {

        if (instance == null){
            instance = new  ExceptionHandlerManager();
        }
        return instance;

    }

}
