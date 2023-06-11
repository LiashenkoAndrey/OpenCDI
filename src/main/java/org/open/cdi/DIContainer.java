package org.open.cdi;

import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;
import org.open.cdi.annotations.InjectBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.open.cdi.DIClassLoader.findAllClassesUsingClassLoader;


/**
 * Custom dependency injection container witch saves objects in {@link HashMap}
 */
public class DIContainer {
    private static final Logger logger = LoggerFactory.getLogger(DIContainer.class);
    private final Map<String, Object> singletonBeans = new HashMap<>();
    private final Map<String, Class<?>> prototypeBeans = new HashMap<>();


    /**
     * finds classes annotated {@link DIBean} in specified packages and loads them in the singleton ot prototype context
     * @param packages packages for loading beans
     */
    public void loadFromPackages(String... packages) {
        for (String pkg : packages) {
            logger.info("Loading beans from packages: " + Arrays.toString(packages));
            loadAll(findAllClassesUsingClassLoader(pkg));
        }
    }


    /**
     * Loads classes annotated {@link DIBean}
     * @param objects objects for loading in container
     */
    public void loadAll(Object... objects) {
        logger.info(objects.length +" beans were found");
        for (Object obj : objects) {
            Class<?> objClass = obj.getClass();
            String className = parseClassNameFromClassToString(obj.getClass().getName());
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
        if (obj == null || beanName == null) throw new IllegalArgumentException("Arguments can't be null");
        else if (beanName.trim().length() == 0) throw new IllegalArgumentException("Argument 'beanName' is empty string");
        logger.info("Load single object: " + obj.getClass() +", with name " + beanName);
        singletonBeans.put(beanName, obj);
    }


    /**
     * Finds fields annotated with {@link InjectBean} and injects appropriate bean by name specified in annotation
     * If there are no appropriate bean injects {@code null}
     */
    private void injectDependencies(Object... objects) {
        try {

            for (Object obj : objects) {
                Class<?> clazz = obj.getClass();
                Set<Field> fieldsSet = new HashSet<>();

                fieldsSet.addAll(Arrays.asList(clazz.getFields()));
                fieldsSet.addAll(Arrays.asList(clazz.getDeclaredFields()));
                for (Field field : fieldsSet.toArray(new Field[] {})) {
                    InjectBean val = getDIAnnotation(field);
                    if (val != null) {
                        field.setAccessible(true);
                        if (val.value().equals("")) field.set(obj, find(parseClassNameFromClassToString(field.getType().getTypeName())));
                        else field.set(obj, find(val.value()));
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static InjectBean getDIAnnotation(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation a : annotations) {
            Class<?> type = a.annotationType();
            if (type.getName().equals(InjectBean.class.getName())) return (InjectBean) a;
        }
        return null;
    }


    /**
     * injects dependencies for both singleton and prototype beans
     */
    public void init() {
        injectDependencies(prototypeBeans.values().toArray());
        injectDependencies(singletonBeans.values().toArray());
        logger.info("Initialization successful");
    }


    /**
     * Finds bean in container
     * @param beanName bean's name
     * @return a bean wrapped on nullable {@link Optional}
     */
    public Object find(String beanName) {
        if (beanName == null) throw new IllegalArgumentException("Argument is null");
        else if (beanName.trim().length() == 0) throw new IllegalArgumentException("Argument is empty string");
        Object bean = singletonBeans.get(beanName);
        if (bean == null) {
            Class<?> clazz = prototypeBeans.get(beanName);
            if (clazz != null) {
                bean = createInstance(clazz);
                injectDependencies(bean);
            }
        }
        logger.info("Finding a bean with name \"" + beanName+"\": " + (bean != null) );
        return bean;
    }


    private boolean hasBeanWithName(String beanName) {
        return find(beanName) != null;
    }

    private Object createInstance(Class<?> clazz) {
        System.out.println(clazz);
        if (clazz == null) throw new IllegalArgumentException("Argument is not present!");
        try {
            Object o = clazz.getConstructor().newInstance();
            injectDependencies(o);
            return o;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clears all beans from context
     */
    public void clearContext() {
        logger.info("Clear context!");
        prototypeBeans.clear();
        singletonBeans.clear();
    }


    /**
     * returns size both singleton and prototype beans
     * @return size both singleton and prototype beans
     */
    public int containerSize() {
        return singletonBeans.size() + prototypeBeans.size();
    }

    public boolean containerIsEmpty() {
        return containerSize() == 0;
    }


    public static String parseClassNameFromClassToString(String path) {
        String[] arr = path.split("\\.");
        return arr[arr.length-1];
    }
}
