package cdi.abstractClasses;

import org.openCDI.annotations.Inject;

public abstract class AbstractDAO {

    public Integer abstractClassValue = 10;

    @Inject
    protected Dependency1 dependency1;
}
