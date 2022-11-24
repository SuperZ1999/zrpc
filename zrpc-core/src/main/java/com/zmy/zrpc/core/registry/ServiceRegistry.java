package com.zmy.zrpc.core.registry;

public interface ServiceRegistry {
    void register(Object service);

    Object getService(String serviceName);
}
