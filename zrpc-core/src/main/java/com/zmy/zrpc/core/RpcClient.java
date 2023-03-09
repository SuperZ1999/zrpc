package com.zmy.zrpc.core;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.core.serializer.CommonSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);
}
