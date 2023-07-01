package cdi.util.abstractClasses;

import org.open.cdi.annotations.InjectBean;

public abstract class AbstractDAO {

    public Integer abstractClassValue = 10;

    @InjectBean
    protected Dependency1 dependency1;
}
