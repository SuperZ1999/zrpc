package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.transport.RpcServer;
import com.zmy.zrpc.core.provider.ServiceProviderImpl;
import com.zmy.zrpc.core.provider.ServiceProvider;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import com.zmy.zrpc.core.transport.socket.server.SocketServer;

public class TestScoketServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        RpcServer rpcServer = new SocketServer("127.0.0.1", 9000);
        rpcServer.setSerializer(CommonSerializer.getByCode(4));
        rpcServer.publishService(helloService, HelloService.class);
    }
}
