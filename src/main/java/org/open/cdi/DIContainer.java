package org.open.cdi;

import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;
import org.open.cdi.annotations.InjectBean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.open.cdi.DIClassLoader.findAllClassesUsingClassLoader;


/**
 * Custom dependency injection container witch saves objects in {@link HashMap}
 * Util annotations package {@link module.telegram_utils.annotations}
 */
public class DIContainer {
    private static final Logger logger = Logger.getLogger(DIContainer.class.getName());
    private final Map<String, Object> singletonBeans = new HashMap<>();
    private final Map<String, Class<?>> prototypeBeans = new HashMap<>();


    /**
     * finds classes annotated {@link DIBean} in specified packages and loads them in the singleton ot prototype context
     * @param packages packages for loading beans
     */
    public void loadFromPackages(String... packages) {
        for (String pkg : packages) {
            logger.log(Level.INFO, "Loading beans from packages: " + Arrays.toString(packages));
            loadAll(findAllClassesUsingClassLoader(pkg));
        }
    }


    /**
     * Loads classes annotated {@link DIBean}
     * @param objects objects for loading in container
     */
    public void loadAll(Object... objects) {
        logger.log(Level.INFO, objects.length +" beans were found");
        for (Object obj : objects) {
            Class<?> objClass = obj.getClass();
            String className = getTypeFromPath(obj.getClass().getName());
            DIBean annotation = objClass.getDeclaredAnnotation(DIBean.class);
            if (annotation == null) singletonBeans.put(className, obj);
            else {
                String val = annotation.value();
                BeanScope scope = annotation.scope();
                if (val.equals("")) {
                    if (scope == BeanScope.PROTOTYPE) prototypeBeans.put(className, objClass);
                    else singletonBeans.put(className, obj);

                } else {
                    if (scope == BeanScope.PROTOTYPE) prototypeBeans.put(val, objClass);
                    else singletonBeans.put(val, obj);
                }
            }
        }
    }

    public void loadWithName(Object obj, String beanName) {
        logger.log(Level.INFO, "Load single object: " + obj.getClass() +", with name" + beanName);
        singletonBeans.put(beanName, obj);
    }


    /**
     * Finds fields annotated with {@link InjectBean} and injects appropriate bean by name specified in annotation
     * If there are no appropriate bean injects {@code null}
     */
    private void init(Object... objects) {
        logger.log(Level.INFO, "injectDependencies: " + Arrays.toString(objects));
        try {
            for (Object obj : objects) {
                Class clazz = obj.getClass();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    InjectBean val = field.getAnnotation(InjectBean.class);
                    if (val != null) {
                        field.setAccessible(true);
                        if (val.value().equals("")) field.set(obj, find(getTypeFromPath(field.getType().getTypeName())));
                        else field.set(obj, find(val.value()));
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * injects dependencies for both singleton and prototype beans
     */
    public void init() {
        init(prototypeBeans.values().toArray());
        init(singletonBeans.values().toArray());
    }


    /**
     * Finds bean in container
     * @param beanName bean's name
     * @return a bean wrapped on nullable {@link Optional}
     */
    public Object find(String beanName) {
        Object bean = singletonBeans.get(beanName);
        if (bean == null) {
            Class<?> clazz = prototypeBeans.get(beanName);
            if (clazz != null) bean = createInstance(clazz);
        }
        logger.log(Level.INFO, "Try to find bean with name \"" + beanName+"\": " + (bean != null) );
        return bean;
    }

    private Object createInstance(Class<?> clazz) {
        try {
            Object o = clazz.getConstructor().newInstance();
            init(o);
            return o;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clears all beans from context
     */
    public void clearContext() {
        logger.log(Level.INFO, "Clear context!");
        prototypeBeans.clear();
        singletonBeans.clear();
    }


    /**
     * returns size both singleton and prototype beans
     * @return size both singleton and prototype beans
     */
    public int size() {
        return singletonBeans.size() + prototypeBeans.size();
    }


    private String getTypeFromPath(String path) {
        String[] arr = path.split("\\.");
        return arr[arr.length-1];
    }
}
