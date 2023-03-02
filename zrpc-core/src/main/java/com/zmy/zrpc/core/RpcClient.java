package com.zmy.zrpc.core;

import com.zmy.zrpc.common.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}
