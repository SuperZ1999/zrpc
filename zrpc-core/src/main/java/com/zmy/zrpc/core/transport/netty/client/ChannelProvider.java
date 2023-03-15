package com.zmy.zrpc.core.transport.netty.client;

import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.core.codec.CommonDecoder;
import com.zmy.zrpc.core.codec.CommonEncoder;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static final Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    private static Bootstrap initializeBootstrap() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if(channels.containsKey(key)){
            Channel channel = channels.get(key);
            if(channel != null && channel.isActive()){
                return channel;
            }else {
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new CommonEncoder(serializer));
                ch.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                ch.pipeline().addLast(new CommonDecoder());
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });

        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("获取Channel时有错误发生", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();

        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端成功连接{}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
               completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
}
