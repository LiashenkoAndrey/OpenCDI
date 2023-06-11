package cdi;

import cdi.util.Person;
import cdi.util.inheritance.ChildBean;
import cdi.util.inheritance.ParentBean;
import org.junit.jupiter.api.Test;
import org.open.cdi.BeanManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeansInheritanceTest {

    @Test
    public void inheritanceTest() {
        BeanManager manager = new BeanManager();
        Person person = Person.random();
        manager.loadAll(new ParentBean(), new ChildBean(), person);
        manager.init();

        ChildBean childBean = (ChildBean) manager.find("ChildBean");

        assertNotNull(childBean.person);
        assertEquals(childBean.person, person);
    }
}
