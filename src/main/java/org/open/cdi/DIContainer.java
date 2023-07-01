package org.open.cdi;

import org.open.cdi.annotations.BeanScope;
import org.open.cdi.annotations.DIBean;
import org.open.cdi.annotations.InjectBean;
import org.open.cdi.exceptions.WrongBeanTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.open.cdi.DIClassLoader.findAllClassesUsingClassLoader;
import static org.open.cdi.DIContainerUtils.getDIAnnotation;
import static org.open.cdi.DIContainerUtils.parseClassNameFromClassToString;


/**
 * Custom dependency injection container witch saves objects in {@link HashMap}
 */
public class DIContainer {
    private static final Logger logger = LoggerFactory.getLogger(DIContainer.class);
    private final Map<String, Object> singletonBeans = new HashMap<>();
    private final Map<String, Class<?>> prototypeBeans = new HashMap<>();

    private final DIContainerUtils utils = new DIContainerUtils();


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
            else if (!Modifier.isAbstract(objClass.getModifiers())) {
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
                List<Field> injectableFields = utils.getAllInjectableFieldsRecursively(clazz);

                for (Field field : injectableFields) {
                    InjectBean val = getDIAnnotation(field);
                    if (val.value().equals("")) field.set(obj, find(parseClassNameFromClassToString(field.getType().getTypeName())));
                    else field.set(obj, find(val.value()));
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
        injectDependencies(prototypeBeans.values().toArray());
        injectDependencies(singletonBeans.values().toArray());
        logger.info("Initialization successful");
    }


    /**
     * Finds bean in container by bean name
     *
     *  @deprecated
     *  This method is no longer acceptable.
     *  <p> Use {@link DIContainer#find(Class, String)} instead.
     *
     * @param beanName bean's name
     * @return a bean wrapped on nullable {@link Optional}
     */
    @Deprecated(since = "version 0.1", forRemoval = true)
    public Object find(String beanName) {
        return findBeanByName(beanName);
    }


    /**
     * Finds saved instance of the class stored in container by bean name
     * @param type type of bean
     * @param beanName name of bean
     * @return saved instance of class stored in container
     * @param <T> type of stores bean
     */
    @SuppressWarnings("unchecked cast")
    public <T> T find(Class<T> type, String beanName) {
        Object bean = findBeanByName(beanName);
        if (type == null) throw new IllegalArgumentException("Type is null");

        if (bean != null) {
            if (bean.getClass().equals(type)) { // if bean is instance of specified type
                return (T) bean;
            } else throw new WrongBeanTypeException("Specified type: "+ type.getName() + " is not the same which in found bean: " + bean.getClass().getName());

        } else {
            return null;
        }
    }

    private Object findBeanByName(String beanName) {
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


    public boolean hasBeanWithName(String beanName) {
        return findBeanByName(beanName) != null;
    }

    public Class getTypeOfBeanByName(String beanName) {
        return findBeanByName(beanName).getClass();
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

}
