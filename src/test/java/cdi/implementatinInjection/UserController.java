package cdi.implementatinInjection;

import lombok.ToString;
import org.open.cdi.annotations.Component;
import org.open.cdi.annotations.Use;

@Component
@ToString
public class UserController {

    @Use(clazz = UserServiceImpl.class)
    UserService service;

    public User getUser() {
        return service.createEmpty();
    }
}
