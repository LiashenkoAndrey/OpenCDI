# Dependency Injection Container

Open CDI is a simple library, basic realization of Dependency Injection technique

## Installation

```xml
 <repositories>
      <repository>
          <id>OpenCDI</id>
          <url>https://github.com/LiashenkoAndrey/OpenCDI.git</url>
      </repository>
  </repositories>
```

## Usage

Dependency
```java
import org.openCDI.annotations.Component;

@Component
public class ServiceImpl {

    public void processData(String data) {
        System.out.println("processing data ...");
    }
}
```
Client code with dependency

```java
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
```

Package structure
![The San Juan Mountains are beautiful!](/packageStucture.png)

Usage
```java
import org.junit.jupiter.api.Test;
import org.openCDI.ApplicationContextFactory;

import java.util.Optional;

public class BeanInjectionTest {
    @Test
    public void injectionTest() {
        var context = ApplicationContextFactory.getApplicationContext(BeanInjectionTest.class);
        context.init();

        Optional<Controller> controller = context.find(Controller.class);
        controller.get().goSomething();
    }
}
```

Output
```java
processing data ...
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)