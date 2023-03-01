package com.zmy.zrpc.test;

import com.zmy.zrpc.core.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9000);
    }
}
