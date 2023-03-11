package com.zmy.zrpc.common.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SingletonFactory {
    private static final Map<Class<?>, Object> objectMap = new HashMap<>();

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        if (instance == null) {
            synchronized (SingletonFactory.class) {
                if (instance == null) {
                    try {
                        Constructor<T> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        instance = constructor.newInstance();
                        objectMap.put(clazz, instance);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
