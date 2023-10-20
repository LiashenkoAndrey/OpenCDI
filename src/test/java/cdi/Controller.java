package cdi;

import org.openCDI.annotations.Component;
import org.openCDI.annotations.Inject;

@Component
public class Controller {

    @Inject
    UserService userService;


}
