package cdi.applicationContextTest;

import cdi.abstractClasses.UserDao;
import org.junit.jupiter.api.Test;
import org.openCDI.util.ApplicationContextUtils;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationContextUtilsTest {



    @Test
    public void recursiveFindTest() {
        ApplicationContextUtils utils = new ApplicationContextUtils();
        List<Field> fieldList = utils.getAllInjectableFieldsRecursively(UserDao.class);
        assertEquals(3, fieldList.size());
    }
}
