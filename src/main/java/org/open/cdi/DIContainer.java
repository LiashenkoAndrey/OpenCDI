package org.open.cdi;

import lombok.SneakyThrows;
import org.open.cdi.annotations.Component;
import org.open.cdi.annotations.Inject;
import org.open.cdi.annotations.Scope;
import org.open.cdi.annotations.Use;
import org.open.cdi.exceptions.ComponentNotFoundException;
import org.open.cdi.exceptions.MultipleComponentsWithTheSameClassException;
import org.open.cdi.exceptions.injectImpl.MultipleImplementationException;
import org.open.cdi.exceptions.WrongComponentTypeException;
import org.open.cdi.exceptions.injectImpl.NoImplementationException;
import org.open.cdi.exceptions.validation.ClassIsAbstractException;
import org.open.cdi.exceptions.injectImpl.IllegalQualifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.type.NullType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.open.cdi.DIClassLoader.findAllClassesUsingClassLoader;
import static org.open.cdi.DIContainerUtils.getAllInjectableFieldsRecursively;


/**
 * Custom dependency injection container witch saves objects in {@link HashMap}
 */
public class DIContainer {
    private static final Logger logger = LoggerFactory.getLogger(DIContainer.class);
    private final Map<String, Object> singletonBeans = new HashMap<>();
    private final Map<String, Object> prototypeBeans = new HashMap<>();

    private final List<Object> impls = new ArrayList<>();

    /**
     * injects dependencies for both singleton and prototype beans
     */
    public void init() {
        injectDependencies(prototypeBeans.values().toArray());
        injectDependencies(singletonBeans.values().toArray());
        logger.info("Initialization successful");
    }


    /**
     * finds classes annotated {@link Component} in specified packages and loads them in the singleton ot prototype context
     * @param packages packages for loading beans
     */
    public void loadFromPackages(String... packages) {
        for (String pkg : packages) {
            loadAll(findAllClassesUsingClassLoader(pkg));
        }
    }



    public void loadAll(Object... objects) {
        for (Object obj : objects) {
            load(obj);
        }
    }

    /**
     * Loads class annotated {@link Component}
     * @param obj objects for loading in container
     */
    public void load(Object obj) {
        Class<?> clazz = obj.getClass();
        validate(clazz);

        if (clazz.getInterfaces().length != 0) {
            impls.add(obj);
        }
        Component ann = clazz.getAnnotation(Component.class);
        String name = getName(clazz, ann.value());
        putIntoContext(obj, name, ann.scope());
    }

    private void putIntoContext(Object o, String name, Scope scope) {
        switch (scope) {
            case PROTOTYPE -> prototypeBeans.put(name, o);

            case SINGLETON -> singletonBeans.put(name, o);
        }
    }

    public void loadWithName(Object obj, String name) {
        Class<?> clazz = obj.getClass();
        validate(clazz);

        Component ann = clazz.getAnnotation(Component.class);
        putIntoContext(obj, name, ann.scope());
    }


    private String getName(Class<?> clazz, String val) {
        if (val.isEmpty()) {
            return clazz.getSimpleName();
        } else return val;
    }

    private void validate(Class<?> clazz) {
        if (isAbstract(clazz) || !isComponent(clazz)) throw new ClassIsAbstractException("is abstract");
    }


    private boolean isComponent(Class<?> clazz) {
        return clazz.getDeclaredAnnotation(Component.class) != null;
    }

    private boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * Finds fields annotated with {@link Inject} and injects appropriate bean by name specified in annotation
     * If there are no appropriate bean injects {@code null}
     */
    @SneakyThrows
    private void injectDependencies(Object... objects) {
        for (Object obj : objects) {
            Class<?> clazz = obj.getClass();
            List<Field> injectableFields = getAllInjectableFieldsRecursively(clazz);

            for (Field field : injectableFields) {
                if (field.getType().isInterface()) {
                    Object impl = findImpl(field);
                    field.set(obj, impl);

                } else {
                    String val = field.getAnnotation(Inject.class).value();
                    if (val.isEmpty()) {
                        field.set(obj, findByName(field.getClass().getSimpleName()));
                    } else {
                        field.set(obj, findByName(val));
                    }
                }
            }
        }
    }



    public Object findImpl(Field field) {
        Class<?> type = field.getType();

        List<Object> implsList = impls.stream()
                .filter(impl -> {
                    Class<?>[] classes = impl.getClass().getInterfaces();
                            return List.of(classes).contains(type);
                        }).toList();

        int size = implsList.size();
        if (size == 0) {
            throw new NoImplementationException("There is no implementation for " + type + ". Please provide an implementation into context");

        } else if (size == 1) {
            return implsList.get(0);

        } else {
            Use annotation = field.getAnnotation(Use.class);
            if (annotation == null) throw new MultipleImplementationException("There is more then one implementation of " + type + " class, impls: " + implsList.stream().map(Object::getClass).toList() + ". Please use org.open.cdi.annotations.Use annotation to choose the implementation");

            String useImpl = annotation.value();
            Class<?> useClass = annotation.clazz();
            if (!useImpl.isBlank()) {
                Optional<Object> bean = findByName(useImpl);
                if (bean.isPresent()) {
                    return bean.get();
                } else {
                    throw new ComponentNotFoundException("Component with name: '" + useImpl + "' not found");
                }
            } else if (!useClass.equals(NullType.class)) {
                Optional<Object> bean = findByClass(useClass);
                if (bean.isPresent()) {
                    return bean.get();
                } else {
                    throw new ComponentNotFoundException("Component with class name: '" + useClass.getName() + "' not found");
                }
            } else {
                throw new IllegalQualifierException("Qualifier: '" + useImpl + "' is illegal. Please provide component name or class as a parameter. For example @Use('ImplClass') or @Use(clazz = ImplClass.class) ");
            }
        }
    }


    /**
     * Finds saved instance of the class stored in container by bean name
     * @param type type of bean
     * @param beanName name of bean
     * @return saved instance of class stored in container
     * @param <T> type of stores bean
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(Class<T> type, String beanName) {
        if (type == null) throw new IllegalArgumentException("Type is null");
        Optional<Object> bean = findByName(beanName);

        if (bean.isPresent()) {
            if (bean.get().getClass().equals(type)) { // if bean is instance of specified type
                return Optional.of((T) bean.get());
            } else throw new WrongComponentTypeException("Specified type: "+ type.getName() + " is not the same which in found bean: " + bean.getClass().getName());

        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type is null");
        Optional<Object> bean = findByClass(type);

        return bean.map(o -> (T) o);
    }

    public Optional<Object> findByName(String name) {
        validateComponentName(name);

        if (singletonBeans.containsKey(name)) {
            return Optional.of(singletonBeans.get(name));

        } else if (prototypeBeans.containsKey(name)) {
            return Optional.of(prototypeBeans.get(name));

        } else {
            return Optional.empty();
        }
    }


    private Optional<Object> findByClass(Class<?> clazz) {
        List<Object> singletonList = singletonBeans.values().stream()
                .filter(o -> o.getClass().getName().equals(clazz.getName()))
                .toList();

        int size = singletonList.size();
        if (size == 0) {
            return Optional.empty();
        } else if (size == 1) {
            return Optional.of(singletonList.get(0));
        } else {
            throw new MultipleComponentsWithTheSameClassException("There is multiple components with the same class " + clazz + ", components: " + singletonList);
        }
    }


    private void validateComponentName(String name) {
        if (name == null) throw new IllegalArgumentException("Argument is null");
        else if (name.length() == 0) throw new IllegalArgumentException("Argument is empty string");
    }

    /**
     * Clears all beans from context
     */
    public void clearContext() {
        logger.info("Clear context!");
        prototypeBeans.clear();
        singletonBeans.clear();
    }

    public Map<String, Object> getContext() {
        Map<String, Object> map = new HashMap<>(singletonBeans.size() + prototypeBeans.size());
        map.putAll(prototypeBeans);
        map.putAll(singletonBeans);
        return map;
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
