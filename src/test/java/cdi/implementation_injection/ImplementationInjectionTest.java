package cdi.implementation_injection;

import org.junit.jupiter.api.Test;
import org.openCDI.ApplicationContext;
import org.openCDI.ApplicationContextFactory;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ImplementationInjectionTest {

    @Test
    public void test() {

        ApplicationContext context = ApplicationContextFactory.getApplicationContext();
        context.loadAll( new UserController(), new UserServiceImpl());
        context.init();


        for (Map.Entry e : context.getContext().entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }

        Optional<UserController> controller = context.find(UserController.class, "UserController");

        if (controller.isPresent()) {
            assertNotNull(controller.get().service);
            System.out.println(controller.get().service.createEmpty());
        } else throw new RuntimeException("error");
    }
}
