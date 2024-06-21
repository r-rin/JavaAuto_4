package com.github.rin.javaauto;

import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes("com.github.rin.javaauto.StorageService")
public class StorageServiceProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(StorageService.class)) {
            if (element instanceof TypeElement) {
                generateService((TypeElement) element);
            } else {
                messager.printMessage(
                        Diagnostic.Kind.ERROR, 
                        "StorageService annotation can be applied only to classes",
                        element);
            }
        }
        
        return true;
    }

    private void generateService(TypeElement element) {
        String className = element.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();

        if (!implementsInterface(element, "com.github.rin.javaauto.Identifiable")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    className + " must implement Identifiable interface", element);
            return;
        }

        String storageClassName = className + "Service";
        ClassName classType = ClassName.get(packageName, className);
        ClassName serviceType = ClassName.get(packageName, storageClassName);

        MethodSpec getInstanceMethod = MethodSpec.methodBuilder("getInstance")
                .returns(serviceType)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .beginControlFlow("if ($N == null)", "instance")
                .addStatement("$N = new $T()", "instance", serviceType)
                .endControlFlow()
                .addStatement("return $N", "instance")
                .build();

        MethodSpec addMethod = MethodSpec.methodBuilder("add" + className)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(classType, "book")
                .addStatement("items.add(book)")
                .build();

        MethodSpec removeMethod = MethodSpec.methodBuilder("remove" + className)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(UUID.class, "uuid")
                .addStatement("$T itemToRemove = find" + className + "ByUUID(uuid)", classType)
                .beginControlFlow("if (itemToRemove != null)")
                .addStatement("items.remove(itemToRemove)")
                .endControlFlow()
                .build();

        MethodSpec getMethod = MethodSpec.methodBuilder("get" + className + "s")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), classType))
                .addStatement("return items")
                .build();

        MethodSpec findMethod = MethodSpec.methodBuilder("find" + className + "ByUUID")
                .addModifiers(Modifier.PRIVATE)
                .returns(classType)
                .addParameter(UUID.class, "uuid")
                .addStatement("return items.stream().filter(item -> item.getId().equals(uuid)).findFirst().orElse(null)")
                .build();

        TypeSpec serviceClass = TypeSpec.classBuilder(serviceType)
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class), classType), "items", Modifier.PRIVATE)
                        .initializer("new $T<>()", ArrayList.class)
                        .build())
                .addField(FieldSpec.builder(serviceType, "instance", Modifier.PRIVATE, Modifier.STATIC)
                        .build())
                .addMethod(getInstanceMethod)
                .addMethod(removeMethod)
                .addMethod(getMethod)
                .addMethod(addMethod)
                .addMethod(findMethod)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, serviceClass)
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private boolean implementsInterface(TypeElement classElement, String interfaceName) {
        for (TypeMirror interfaceType : classElement.getInterfaces()) {
            if (interfaceType.toString().equals(interfaceName)) {
                return true;
            }
        }
        return false;
    }
}
