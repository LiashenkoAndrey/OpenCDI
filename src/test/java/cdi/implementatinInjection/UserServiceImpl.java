package cdi.implementatinInjection;

import org.open.cdi.annotations.Component;

@Component

public class UserServiceImpl implements UserService {

    @Override
    public User createEmpty() {
        return new User("Bob");
    }

}
