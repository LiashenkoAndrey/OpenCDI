package cdi.implementatinInjection;

import lombok.ToString;
import org.openCDI.ApplicationContext;
import org.openCDI.annotations.Component;
import org.openCDI.annotations.Use;

@Component
@ToString
public class UserController {

    public String getHello() {
        return "hello!";
    }

    private ApplicationContext context;

    @Use(clazz = AnotherUserServiceIml.class)
    public UserService service;

    public User getUser() {
        System.out.println(context.contextSize());
        context.loadAll(new AnotherUserServiceIml());
        System.out.println(context.find(AnotherUserServiceIml.class).get());
        return new User("sdfsd");
    }
}
