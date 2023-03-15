package com.zmy.zrpc.core.provider;

import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String, Object> serviceMap = new HashMap<>();

    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (serviceMap.containsKey(serviceName)) {
            return;
        }
        serviceMap.put(serviceName, service);
        logger.info("向接口：{}注册服务：{}", serviceName, service.getClass().getCanonicalName());
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
