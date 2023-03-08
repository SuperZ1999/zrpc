package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloObject;
import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.RpcClient;
import com.zmy.zrpc.core.RpcClientProxy;
import com.zmy.zrpc.core.netty.client.NettyClient;

public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient rpcClient = new NettyClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(111, "Hello Rpc!");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
