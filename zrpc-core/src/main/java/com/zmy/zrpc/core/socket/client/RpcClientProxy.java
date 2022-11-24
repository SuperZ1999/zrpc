package com.zmy.zrpc.core.socket.client;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        String methodName = method.getName();
        LOGGER.debug("调用函数：{}", methodName);
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(methodName)
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        return ((RpcResponse<?>) SocketRpcClient.sendRequest(rpcRequest, host, port)).getData();
    }
}
