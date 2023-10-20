package org.openCDI.impl;

import lombok.SneakyThrows;
import org.openCDI.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApplicationClassLoaderImpl extends ClassLoader {

    public ApplicationClassLoaderImpl() {
        super(ApplicationClassLoaderImpl.class.getClassLoader());
    }

    private static final Logger logger = LoggerFactory.getLogger(ApplicationClassLoaderImpl.class);

    /**
     * Loads all classes annotated {@link Component} form specified package
     * further creates an array instances of them
     * @param packageName package name for class loading
     * @return an array of instances of loaded classes
     */
    public static Object[] findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClassesFromPackage(line, packageName))
                .filter(ApplicationClassLoaderImpl::isComponentOrInterface)
                .map(ApplicationClassLoaderImpl::createInstance)
                .toArray();
    }


    private static boolean isComponentOrInterface(Class<?> clazz) {
        if (clazz != null) {
            return (clazz.getAnnotation(Component.class) != null || clazz.isInterface()) ;
        } else return false;
    }

    @SneakyThrows
    private static Object createInstance(Class<?> clazz) {
        return clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * Loads particular class from specified package and returns an instance of it
     * @param className class name
     * @param packageName package name
     * @return an instance of specified class
     */
    private static Class<?> getClassesFromPackage(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            logger.error( "Class with name: " +className +" in package: "+ packageName+" not found!");
        }
        return null;
    }
}
