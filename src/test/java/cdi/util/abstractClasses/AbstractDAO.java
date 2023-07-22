package cdi.util.abstractClasses;

import org.open.cdi.annotations.Inject;

public abstract class AbstractDAO {

    public Integer abstractClassValue = 10;

    @Inject
    protected Dependency1 dependency1;
}
