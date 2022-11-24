package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import com.zmy.zrpc.core.registry.impl.DefaultServiceRegistry;
import com.zmy.zrpc.core.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        WorldServiceImpl worldService = new WorldServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        serviceRegistry.register(worldService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        LOGGER.debug("启动服务器。。。");
        rpcServer.start(888);
    }
}
