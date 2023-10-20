package cdi.abstractClasses;


import org.openCDI.annotations.Component;

@Component
public class UserDao extends Dao {

    public int i = 3;

    public Long generateIdAndPrint() {
        return utils.getRandomLong();
    }
}
