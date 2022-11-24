package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.api.WorldService;
import com.zmy.zrpc.common.entity.HelloObject;
import com.zmy.zrpc.core.socket.client.RpcClientProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient {
    public static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);

    public static void main(String[] args) {
        HelloService helloService = RpcClientProxyFactory.getProxyInstance(HelloService.class, "127.0.0.1", 888);
        WorldService worldService = RpcClientProxyFactory.getProxyInstance(WorldService.class, "127.0.0.1", 888);
        LOGGER.debug("开始发送信息。。。");
        String res = helloService.hello(new HelloObject(1, "hello!"));
        LOGGER.debug("收到回复：{}", res);
        res = worldService.world(new HelloObject(2, "world!"));
        LOGGER.debug("收到回复：{}", res);
    }
}
