package cdi.util.abstractClasses;

import cdi.util.inheritance.ChildBean;
import org.junit.jupiter.api.Test;
import org.open.cdi.BeanManager;
import org.open.cdi.exceptions.DIContainerUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDIWithAbstractClasses {

    @Test
    public void testDI() {

        BeanManager manager = new BeanManager();
        manager.loadFromPackages("cdi.util.abstractClasses");
        manager.init();


        UserDao userDao = (UserDao) manager.find("UserDao");
        DaoUtils daoUtils = (DaoUtils) manager.find("DaoUtils");
        assertNotNull(userDao);
        assertNotNull(daoUtils);

        assertNotNull(daoUtils.getRandomLong());
        assertNotNull(userDao.utils);
        assertNotNull(userDao.abstractClassValue);
        assertNotNull(userDao.generateIdAndPrint());
    }



}
