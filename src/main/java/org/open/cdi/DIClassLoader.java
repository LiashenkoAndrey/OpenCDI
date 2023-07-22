package org.open.cdi;

import org.open.cdi.annotations.Component;
import org.open.cdi.exceptions.ClassLoadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * Util class for {@link DIContainer} dependency injection container
 */
public class DIClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(DIContainer.class);

    /**
     * Loads all classes annotated {@link Component} form specified package
     * further creates an array instances of them
     * @param packageName package name for class loading
     * @return an array of instances of loaded classes
     */
    public static Object[] findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        List<Object> list = reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClassesFromPackage(line, packageName))
                .filter(Objects::nonNull)
                .filter((clazz) -> clazz.getAnnotation(Component.class) != null)
                .map(v -> {
                    try {
                        return  v.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new ClassLoadingException(e);
                    }
                })
                .toList();

        return list.toArray(new Object[]{});
    }


    /**
     * Loads particular class from specified package and returns an instance of it
     * @param className class name
     * @param packageName package name
     * @return an instance of specified class
     */
    private static Class getClassesFromPackage(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            logger.error( "Class with name: " +className +" in package: "+ packageName+" not found!");
        }
        return null;
    }
}
