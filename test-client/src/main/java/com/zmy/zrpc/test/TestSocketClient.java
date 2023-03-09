package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloObject;
import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.RpcClient;
import com.zmy.zrpc.core.RpcClientProxy;
import com.zmy.zrpc.core.netty.client.NettyClient;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import com.zmy.zrpc.core.serializer.KryoSerializer;
import com.zmy.zrpc.core.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        RpcClient rpcClient = new SocketClient("127.0.0.1", 9000);
        rpcClient.setSerializer(CommonSerializer.getByCode(4));
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(111, "Hello Rpc!");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
