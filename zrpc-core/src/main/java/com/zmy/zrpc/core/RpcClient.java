package com.zmy.zrpc.core;

import com.zmy.zrpc.common.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(String host, int port, RpcRequest rpcRequest);
}
