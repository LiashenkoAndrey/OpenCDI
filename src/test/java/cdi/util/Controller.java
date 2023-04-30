package cdi.util;

import org.open.cdi.annotations.DIBean;
import org.open.cdi.annotations.InjectBean;

@DIBean
public class Controller {

    @InjectBean
    UserService userService;


}
