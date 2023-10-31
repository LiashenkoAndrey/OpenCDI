package cdi.injectionTest;

import org.junit.jupiter.api.Test;
import org.openCDI.ApplicationContextFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanInjectionTest {

    @Test
    public void injectionTest() {
        var context = ApplicationContextFactory.getApplicationContext(BeanInjectionTest.class);
        context.init();
        System.out.println(context.getContext());

        Optional<Controller> controller = context.find(Controller.class);
        Optional<ServiceImpl> service = context.find(ServiceImpl.class);

        assertTrue(controller.isPresent());
        assertTrue(service.isPresent());
        controller.get().goSomething();
    }

    public static void main(String[] args) {
        System.out.println(BeanInjectionTest.class.getPackageName());
    }
}
