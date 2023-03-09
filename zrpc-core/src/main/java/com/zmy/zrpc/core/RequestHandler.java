package com.zmy.zrpc.core;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(Object service, RpcRequest rpcRequest) {
        Object returnObject = null;
        try {
            returnObject = invokeTargetMethod(service, rpcRequest);
            logger.info("服务：{}成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return RpcResponse.success(returnObject, rpcRequest.getRequestId());
        } catch (NoSuchMethodException e) {
            logger.error("调用方法时发生错误：{}", e.toString());
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD, rpcRequest.getRequestId());
        }
    }

    private Object invokeTargetMethod(Object service, RpcRequest rpcRequest) throws NoSuchMethodException {
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            return returnObject;
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("调用方法时发生错误：{}", e.toString());
        }
        return null;
    }
}
