package org.open.cdi;

public interface ApplicationContext {

    <T> T getBean(T type);

    <T> T getBean(T type, String name);


}
