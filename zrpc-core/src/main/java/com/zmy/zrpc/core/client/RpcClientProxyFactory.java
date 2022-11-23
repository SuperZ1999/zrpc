package com.zmy.zrpc.core.client;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class RpcClientProxyFactory {
    private static final Map<String, RpcClientProxy> proxies = new HashMap<>();

    public static <T> T getProxyInstance(Class<?> clazz, String host, int port) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, getProxy(host, port));
    }

    private static RpcClientProxy getProxy(String host, int port) {
        String key = host + ":" + port;
        if (proxies.containsKey(key)) {
            return proxies.get(key);
        }
        RpcClientProxy rpcClientProxy = new RpcClientProxy(host, port);
        proxies.put(key, rpcClientProxy);
        return rpcClientProxy;
    }
}
