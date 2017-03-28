package com.github.ledoyen.junit5.junitparams;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ContainerExtensionContext;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionCondition;
import org.junit.jupiter.api.extension.TestExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.engine.execution.ExecutableInvoker;
import org.junit.jupiter.engine.extension.ExtensionRegistry;
import org.junit.platform.commons.util.ReflectionUtils;

import static java.util.Collections.emptyList;

class JUnitParamsExtension implements TestTemplateInvocationContextProvider, TestExecutionCondition {

    private ExecutableInvoker executableInvoker = new ExecutableInvoker();
    private static Class<?> TEMPLATE_CONTEXT_CLASS = ReflectionUtils.loadClass("org.junit.jupiter.engine.descriptor.TestTemplateContainerExtensionContext").get();

    @Override
    public boolean supports(ContainerExtensionContext context) {
        return context.getElement().map(e -> e.isAnnotationPresent(Parameters.class)).orElse(false);
    }

    @Override
    public ConditionEvaluationResult evaluate(TestExtensionContext context) {
        boolean annotatedWithParameters = context
                .getElement()
                .filter(e -> e.isAnnotationPresent(Parameters.class))
                .map(e -> true)
                .orElse(false);

        boolean parentContextIsTemplatedOne = TEMPLATE_CONTEXT_CLASS.equals(context.getParent().get().getClass());
        return (annotatedWithParameters && !parentContextIsTemplatedOne) ? ConditionEvaluationResult.disabled("Both @Parameters and @Test annotations are present") : ConditionEvaluationResult.enabled("");
    }

    @Override
    public Stream<TestTemplateInvocationContext> provide(ContainerExtensionContext extensionContext) {

        Parameters parametersAnnotation = extensionContext.getElement().get().getAnnotation(Parameters.class);

        String[] values = parametersAnnotation.value();
        String methodAnnotation = parametersAnnotation.method();

        final List<Object> parameters;
        if (values.length != 0) {
            parameters = Arrays.stream(values).map(JUnitParamsExtension::splitAtCommaOrPipe).collect(Collectors.toList());
        } else if (!methodAnnotation.isEmpty()) {
            List<Object> result = new ArrayList<Object>();
            for (String methodName : methodAnnotation.split(",")) {
                for (Object param : getParametersUsingMethod(extensionContext.getTestClass().get(), methodName.trim(), extensionContext))
                    result.add(param);
            }

            parameters = result;
        } else {
            parameters = getParametersUsingMethod(extensionContext.getTestClass().get(), "parametersFor" + capitalizedFirstLetter(extensionContext.getTestMethod().get().getName()), extensionContext);
        }

        final Stream<TestTemplateInvocationContext> testCases;
        if (parameters.size() == 1) {
            testCases = Stream.of(buildTestTemplateInvocationContext(parameters.get(0)));
        } else {
            testCases = parameters.stream().map(JUnitParamsExtension::buildTestTemplateInvocationContext);
        }

        return testCases;
    }

    private List<Object> getParametersUsingMethod(Class<?> testClass, String methodName, ExtensionContext extensionContext) {
        Constructor<?> constructor = ReflectionUtils.getDeclaredConstructor(testClass);
        Object instance = executableInvoker.invoke(constructor, extensionContext, ExtensionRegistry.createRegistryWithDefaultExtensions(new EmptyConfigurationParameters()));

        Method paramMethod = ReflectionUtils.findMethod(testClass, methodName).get();
        paramMethod.setAccessible(true);

        final List<Object> parameters;
        try {
            Object params = paramMethod.invoke(instance);
            if (params.getClass().isArray()) {
                parameters = Arrays.asList((Object[]) params);
            } else if (Collection.class.isAssignableFrom(params.getClass())) {
                parameters = new ArrayList<>((Collection<Object>) params);
            } else if (Iterator.class.isAssignableFrom(params.getClass())) {
                parameters = new ArrayList<>();
                ((Iterator<Object>) params).forEachRemaining(parameters::add);
            } else {
                throw new IllegalArgumentException("Incompatible parameter method type: " + paramMethod.getGenericReturnType().getTypeName());
            }
            System.out.println(params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }

        return parameters;
    }

    private static TestTemplateInvocationContext buildTestTemplateInvocationContext(Object untypedParameters) {
        final List<Object> parameters;
        if (untypedParameters.getClass().isArray()) {
            parameters = Arrays.asList((Object[]) untypedParameters);
        } else if (Collection.class.isAssignableFrom(untypedParameters.getClass())) {
            parameters = new ArrayList<Object>((Collection<Object>) untypedParameters);
        } else if (Iterator.class.isAssignableFrom(untypedParameters.getClass())) {
            parameters = new ArrayList<>();
            ((Iterator<Object>) untypedParameters).forEachRemaining(parameters::add);
        } else {
            throw new IllegalArgumentException("Incompatible parameters type: " + untypedParameters.getClass().getName());
        }
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return parameters.toString();
            }

            public List<Extension> getAdditionalExtensions() {
                List<Extension> extensions = new ArrayList<>();
                for (int i = 0; i < parameters.size(); i++) {
                    extensions.add(new FixedValueParameterResolver(i, parameters.get(i)));
                }
                return extensions;
            }
        };
    }

    private static String capitalizedFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static List<String> splitAtCommaOrPipe(String input) {
        ArrayList<String> result = new ArrayList<String>();

        char character = '\0';
        char previousCharacter;

        StringBuilder value = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            previousCharacter = character;
            character = input.charAt(i);

            if (character == ',' || character == '|') {
                if (previousCharacter == '\\') {
                    value.setCharAt(value.length() - 1, character);
                    continue;
                }
                result.add(value.toString().trim());
                value = new StringBuilder();
                continue;
            }

            value.append(character);
        }
        result.add(value.toString().trim());

        return result;
    }
}
