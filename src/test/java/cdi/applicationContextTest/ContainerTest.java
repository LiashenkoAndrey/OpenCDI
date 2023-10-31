package cdi.applicationContextTest;

import cdi.util.Address;
import cdi.util.Person;
import cdi.packageOfBeans.Fox;
import org.junit.jupiter.api.Test;
import org.openCDI.ApplicationContext;
import org.openCDI.ApplicationContextFactory;
import org.openCDI.exceptions.ComponentNotFoundException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {

    @Test
    public void loadAndGetBeansTest() {
        ApplicationContext context = ApplicationContextFactory.getApplicationContext();
        Person bean = Person.random();
        context.loadAll(bean);
        Optional<Person> person = context.find(Person.class);
        assertNotNull(person.get());
    }

    @Test
    public void loadFromPackagesTest() {
        ApplicationContext context = ApplicationContextFactory.getApplicationContext();
        context.loadFromPackages("cdi.packageOfBeans");

        Optional<Fox> fox = context.find(Fox.class);
        assertNotNull(fox.get());
    }

    @Test
    public void loadAllTest() {
        Fox fox = new Fox();
        Person person = Person.random();

        ApplicationContext context = ApplicationContextFactory.getApplicationContext();

        assertEquals(0, context.contextSize());
        context.loadAll(fox, person);

        assertNotEquals(0, context.contextSize());
        assertEquals(2, context.contextSize());

        Fox foxFromContainer = context.find(Fox.class).orElseThrow(ComponentNotFoundException::new);
        Person personFromContainer = context.find(Person.class).orElseThrow(ComponentNotFoundException::new);;

        assertNotNull(foxFromContainer);
        assertNotNull(personFromContainer);
        assertEquals(fox, foxFromContainer);
        assertEquals(person, personFromContainer);
    }


    @Test
    public void loadWithNameTest() {
        Person person = Person.random();
        ApplicationContext context = ApplicationContextFactory.getApplicationContext();
        context.loadAll(person);

        Person actual = context.find(Person.class).orElseThrow(ComponentNotFoundException::new);
        assertEquals(person, actual);
    }

    @Test
    public void initTest() {
        ApplicationContext context = ApplicationContextFactory.getApplicationContext();

        Person person = new Person();
        Address address = new Address("kiev");
        context.loadAll(person, address);

        context.init();

        Person personFromContext = context.find(Person.class).orElseThrow(ComponentNotFoundException::new);
        assertNotNull(personFromContext);
        assertNotNull(personFromContext.getAddress());
        assertEquals(address, personFromContext.getAddress());
    }


    @Test
    public void findTest() {
        ApplicationContext context = ApplicationContextFactory.getApplicationContext();


        Person person = Person.random();
        context.loadAll(person);
        Person saved = context.find(Person.class).orElseThrow(ComponentNotFoundException::new);

        assertNotNull(saved);
        assertEquals(person, saved);

        assertThrows(IllegalArgumentException.class, () -> context.find(Person.class,null));
        assertThrows(IllegalArgumentException.class, () -> context.find(Person.class,""));
    }

    @Test
    public void findTest2() {
        ApplicationContext context = ApplicationContextFactory.getApplicationContext();


        Person person = Person.random();
        context.loadAll(person);
        Person saved = context.find(Person.class).orElseThrow(ComponentNotFoundException::new);

        assertNotNull(saved);
        assertEquals(person, saved);

        assertThrows(IllegalArgumentException.class, () -> context.find(Person.class,null));
        assertThrows(IllegalArgumentException.class, () -> context.find(Person.class,""));
        assertThrows(IllegalArgumentException.class, () -> context.find(null,""));
    }



    @Test
    public void clearContextTest() {

        ApplicationContext context = ApplicationContextFactory.getApplicationContext();

        HashMap<String, Object>  singletonBeans = getValueOfObjectField(context, "singletonBeans");
        HashMap<String, Class<?>>  prototypeBeans = getValueOfObjectField(context, "prototypeBeans");

        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));
        assertEquals(0, context.contextSize());

        assertEquals(2, sizeOfMaps(singletonBeans, prototypeBeans));

        context.clearContext();

        assertEquals(0,  sizeOfMaps(singletonBeans, prototypeBeans));
        assertEquals(0, context.contextSize());
    }

    private int sizeOfMaps(Map... maps) {
        return Stream.of(maps).mapToInt(Map::size).sum();
    }

    private <T> T getValueOfObjectField(Object obj, String fieldName) {
        try {
            Field field =  obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void contextSizeTest() {

        ApplicationContext context = ApplicationContextFactory.getApplicationContext();
        HashMap<String, Object>  singletonBeans = getValueOfObjectField(context, "singletonBeans");
        HashMap<String, Class<?>>  prototypeBeans =  getValueOfObjectField(context, "prototypeBeans");

        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));
        assertEquals(0, context.contextSize());


        assertEquals(3, sizeOfMaps(singletonBeans, prototypeBeans));
        assertEquals(3, context.contextSize());


        context.clearContext();
        assertEquals(0, context.contextSize());
        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));

        context.loadAll(new Address());
        assertEquals(1, context.contextSize());
        assertEquals(1, sizeOfMaps(singletonBeans, prototypeBeans));

    }

    @Test
    public void contextIsEmptyTest() {

        ApplicationContext context = ApplicationContextFactory.getApplicationContext();
        HashMap<String, Object>  singletonBeans = getValueOfObjectField(context, "singletonBeans");
        HashMap<String, Class<?>> prototypeBeans =  getValueOfObjectField(context, "prototypeBeans");

        assertEquals(0, sizeOfMaps(singletonBeans, prototypeBeans));
        assertEquals(0, context.contextSize());

        assertTrue(sizeOfMaps(singletonBeans, prototypeBeans) > 0);
        assertEquals(0, context.contextSize());
    }

}
