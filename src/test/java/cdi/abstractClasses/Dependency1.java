package cdi.abstractClasses;

import org.openCDI.annotations.Component;

@Component
public class Dependency1 {

    public void show() {
        System.out.println("1");
    }
}
