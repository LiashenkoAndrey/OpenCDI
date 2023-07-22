package cdi.util.abstractClasses;

import org.open.cdi.annotations.Component;

@Component
public class Dependency1 {

    public void show() {
        System.out.println("1");
    }
}
