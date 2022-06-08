package net.suparking.chargeserver.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JasonMapper extends ObjectMapper {
    public JavaType getType(Class<?> parametrized, Class... parameterClasses) {
        return _typeFactory.constructParametricType(parametrized, parameterClasses);
    }
}
