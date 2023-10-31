package cdi.injectionTest;

import org.openCDI.annotations.Component;
import org.openCDI.annotations.Inject;

@Component
public class Controller {

    @Inject
    public ServiceImpl service;

    public void goSomething() {
        String data = "data";
        service.processData(data);
    }
}
