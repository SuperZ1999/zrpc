package com.zmy.zrpc.core.client;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(RpcClientProxy.class);

    String host;
    int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        LOGGER.debug("调用函数：{}", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        return ((RpcResponse<?>) RpcClient.sendRequest(rpcRequest, host, port)).getData();
    }
}
