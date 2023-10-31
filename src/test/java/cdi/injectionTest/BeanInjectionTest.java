package cdi.injectionTest;

import org.junit.jupiter.api.Test;
import org.openCDI.ApplicationContextFactory;

import java.util.Optional;

public class BeanInjectionTest {
    @Test
    public void injectionTest() {
        var context = ApplicationContextFactory.getApplicationContext(BeanInjectionTest.class);
        context.init();

        Optional<Controller> controller = context.find(Controller.class);
        controller.get().goSomething();
    }
}
