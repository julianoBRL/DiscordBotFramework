package io.github.julianobrl.discordbots.framework.plugins;

import io.github.julianobrl.discordbots.framework.annotations.BotPlugin;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Set;

@Slf4j
public class BotPluginDescriptorFinder implements PluginDescriptorFinder {

    @Override
    public boolean isApplicable(Path path) {

        try{
            Class<?> clazz = findSingleBotPluginClassInJar(path);
            if(clazz != null && clazz.isAnnotationPresent(BotPlugin.class)){
                return true;
            }
        }catch (MalformedURLException ex){
            log.error("Error while checking plugin: {}",ex.getLocalizedMessage());
            return false;
        }

        return false;
    }

    @Override
    public PluginDescriptor find(Path path) {

        try{
            Class<?> clazz = findSingleBotPluginClassInJar(path);
            if(clazz != null && clazz.isAnnotationPresent(BotPlugin.class)){
                BotPluginInfo pluginInfo = new BotPluginInfo();
                pluginInfo.fromAnnotation(clazz.getAnnotation(BotPlugin.class));
                pluginInfo.setPluginClass(clazz.getName());
                return pluginInfo;
            }
        }catch (MalformedURLException ex){
            log.error("Error while getting plugin descriptor: {}",ex.getLocalizedMessage());
            return null;
        }

        return null;
    }

    public static Class<?> findSingleBotPluginClassInJar(Path jarFilePath) throws MalformedURLException {
        File jarFile = jarFilePath.toFile();

        if (!jarFile.exists() || !jarFile.isFile()) {
            throw new IllegalArgumentException("O caminho do JAR não é válido ou o arquivo não existe: " + jarFilePath);
        }

        URL jarUrl = jarFile.toURI().toURL();
        URL[] urls = new URL[]{jarUrl};
        URLClassLoader classLoader = new URLClassLoader(urls, ClasspathHelper.contextClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader(classLoader))
                .setScanners(Scanners.TypesAnnotated)
                .addClassLoaders(classLoader));

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(BotPlugin.class);
        if (annotatedClasses.isEmpty()) {
            return null;
        } else if (annotatedClasses.size() > 1) {
            throw new IllegalStateException("Mais de uma classe com @BotPlugin encontrada no JAR: " + jarFilePath + ". Classes encontradas: " + annotatedClasses);
        } else {
            return annotatedClasses.iterator().next();
        }
    }

}
