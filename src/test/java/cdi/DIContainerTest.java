package cdi;

import cdi.util.*;
import org.junit.Test;
import org.open.cdi.DIContainer;

import static org.junit.Assert.*;

public class DIContainerTest {

    @Test
    public void loadPrototypeBeanTest() {
        Person person = new Person();
        DIContainer container = new DIContainer();
        container.loadAll(person);
        container.init();
        assertEquals(1, container.size());

        Person saved = (Person) container.find("person");
        Person saved2 = (Person) container.find("person");
        assert saved2 != saved;
        assert saved2 != person;
        assert  saved != person;
        assertEquals(person, saved);
        container.clearContext();
    }

    @Test
    public void loadPrototypeBean() {

        SingeltonPerson person = new SingeltonPerson();
        DIContainer container = new DIContainer();
        container.loadAll(person);
        container.init();
        assertEquals(1, container.size());

        SingeltonPerson saved = (SingeltonPerson) container.find("singletonPerson");
        SingeltonPerson saved2 = (SingeltonPerson) container.find("singletonPerson");
        assert saved2 == saved;
        assert saved2 == person;
        assertEquals(person, saved);
    }

    @Test
    public void dependencyInjectionTest() {
        DIContainer container = new DIContainer();
        container.loadAll(new Address(),new Person());
        container.injectDependencies();
        assertEquals(2, container.size());

        Person saved = (Person) container.find("person");
        Person saved2 = (Person) container.find("person");
        assertNotNull(saved2.getAddress());

        assertNotEquals(saved2.getAddress(), saved.getAddress());

        container.clearContext();

        container.loadAll(new Controller(), new UserService());
        container.injectDependencies();
        assertEquals(2, container.size());

        UserService service  = (UserService) container.find("UserService");
        int val = 2323;
        service.setValue(val);

        UserService service2  = (UserService) container.find("UserService");
        assert service == service2;
        assertEquals(val, service2.getValue());
    }
}
