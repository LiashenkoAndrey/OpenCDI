package cdi.abstractClasses;

import org.junit.jupiter.api.Test;
import org.openCDI.ApplicationContext;
import org.openCDI.ApplicationContextFactory;
import org.openCDI.exceptions.ComponentNotFoundException;


import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDIWithAbstractClasses {

    @Test
    public void testDI() {

        ApplicationContext context = ApplicationContextFactory.getApplicationContext();
        context.loadFromPackages("cdi.abstractClasses");
        context.init();


        UserDao userDao = context.find(UserDao.class).orElseThrow(ComponentNotFoundException::new);
        DaoUtils daoUtils = context.find(DaoUtils.class).orElseThrow(ComponentNotFoundException::new);
        assertNotNull(userDao);
        assertNotNull(daoUtils);

        assertNotNull(daoUtils.getRandomLong());
        assertNotNull(userDao.utils);
        assertNotNull(userDao.abstractClassValue);
        assertNotNull(userDao.generateIdAndPrint());
    }



}
