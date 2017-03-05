package com.github.slick;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;

import static com.github.slick.SlickProcessor.ClASS_NAME_HASH_MAP;
import static com.github.slick.SlickProcessor.ClASS_NAME_STRING;

/**
 * @author : Pedramrn@gmail.com
 *         Created on: 2017-02-05
 */
class PresenterGeneratorActivityImpl extends BasePresenterGeneratorImpl {
    public PresenterGeneratorActivityImpl(MethodSignatureGenerator generator) {
        super(generator);
    }

    @Override
    protected MethodSpec.Builder bindMethodBody(MethodSpec.Builder builder, AnnotatedPresenter ap, ClassName view,
                                                ClassName presenter,
                                                ClassName presenterHost,
                                                ClassName classNameDelegate,
                                                String fieldName, String argNameView,
                                                String presenterArgName, TypeVariableName viewGenericType,
                                                ParameterizedTypeName typeNameDelegate, String argsCode) {
        builder.addStatement("final String id = $T.getActivityId($L)", classNameDelegate, argNameView)
                .addStatement("if ($L == null) $L = new $T()", hostInstanceName, hostInstanceName, presenterHost)
                .addStatement("$T $L = $L.$L.get(id)", typeNameDelegate, varNameDelegate, hostInstanceName,
                        fieldNameDelegates)
                .beginControlFlow("if ($L == null)", varNameDelegate);
        return presenterInstantiating(builder, ap)
                .addStatement("$L = new $T<>($L, $L.getClass(), id)", varNameDelegate, ap.getDelegateType(),
                        presenterName, argNameView)
                .addStatement("$L.setListener($L)", varNameDelegate, hostInstanceName)
                .addStatement("$L.$L.put(id, $L)", hostInstanceName, fieldNameDelegates, varNameDelegate)
                .addStatement("$L.getApplication().registerActivityLifecycleCallbacks(delegate)", argNameView)
                .endControlFlow()
                .addStatement("(($L) $L).$L = $L.getPresenter()", view.simpleName(), argNameView, fieldName, varNameDelegate);

    }

    protected MethodSpec.Builder presenterInstantiating(MethodSpec.Builder builder, AnnotatedPresenter ap) {
        return builder.addStatement("final $T $L = new $T($L)", ap.getPresenter(), presenterName,
                ap.getPresenter(), ap.getArgsAsString());
    }

    @Override
    protected FieldSpec getDelegateField(ParameterizedTypeName typeNameDelegate, AnnotatedPresenter ap) {
        final ParameterizedTypeName parametrizedMapTypeName =
                ParameterizedTypeName.get(ClASS_NAME_HASH_MAP, ClASS_NAME_STRING, typeNameDelegate);

        return FieldSpec.builder(parametrizedMapTypeName, fieldNameDelegates)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T<>()", ClASS_NAME_HASH_MAP)
                .build();
    }
}
