package org.openCDI.impl;

import lombok.SneakyThrows;
import org.openCDI.ApplicationContext;
import org.openCDI.annotations.Component;
import org.openCDI.annotations.Inject;
import org.openCDI.annotations.Service;
import org.openCDI.annotations.Use;
import org.openCDI.exceptions.ComponentNotFoundException;
import org.openCDI.exceptions.MultipleComponentsWithTheSameClassException;
import org.openCDI.exceptions.WrongComponentTypeException;
import org.openCDI.exceptions.injectImpl.IllegalQualifierException;
import org.openCDI.exceptions.injectImpl.MultipleImplementationException;
import org.openCDI.exceptions.injectImpl.NoImplementationException;
import org.openCDI.util.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.type.NullType;
import java.lang.reflect.Field;
import java.util.*;

import static org.openCDI.impl.ApplicationClassLoaderImpl.findAllClassesUsingClassLoader;
import static org.openCDI.util.ApplicationContextUtils.validateComponent;

@Component
public class ApplicationContextImpl implements ApplicationContext {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextImpl.class);
    private final Map<String, Object> singletonBeans = new HashMap<>();

    private final List<Object> impls = new ArrayList<>();

    /**
     * injects dependencies for beans
     */
    public void init() {
        logger.info("Starting injection... " + singletonBeans );
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
            loadComponent(obj);
        }
    }

    /**
     * Loads class annotated {@link Component}
     * @param obj objects for loading in container
     */
    private void loadComponent(Object obj) {
        Class<?> clazz = obj.getClass();
        validateComponent(clazz);

        if (clazz.getInterfaces().length != 0) {
            impls.add(obj);
        }

        String name = getComponentName(clazz);
        putIntoContext(obj, name);
    }

    private void putIntoContext(Object o, String name) {
        singletonBeans.put(name, o);
    }

    private String getComponentName(Class<?> clazz) {
        String value;
        if (clazz.isAnnotationPresent(Component.class)) {
            value = clazz.getAnnotation(Component.class).value();
        } else {
            value = clazz.getAnnotation(Service.class).value();
        }
        return value.isEmpty() ? clazz.getSimpleName() : value;
    }

    private String getName(Class<?> clazz, String val) {
        if (val.isEmpty()) {
            return clazz.getSimpleName();
        } else return val;
    }

    public void loadWithName(Object obj, String name) {
        Class<?> clazz = obj.getClass();
        validateComponent(clazz);

        Component ann = clazz.getAnnotation(Component.class);
        putIntoContext(obj, name);
    }


    /**
     * Finds fields annotated with {@link Inject} and injects appropriate bean by name specified in annotation
     * If there are no appropriate bean injects {@code null}
     */
    @SneakyThrows
    private void injectDependencies(Object... objects) {
        for (Object obj : objects) {
            Class<?> clazz = obj.getClass();
            List<Field> injectableFields = ApplicationContextUtils.getAllInjectableFieldsRecursively(clazz);

            for (Field field : injectableFields) {
                if (field.getType().isInterface()) {
                    Object impl = findImpl(field);
                    field.set(obj, impl);

                } else {
                    String val = field.getAnnotation(Inject.class).value();
                    if (val.isEmpty()) {
                        Optional<Object> o = findByName(field.getType().getSimpleName());
                        if (o.isPresent()) field.set(obj, o.get());
                    } else {
                        Optional<Object> o = findByName(val);
                        if (o.isPresent()) field.set(obj, o.get());
                        field.set(obj, o);
                    }
                }
            }
        }
    }


    /**
     * Finds implementation of interface specified as dependency in component
     * @param field dependency field
     * @return implementation of interface
     */
    private Object findImpl(Field field) {
        Class<?> type = field.getType();

        List<Object> implsList = impls.stream()
                .filter(impl -> {
                    Class<?>[] classes = impl.getClass().getInterfaces();
                    return List.of(classes).contains(type);
                }).toList();

        Object impl;
        switch (implsList.size()) {
            case 0 -> throw new NoImplementationException("There is no implementation for " + type +
                    ". Please provide an implementation into context");

            case 1 -> impl = implsList.get(0);

            default -> impl = getByImplementation(field, type, impls);
        }

        return impl;
    }

    private Object getByImplementation(Field field, Class<?> type, List<Object> implsList) {
        Use annotation = field.getAnnotation(Use.class);
        if (annotation == null) throw new MultipleImplementationException(String.format("""
                There is more then one implementation of %s class, impls: %s.
                Please use org.open.cdi.annotations.Use annotation to choose the implementation
                """, type, implsList.stream().map(Object::getClass).toList())
        );

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
            throw new IllegalQualifierException(String.format("""
                Qualifier: '%s' is illegal.
                Please provide component name or class as a parameter.
                For example @Use('ImplClass') or @Use(clazz = ImplClass.class)
                    """, useImpl)
            );
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

        logger.info("find by name: " + name);
        ApplicationContextUtils.validateComponentName(name);

        if (singletonBeans.containsKey(name)) {
            return Optional.of(singletonBeans.get(name));

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


    /**
     * Clears all beans from context
     */
    public void clearContext() {
        logger.info("Clear context!");
        singletonBeans.clear();
    }

    public Map<String, Object> getContext() {
        Map<String, Object> map = new HashMap<>(singletonBeans.size());
        map.putAll(singletonBeans);
        return map;
    }


    /**
     * returns size both singleton and prototype beans
     * @return size both singleton and prototype beans
     */
    public int contextSize() {
        return singletonBeans.size();
    }

}
