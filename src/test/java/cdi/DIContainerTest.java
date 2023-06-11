package cdi;

import cdi.util.*;
import cdi.util.packageOfBeans.Fox;
import org.junit.jupiter.api.Test;
import org.open.cdi.BeanManager;
import org.open.cdi.DIContainer;
import org.open.cdi.annotations.InjectBean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;



public class DIContainerTest {

    @Test
    public void loadAndGetBeansTest() {
        BeanManager manager = new BeanManager();
        Person bean = Person.random();
        manager.loadWithName(bean, "bean");
        Person person = (Person) manager.find("bean");
        assertEquals(bean, person);
    }

    @Test
    public void loadFromPackagesTest() {
        BeanManager manager = new BeanManager();
        manager.loadFromPackages("cdi.util.packageOfBeans");

        Fox fox = (Fox) manager.find("Fox");
        assertNotNull(fox);
    }

    @Test
    public void loadAllTest() {
        Fox fox = new Fox();
        Person person = Person.random();

        BeanManager manager = new BeanManager();
        assertTrue(manager.containerIsEmpty());
        manager.loadAll(fox, person);

        assertFalse(manager.containerIsEmpty());
        assertEquals(2, manager.containerSize());

        Fox foxFromContainer = (Fox) manager.find("Fox");
        Person personFromContainer = (Person) manager.find("Person");

        assertNotNull(foxFromContainer);
        assertNotNull(personFromContainer);
        assertEquals(fox, foxFromContainer);
        assertEquals(person, personFromContainer);
    }


    @Test
    public void loadWithNameTest() {
        Person person = Person.random();
        BeanManager manager = new BeanManager();
        manager.loadWithName(person, "person");

        Person actual = (Person) manager.find("person");
        assertEquals(person, actual);


    }

    @Test
    public void loadWithNameWithNullArgumentsTest() {
        BeanManager manager = new BeanManager();
        assertThrows(IllegalArgumentException.class, () -> manager.loadWithName(Person.random(), ""));
        assertThrows(IllegalArgumentException.class, () -> manager.loadWithName(Person.random(), null));
        assertThrows(IllegalArgumentException.class, () -> manager.loadWithName(null,null));
        assertThrows(IllegalArgumentException.class, () -> manager.loadWithName("",null));
    }

    @Test
    public void injectDependenciesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Person person = new Person();
        Address address = new Address("kiev");

        BeanManager manager = new BeanManager();
        manager.loadAll(address);
        assertNull(person.getAddress());

        Method method = DIContainer.class.getDeclaredMethod("injectDependencies", Object[].class);
        method.setAccessible(true);
        method.invoke(manager, new Object[] {new Object[] {person}});

        assertNotNull(person.getAddress());
        assertEquals(address, person.getAddress());
    }


    @Test
    public void initTest() {
        BeanManager manager = new BeanManager();
        Person person = new Person();
        Address address = new Address("kiev");
        manager.loadAll(person, address);

        manager.init();

        Person personFromContext = (Person) manager.find("Person");
        assertNotNull(personFromContext);
        assertNotNull(personFromContext.getAddress());
        assertEquals(address, personFromContext.getAddress());
    }



    @Test
    public void getDIAnnotationTest() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Field field = Person.class.getDeclaredFields()[0];
        Optional<Object> optional = invokePrivateMethod(DIContainer.class.getConstructor().newInstance(), DIContainer.class,  "getDIAnnotation", new Class[] {Field.class},  field);
        assertTrue(optional.isPresent());
        InjectBean injectBean = (InjectBean) optional.get();
        System.out.println(injectBean.value());
    }


    private Optional<Object> invokePrivateMethod(Object invoker, Class<?> clazz, String methodName, Class<?>[] expectedMethodArgs, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, expectedMethodArgs);
            method.setAccessible(true);
            return Optional.ofNullable(method.invoke(invoker, args));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    @Test
    public void findTest() {
        BeanManager manager = new BeanManager();

        Person person = Person.random();
        manager.loadWithName(person, "bean");
        Person saved = (Person) manager.find("bean");

        assertNotNull(saved);
        assertEquals(person, saved);

        assertThrows(IllegalArgumentException.class, () -> manager.find(null));
        assertThrows(IllegalArgumentException.class, () -> manager.find(""));

    }

    @Test
    public void createInstanceTest() {
        Optional<Object> obj = invokePrivateMethod(new DIContainer(), DIContainer.class, "createInstance", new Class[] {Class.class}, Person.class);
        assertTrue(obj.isPresent());
        assertInstanceOf(Person.class, obj.get());

        assertThrows(RuntimeException.class, () -> invokePrivateMethod(new DIContainer(), DIContainer.class, "createInstance", new Class[] {Class.class}, new Object[]{null}));
    }


    @Test
    public void clearContextTest() {
        DIContainer container = new DIContainer();

        HashMap<String, Object>  singletonBeans = (HashMap<String, Object>) getFieldOfObject(container, "singletonBeans");
        HashMap<String, Class<?>>  prototypeBeans = (HashMap<String, Class<?>>) getFieldOfObject(container, "prototypeBeans");

        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));
        assertTrue(container.containerIsEmpty());

        container.loadAll(Person.random(), PrototypePerson.random());
        assertEquals(2, sizeOfMaps(singletonBeans, prototypeBeans));

        container.clearContext();

        assertEquals(0,  sizeOfMaps(singletonBeans, prototypeBeans));
        assertTrue(container.containerIsEmpty());
    }

    private int sizeOfMaps(Map... maps) {
        return Stream.of(maps).mapToInt(Map::size).sum();
    }

    private Object getFieldOfObject(Object obj, String fieldName) {
        try {
            Field field =  obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void containerSizeTest() {
        DIContainer container = new DIContainer();
        HashMap<String, Object>  singletonBeans = (HashMap<String, Object>) getFieldOfObject(container, "singletonBeans");
        HashMap<String, Class<?>>  prototypeBeans = (HashMap<String, Class<?>>) getFieldOfObject(container, "prototypeBeans");

        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));
        assertEquals(0, container.containerSize());


        container.loadAll(new Address(), new Person(), PrototypePerson.random());
        assertEquals(3, sizeOfMaps(singletonBeans, prototypeBeans));
        assertEquals(3, container.containerSize());


        container.clearContext();
        assertEquals(0, container.containerSize());
        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));

        container.loadWithName(new Address(), "bean");
        assertEquals(1, container.containerSize());
        assertEquals(1, sizeOfMaps(singletonBeans, prototypeBeans));

    }

    @Test
    public void containerIsEmptyTest() {
        DIContainer container = new DIContainer();
        HashMap<String, Object>  singletonBeans = (HashMap<String, Object>) getFieldOfObject(container, "singletonBeans");
        HashMap<String, Class<?>>  prototypeBeans = (HashMap<String, Class<?>>) getFieldOfObject(container, "prototypeBeans");

        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));
        assertTrue(container.containerIsEmpty());

        container.loadAll(new Address(), new Person(), PrototypePerson.random());
        assertTrue(sizeOfMaps(singletonBeans, prototypeBeans) > 0);
        assertFalse(container.containerIsEmpty());
    }

}
