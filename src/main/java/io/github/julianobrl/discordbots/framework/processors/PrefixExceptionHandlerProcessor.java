package io.github.julianobrl.discordbots.framework.processors;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.julianobrl.discordbots.annotations.exceptions.PrefixExceptionHandler"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class PrefixExceptionHandlerProcessor extends AbstractProcessor {

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

        TypeElement messageEventType = elementUtils.getTypeElement("net.dv8tion.jda.api.events.message.MessageReceivedEvent");

        if (messageEventType == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "JDA's MessageReceivedEvent class not found. " +
                    "Make sure JDA is in the compile classpath for annotation processing.");
            return false;
        }
        TypeMirror messageEventTypeMirror = messageEventType.asType();


        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(
            elementUtils.getTypeElement("io.github.julianobrl.discordbots.annotations.exceptions.PrefixExceptionHandler"))) {

            if (annotatedElement.getKind() != ElementKind.METHOD) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Only methods can be annotated with @PrefixExceptionHandler", annotatedElement);
                continue;
            }

            ExecutableElement method = (ExecutableElement) annotatedElement;

            List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.size() != 2) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Method annotated with @PrefixExceptionHandler must have exactly 2 parameters.", method);
                continue;
            }

            VariableElement firstParam = parameters.getFirst();
            TypeMirror firstParamType = firstParam.asType();

            if (firstParamType.getKind() == TypeKind.DECLARED) {
                DeclaredType declaredFirstParamType = (DeclaredType) firstParamType;

                TypeElement classType = elementUtils.getTypeElement("java.lang.Class");
                if (!typeUtils.isSameType(typeUtils.asElement((TypeMirror) declaredFirstParamType.asElement()).asType(), classType.asType())) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "First parameter of @PrefixExceptionHandler method must be of type java.lang.Class.", firstParam);
                    continue;
                }

                List<? extends TypeMirror> typeArguments = declaredFirstParamType.getTypeArguments();
                if (typeArguments.size() == 1) {
                    TypeMirror genericArgument = typeArguments.get(0);
                    TypeMirror throwableType = elementUtils.getTypeElement("java.lang.Throwable").asType();

                    if (!typeUtils.isAssignable(genericArgument, throwableType)) {
                        messager.printMessage(Diagnostic.Kind.ERROR,
                                "Generic type of the first parameter (Class<?>) must extend Throwable.", firstParam);
                        continue;
                    }
                } else {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "First parameter must be of type Class<? extends Throwable> (missing generic type argument).", firstParam);
                    continue;
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "First parameter of @PrefixExceptionHandler method must be of type java.lang.Class.", firstParam);
                continue;
            }

            VariableElement secondParam = parameters.get(1);
            TypeMirror secondParamType = secondParam.asType();

            if (!typeUtils.isAssignable(secondParamType, messageEventTypeMirror)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Second parameter of @PrefixExceptionHandler method must be of type net.dv8tion.jda.api.events.message.MessageReceivedEvent.", secondParam);
                continue;
            }
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Method '" + method.getSimpleName() + "' with @PrefixExceptionHandler is valid.", method);
        }

        return false;
    }

}
