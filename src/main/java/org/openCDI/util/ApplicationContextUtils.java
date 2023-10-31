package org.openCDI.util;

import org.openCDI.annotations.Component;
import org.openCDI.annotations.Inject;
import org.openCDI.annotations.Service;
import org.openCDI.exceptions.validation.ClassIsAbstractException;
import org.openCDI.exceptions.validation.MultipleAnnotationException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationContextUtils {


    public static List<Field> getAllInjectableFieldsRecursively(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        getAllInjectableFieldsFromClass(clazz, fieldList);
        return fieldList;
    }

    public static void getAllInjectableFieldsFromClass(Class<?> clazz, List<Field> fieldList) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (isInjectable(field)) {
                fieldList.add(field);
            }
        }

        if (clazz.getSuperclass() != null) {
            getAllInjectableFieldsFromClass(clazz.getSuperclass(), fieldList);
        }
    }

    private static boolean isInjectable(Field field) {
        if (Modifier.isFinal(field.getModifiers())) return false;
        if (field.getType().isInterface()) return true;
        else return field.getAnnotation(Inject.class) != null;
    }



    public static void validateComponentName(String name) {
        if (name == null) throw new IllegalArgumentException("Argument is null");
        else if (name.length() == 0) throw new IllegalArgumentException("Argument is empty string");
    }

    public static boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class);
    }

    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }



    public static void validateComponent(Class<?> clazz) {
        if (isAbstract(clazz)) throw new ClassIsAbstractException("Class "+ clazz.getName() + " is abstract");
        if (!isComponent(clazz)) throw new IllegalArgumentException("Class  "+ clazz.getName() +" is not component");
        if (clazz.isAnnotationPresent(Component.class) && clazz.isAnnotationPresent(Service.class))
            throw new MultipleAnnotationException("Class annotated with Component and Service annotations simultaneously");
    }

}
