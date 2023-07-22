package cdi.util;

import org.open.cdi.annotations.Component;
import org.open.cdi.annotations.Inject;

@Component
public class Controller {

    @Inject
    UserService userService;


}
