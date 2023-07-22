package cdi.implementatinInjection;

import org.junit.jupiter.api.Test;
import org.open.cdi.DIContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImplementationInjectionTest {
    private static final Logger logger = LoggerFactory.getLogger(ImplementationInjectionTest.class);

    public static void main(String[] args) {
        UserServiceImpl service = new UserServiceImpl();
        System.out.println(Arrays.toString(service.getClass().getInterfaces()));
    }

    @Test
    public void test() {

        DIContainer container = new DIContainer();
        container.loadAll( new UserController(), new UserServiceImpl());
        container.init();

        Optional<UserController> controller = container.find(UserController.class, "UserController");

        if (controller.isPresent()) {
            User user = controller.get().getUser();
            System.out.println(user);
            assertNotNull(user);

        } else throw new RuntimeException("error");
    }
}
