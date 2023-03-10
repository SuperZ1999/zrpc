package com.zmy.zrpc.core.provider;

public interface ServiceProvider {
    <T> void addServiceProvider(T service);

    Object getServiceProvider(String serviceName);
}
