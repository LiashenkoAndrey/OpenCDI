package cdi.util.abstractClasses;


import org.open.cdi.annotations.DIBean;

@DIBean
public class UserDao extends Dao {

    public int i = 3;

    public Long generateIdAndPrint() {
        return utils.getRandomLong();
    }
}
