package com.zmy.zrpc.test;

import com.zmy.zrpc.core.RpcServer;
import com.zmy.zrpc.core.netty.server.NettyServer;
import com.zmy.zrpc.core.registry.DefaultServiceRegistry;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import com.zmy.zrpc.core.serializer.KryoSerializer;
import com.zmy.zrpc.core.socket.server.SocketServer;

public class TestScoketServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        SocketServer rpcServer = new SocketServer();
        rpcServer.setSerializer(new KryoSerializer());
        rpcServer.start(9000);
    }
}
