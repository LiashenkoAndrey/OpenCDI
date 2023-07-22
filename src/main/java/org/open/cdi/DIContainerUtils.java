package org.open.cdi;

import org.open.cdi.annotations.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DIContainerUtils {


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
        if (field.getType().isInterface()) return true;
        else return field.getAnnotation(Inject.class) != null;
    }

}
