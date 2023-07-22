package cdi.implementatinInjection;

import org.open.cdi.annotations.Component;

@Component
public class AnotherUserServiceIml implements UserService{
    @Override
    public User createEmpty() {
        return new User("Ann");
    }
}
