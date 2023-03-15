package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.annotation.ServiceScan;
import com.zmy.zrpc.core.transport.RpcServer;
import com.zmy.zrpc.core.transport.netty.server.NettyServer;
import com.zmy.zrpc.core.provider.ServiceProviderImpl;
import com.zmy.zrpc.core.provider.ServiceProvider;
import com.zmy.zrpc.core.serializer.CommonSerializer;

@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1", 9000, CommonSerializer.KRYO_SERIALIZER);
        rpcServer.start();
    }
}
