package org.openCDI;

import java.util.Map;
import java.util.Optional;

public interface ApplicationContext {


    void loadAll(Object... objects);

    void loadFromPackages(String... packages);

    void init();

    <T> Optional<T> find(Class<T> type);

    <T> Optional<T> find(Class<T> type, String name);

    Optional<Object> findByName(String name);

    Map<String, Object> getContext();

    void clearContext();

    int contextSize();
}
