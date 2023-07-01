package cdi.util.abstractClasses;

import org.open.cdi.annotations.DIBean;

import java.util.Random;

@DIBean
public class DaoUtils {

    public Long getRandomLong() {
        return new Random().nextLong(0, 300);
    }
}
