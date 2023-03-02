package com.zmy.zrpc.core.registry;

public interface ServiceRegistry {
    <T> void register(T service);

    Object getService(String serviceName);
}
