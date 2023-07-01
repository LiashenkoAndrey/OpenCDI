package org.open.cdi;

import org.open.cdi.annotations.InjectBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DIContainerUtils {

    public static InjectBean getDIAnnotation(Field field) {
        Annotation[] annotations = field.getAnnotationsByType(InjectBean.class);
        if (annotations.length != 0) {
           return (InjectBean) annotations[0];
        } else return null;
    }

    public List<Field> getAllInjectableFieldsRecursively(Class clazz) {
        List<Field> fieldList = new ArrayList<>();
        getAllInjectableFieldsFromClass(clazz, fieldList);
        return fieldList;
    }

    private void getAllInjectableFieldsFromClass(Class clazz, List<Field> fieldList) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            InjectBean injectBean = getDIAnnotation(field);
            if (injectBean != null) {
                fieldList.add(field);
            }
        }

        if (clazz.getSuperclass() != null) {
            getAllInjectableFieldsFromClass(clazz.getSuperclass(), fieldList);
        }
    }

    public static String parseClassNameFromClassToString(String path) {
        String[] arr = path.split("\\.");
        return arr[arr.length-1];
    }
}
