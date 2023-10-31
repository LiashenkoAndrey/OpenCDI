package cdi.injectionTest;

import org.openCDI.annotations.Component;
import org.openCDI.annotations.Service;

@Component
public class ServiceImpl {

    public void processData(String data) {
        System.out.println("processing data ...");
    }
}
