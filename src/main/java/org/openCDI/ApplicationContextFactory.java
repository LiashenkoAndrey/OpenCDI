package org.openCDI;

import org.openCDI.impl.ApplicationContextImpl;


public class ApplicationContextFactory {

    public static ApplicationContext getApplicationContext() {
        ApplicationContext context = new ApplicationContextImpl();
        context.loadAll(context);
        return context;
    }

    public static ApplicationContext getApplicationContext(Class<?> clazz) {
        ApplicationContext context = new ApplicationContextImpl();
        context.loadFromPackages(clazz.getPackageName());
        context.loadAll(context);
        return context;
    }

}
