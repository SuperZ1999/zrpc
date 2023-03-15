package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloObject;
import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.loadbalancer.RoundRobinLoadBalancer;
import com.zmy.zrpc.core.transport.RpcClient;
import com.zmy.zrpc.core.transport.RpcClientProxy;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import com.zmy.zrpc.core.transport.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        RpcClient rpcClient = new SocketClient(new RoundRobinLoadBalancer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(222, "Hello Rpc!");
        for (int i = 0; i < 20; i++) {
            String res = helloService.hello(helloObject);
            System.out.println(res);
        }
    }
}
