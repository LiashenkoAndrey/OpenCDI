package cdi.util.abstractClasses;

import org.open.cdi.annotations.InjectBean;

public abstract class Dao extends AbstractDAO {

    @InjectBean
    private Dependency1 dependency1;

    @InjectBean
    protected DaoUtils utils;
}
