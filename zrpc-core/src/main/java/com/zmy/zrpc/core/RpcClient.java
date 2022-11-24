package com.zmy.zrpc.core;

import com.zmy.zrpc.common.entity.RpcRequest;

public interface RpcClient {
    static Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        // 具体由实现类实现
        return null;
    }
}
