package org.open.cdi;

public class ApplicationClassLoader extends ClassLoader {

    public ApplicationClassLoader() {
        super(ApplicationClassLoader.class.getClassLoader());
    }




}
