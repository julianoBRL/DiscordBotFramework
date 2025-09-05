package io.github.julianobrl.discordbots.framework.processors;

import io.github.julianobrl.discordbots.framework.annotations.BotPlugin;
import io.github.julianobrl.discordbots.framework.plugins.AbstractBotPlugin;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.julianobrl.discordbots.annotations.BotPlugin"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BotPluginProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement listenerAdapterTypeElement = elementUtils.getTypeElement(AbstractBotPlugin.class.getName());

        if (listenerAdapterTypeElement == null) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "JDA's AbstractBotPlugin class not found in classpath. " +
                            "Make sure AbstractBotPlugin is in the compile classpath for annotation processing.", null);
            return false;
        }

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(BotPlugin.class)) {
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) annotatedElement;
                checkClassExtension(classElement, listenerAdapterTypeElement);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Only classes can be annotated with @BotPlugin.", annotatedElement);
            }
        }

        return false;
    }

    /**
     * Verifica se a classe dada estende a superclasse requerida.
     *
     * @param classElement A classe a ser verificada.
     * @param requiredSuperClassType A superclasse que a classeElement deve estender.
     */
    private void checkClassExtension(TypeElement classElement, TypeElement requiredSuperClassType) {
        if (typeUtils.isSubtype(classElement.asType(), requiredSuperClassType.asType())) {
            return;
        }

        messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Class '" + classElement.getQualifiedName() + "' is annotated as a @BotPlugin " +
                        "but does not extend the '" + requiredSuperClassType.getQualifiedName() + "' class.",
                classElement
        );
    }

}
