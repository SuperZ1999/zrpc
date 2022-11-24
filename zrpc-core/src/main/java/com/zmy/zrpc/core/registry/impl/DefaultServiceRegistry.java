package com.zmy.zrpc.core.registry.impl;

import com.zmy.zrpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultServiceRegistry implements ServiceRegistry {
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    private final Map<String, Object> serviceMap = new HashMap<>();
    private final Set<String> registeredService = new HashSet<>();

    // TODO 上锁
    @Override
    public void register(Object service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            LOGGER.debug("{}服务已经注册过了", serviceName);
            return;
        }
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        // TODO 无接口异常
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        LOGGER.debug("注册{}接口的的服务:{}", interfaces, serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        // TODO 无服务异常
        return serviceMap.get(serviceName);
    }
}
