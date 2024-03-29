package com.zmy.zrpc.core.transport.netty.client;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.common.factory.SingletonFactory;
import com.zmy.zrpc.common.util.RpcMessageChecker;
import com.zmy.zrpc.core.loadbalancer.LoadBalancer;
import com.zmy.zrpc.core.loadbalancer.RandomLoadBalancer;
import com.zmy.zrpc.core.register.NacosServiceDiscovery;
import com.zmy.zrpc.core.register.NacosServiceRegistry;
import com.zmy.zrpc.core.register.ServiceDiscovery;
import com.zmy.zrpc.core.register.ServiceRegistry;
import com.zmy.zrpc.core.transport.RpcClient;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final CommonSerializer serializer;
    private final ServiceDiscovery serviceDiscovery;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializerCode) {
        this(serializerCode, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializerCode, LoadBalancer loadBalancer) {
        serializer = CommonSerializer.getByCode(serializerCode);
        serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
        AtomicReference<Object> result = new AtomicReference<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (channel != null && channel.isActive()) {
                unprocessedRequests.put(rpcRequest.getRequestId(), completableFuture);
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        logger.info("客户端发送消息: {}", rpcRequest);
                    } else {
                        future.channel().close();
                        completableFuture.completeExceptionally(future.cause());
                        logger.error("发送消息失败，原因：" + future.cause());
                    }
                });
//                // 这里会阻塞，channel被关闭后就会继续向下运行
//                channel.closeFuture().sync();
//                // 关闭group，否则由于group没关闭，group的线程没关闭，client就不能停止
//                channel.eventLoop().parent().shutdownGracefully();
//                // TODO: 2023/3/2 这到底是什么东西？目前来看是一个全局的map
//                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
//                RpcResponse rpcResponse = channel.attr(key).get();
//                RpcMessageChecker.check(rpcRequest, rpcResponse);
//                // TODO: 2023/3/10 这里为什么要用原子类？
//                result.set(rpcResponse.getData());
            } else {
                System.exit(0);
            }
        } catch (Exception e) {
            logger.error("发送消息时发生错误：" + e);
            unprocessedRequests.remove(rpcRequest.getRequestId());
            // TODO: 2023/3/11 这里有什么作用？
            Thread.currentThread().interrupt();
        }
        return completableFuture;
    }
}
