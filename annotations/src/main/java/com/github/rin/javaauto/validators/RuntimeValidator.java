package com.github.rin.javaauto.validators;

import com.github.rin.javaauto.NotEmpty;

import java.lang.reflect.Field;

public class RuntimeValidator {

    public static void validate(Object obj) throws IllegalAccessException, ValidationException {
        Class<?> classObj = obj.getClass();
        Field[] fields = classObj.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(NotEmpty.class)) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    NotEmpty annotation = field.getAnnotation(NotEmpty.class);
                    throw new ValidationException(annotation.message() + ": " + field.getName());
                }
            }
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
