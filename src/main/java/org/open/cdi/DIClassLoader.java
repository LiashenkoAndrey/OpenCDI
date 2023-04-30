package org.open.cdi;

import org.open.cdi.annotations.DIBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Util class for {@link DIContainer} dependency injection container
 */
public class DIClassLoader {

    private static final Logger logger = Logger.getLogger(DIClassLoader.class.getName());

    /**
     * Loads all classes annotated {@link DIBean} form specified package
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
                .map(line -> getObjectOfClass(line, packageName))
                .filter(Objects::nonNull)
                .filter((clazz) -> clazz.getClass().getAnnotation(DIBean.class) != null)
                .toList();
        return list.toArray(new Object[]{});
    }


    /**
     * Loads particular class from specified package and returns an instance of it
     * @param className class name
     * @param packageName package name
     * @return an instance of specified class
     */
    private static Object getObjectOfClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.'))).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            logger.warning( "Class with name: " +className +" in package: "+ packageName+" not found!");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
