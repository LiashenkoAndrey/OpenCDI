package cdi.util.abstractClasses;

import org.open.cdi.annotations.DIBean;

@DIBean
public class Dependency1 {

    public void show() {
        System.out.println("1");
    }
}
