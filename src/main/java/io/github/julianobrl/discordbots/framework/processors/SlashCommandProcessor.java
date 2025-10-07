package io.github.julianobrl.discordbots.framework.processors;

import io.github.julianobrl.discordbots.framework.annotations.commands.SlashCommand;
import io.github.julianobrl.discordbots.framework.commands.IExecuteCommands;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.julianobrl.discordbots.annotations.commands.SlashCommand"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SlashCommandProcessor extends AbstractProcessor {

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

        TypeElement iExecuteCommandsType = elementUtils.getTypeElement(IExecuteCommands.class.getName());

        if (iExecuteCommandsType == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "IExecuteCommands interface not found in classpath. Cannot validate commands.");
            return false;
        }

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(SlashCommand.class)) {
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) annotatedElement;
                checkInterfaceImplementation(classElement, iExecuteCommandsType);
            }
        }

        return false;
    }

    private void checkInterfaceImplementation(TypeElement classElement, TypeElement requiredInterfaceType) {

        TypeMirror requiredInterfaceMirror = requiredInterfaceType.asType();

        for (TypeMirror interfaceMirror : classElement.getInterfaces()) {
            if (typeUtils.isSameType(interfaceMirror, requiredInterfaceMirror)) {
                return;
            }
        }

        messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Class '" + classElement.getQualifiedName() + "' is annotated as a command " +
                        "but does not implement the '" + requiredInterfaceType.getQualifiedName() + "' interface.",
                classElement
        );
    }

}
