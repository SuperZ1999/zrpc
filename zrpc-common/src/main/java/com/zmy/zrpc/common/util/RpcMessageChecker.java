package com.zmy.zrpc.common.util;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.ResponseCode;
import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcMessageChecker {
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    public static void check(RpcRequest rpcRequest, RpcResponse<?> rpcResponse) {
        if (rpcResponse == null) {
            logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "调用方法：" + rpcRequest.getInterfaceName() + "#" + rpcRequest.getMethodName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, "调用方法：" + rpcRequest.getInterfaceName() + "#" + rpcRequest.getMethodName());
        }
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "调用方法：" + rpcRequest.getInterfaceName() + "#" + rpcRequest.getMethodName());
        }
    }
}
