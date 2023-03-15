package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.annotation.ServiceScan;
import com.zmy.zrpc.core.transport.RpcServer;
import com.zmy.zrpc.core.provider.ServiceProviderImpl;
import com.zmy.zrpc.core.provider.ServiceProvider;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import com.zmy.zrpc.core.transport.socket.server.SocketServer;

@ServiceScan
public class TestSocketServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new SocketServer("127.0.0.1", 9001);
        rpcServer.start();
    }
}
