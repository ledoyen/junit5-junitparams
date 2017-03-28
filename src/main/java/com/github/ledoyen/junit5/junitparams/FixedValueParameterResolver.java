package com.github.ledoyen.junit5.junitparams;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.meta.API;

import static org.junit.platform.commons.meta.API.Usage.Internal;

@API(Internal)
class FixedValueParameterResolver implements ParameterResolver {

    private final int position;
    private final Object value;

    FixedValueParameterResolver(int position, Object value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getIndex() == position;
    }

    @Override
    public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return castParameterDirectly(value, parameterContext.getParameter().getType());
    }

    private Object castParameterDirectly(Object object, Class clazz) {
        if(object == null || clazz.isInstance(object) || (!(object instanceof String) && clazz.isPrimitive()))
        return object;
        if(clazz.isEnum())
        return (Enum.valueOf(clazz, (String) object));
        if(clazz.isAssignableFrom(String.class))
        return object.toString();
        if(clazz.isAssignableFrom(Integer.TYPE) || clazz.isAssignableFrom(Integer.class))
        return Integer.parseInt((String) object);
        if(clazz.isAssignableFrom(Short.TYPE) || clazz.isAssignableFrom(Short.class))
        return Short.parseShort((String) object);
        if(clazz.isAssignableFrom(Long.TYPE) || clazz.isAssignableFrom(Long.class))
        return Long.parseLong((String) object);
        if(clazz.isAssignableFrom(Float.TYPE) || clazz.isAssignableFrom(Float.class))
        return Float.parseFloat((String) object);
        if(clazz.isAssignableFrom(Double.TYPE) || clazz.isAssignableFrom(Double.class))
        return Double.parseDouble((String) object);
        if(clazz.isAssignableFrom(Boolean.TYPE) || clazz.isAssignableFrom(Boolean.class))
        return Boolean.parseBoolean((String) object);
        if(clazz.isAssignableFrom(Character.TYPE) || clazz.isAssignableFrom(Character.class))
        return object.toString().charAt(0);
        if(clazz.isAssignableFrom(Byte.TYPE) || clazz.isAssignableFrom(Byte.class))
        return Byte.parseByte((String) object);
        throw
        new IllegalArgumentException("Parameter type (" + clazz.getName() + ") cannot be handled! Only primitive types and Strings can be" +
                " used" +
                ".");
    }
}
