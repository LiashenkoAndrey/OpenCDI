package cdi.util.abstractClasses;

import org.open.cdi.annotations.Inject;

public abstract class Dao extends AbstractDAO {

    @Inject
    private Dependency1 dependency1;

    @Inject
    protected DaoUtils utils;
}
