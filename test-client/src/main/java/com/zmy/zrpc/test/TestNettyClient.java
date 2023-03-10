package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloObject;
import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.transport.RpcClient;
import com.zmy.zrpc.core.transport.RpcClientProxy;
import com.zmy.zrpc.core.transport.netty.client.NettyClient;
import com.zmy.zrpc.core.serializer.CommonSerializer;

public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient rpcClient = new NettyClient();
        rpcClient.setSerializer(CommonSerializer.getByCode(4));
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(111, "Hello Rpc!");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
