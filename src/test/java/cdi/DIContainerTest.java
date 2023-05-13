package cdi;

import cdi.util.*;
import org.junit.Test;
import org.open.cdi.BeanManager;
import org.open.cdi.DIContainer;

public class DIContainerTest {

    @Test
    public void loadPrototypeBeanTest() {
        BeanManager beanManager = new BeanManager();
        beanManager.loadWithName(beanManager, "beanManager");
        beanManager.init();


        BeanManager manager = (BeanManager) beanManager.find("beanManager");
        System.out.println(manager.size());
        manager.loadWithName(new Person(), "person");
        Person p = (Person) manager.find("person");
        assert p != null;
//        DIContainer.loadAll(person);
//        DIContainer.init();
//        System.out.println(Arrays.toString(DIContainer.singletonBeans.values().toArray()));
//        System.out.println(Arrays.toString(DIContainer.prototypeBeans.values().toArray()));
//        assertEquals(1, DIContainer.size());
//
//        Person saved = (Person) DIContainer.find("person");
//        Person saved2 = (Person) DIContainer.find("person");
//        assert saved2 != saved;
//        assert saved2 != person;
//        assert  saved != person;
//        assertEquals(person, saved);
//        DIContainer.clearContext();
    }
//
//    @Test
//    public void loadPrototypeBean() {
//
//        SingeltonPerson person = new SingeltonPerson();
//        System.out.println(Arrays.toString(DIContainer.singletonBeans.values().toArray()));
//        System.out.println(Arrays.toString(DIContainer.prototypeBeans.values().toArray()));
//        DIContainer.loadAll(person);
//        DIContainer.init();
//        assertEquals(1, DIContainer.size());
//
//        SingeltonPerson saved = (SingeltonPerson) DIContainer.find("singletonPerson");
//        SingeltonPerson saved2 = (SingeltonPerson) DIContainer.find("singletonPerson");
//        assert saved2 == saved;
//        assert saved2 == person;
//        assertEquals(person, saved);
//        DIContainer.clearContext();
//    }
//
//    @Test
//    public void dependencyInjectionTest() {
//        DIContainer.loadAll(new Address(),new Person());
//        DIContainer.injectDependencies();
//        assertEquals(2, DIContainer.size());
//
//        Person saved = (Person) DIContainer.find("person");
//        Person saved2 = (Person) DIContainer.find("person");
//        assertNotNull(saved2.getAddress());
//
//        assertNotEquals(saved2.getAddress(), saved.getAddress());
//        DIContainer.clearContext();
//
//
//        DIContainer.loadAll(new Controller(), new UserService());
//        DIContainer.injectDependencies();
//        assertEquals(2, DIContainer.size());
//
//        UserService service  = (UserService) DIContainer.find("UserService");
//        int val = 2323;
//        service.setValue(val);
//
//        UserService service2  = (UserService) DIContainer.find("UserService");
//        assert service == service2;
//        assertEquals(val, service2.getValue());
//        DIContainer.clearContext();
//    }

}
