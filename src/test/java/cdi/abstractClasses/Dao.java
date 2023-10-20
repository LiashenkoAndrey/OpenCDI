package cdi.abstractClasses;

import org.openCDI.annotations.Inject;

public abstract class Dao extends AbstractDAO {

    @Inject
    private Dependency1 dependency1;

    @Inject
    protected DaoUtils utils;
}
