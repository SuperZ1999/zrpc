package com.zmy.zrpc.core.transport;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.core.serializer.CommonSerializer;

public interface RpcClient {
    int DEFAULT_SERIALIZER = CommonSerializer.PROTOBUF_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);
}
