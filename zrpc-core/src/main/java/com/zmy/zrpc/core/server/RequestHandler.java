package com.zmy.zrpc.core.server;

import com.zmy.zrpc.common.entity.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    public Object handle(Object service, RpcRequest rpcRequest) {
        return invokeTargetMethod(service, rpcRequest);
    }

    private Object invokeTargetMethod(Object service, RpcRequest rpcRequest) {
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            return returnObject;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
