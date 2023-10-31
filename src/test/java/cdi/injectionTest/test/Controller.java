package cdi.injectionTest.test;

public class Controller {

    private final ServiceImpl service = new ServiceImpl();

    public void doSomething() {
        String data = "data";
        service.processData(data);
    }
}
