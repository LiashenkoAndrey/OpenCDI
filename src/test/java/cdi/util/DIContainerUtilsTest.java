package cdi.util;

import cdi.util.abstractClasses.UserDao;
import org.junit.jupiter.api.Test;
import org.open.cdi.exceptions.DIContainerUtils;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DIContainerUtilsTest {



    @Test
    public void recursiveFindTest() {
        DIContainerUtils utils = new DIContainerUtils();
        List<Field> fieldList = utils.getAllInjectableFieldsRecursively(UserDao.class);
        assertEquals(3, fieldList.size());
    }
}
