package com.zmy.zrpc.test;

import com.zmy.zrpc.core.RpcServer;
import com.zmy.zrpc.core.netty.server.NettyServer;
import com.zmy.zrpc.core.registry.DefaultServiceRegistry;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import com.zmy.zrpc.core.socket.server.SocketServer;

public class TestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new NettyServer();
        rpcServer.start(9000);
    }
}
