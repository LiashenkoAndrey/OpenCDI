package cdi.util.abstractClasses;

import org.open.cdi.annotations.Component;

import java.util.Random;

@Component
public class DaoUtils {

    public Long getRandomLong() {
        return new Random().nextLong(0, 300);
    }
}
