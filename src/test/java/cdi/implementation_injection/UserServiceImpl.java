package cdi.implementation_injection;

import org.openCDI.annotations.Component;

@Component

public class UserServiceImpl implements UserService {

    @Override
    public User createEmpty() {
        return new User("Bob");
    }

}
