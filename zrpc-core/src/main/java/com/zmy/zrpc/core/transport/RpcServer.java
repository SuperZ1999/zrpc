package com.zmy.zrpc.core.transport;

import com.zmy.zrpc.core.serializer.CommonSerializer;

public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.PROTOBUF_SERIALIZER;

    void start();

    <T> void publishService(T service, String serviceName);
}
