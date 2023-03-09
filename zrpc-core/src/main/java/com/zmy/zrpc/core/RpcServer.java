package com.zmy.zrpc.core;

import com.zmy.zrpc.core.serializer.CommonSerializer;

public interface RpcServer {
    void start(int port);

    void setSerializer(CommonSerializer serializer);
}
