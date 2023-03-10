package com.zmy.zrpc.core.netty.client;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static final Bootstrap bootstrap = initializeBootstrap();
    private static Channel channel = null;
    private static final int MAX_RETRY_COUNT = 5;

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
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new CommonEncoder(serializer));
                ch.pipeline().addLast(new CommonDecoder());
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            connect(bootstrap, inetSocketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取Channel时有错误发生", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端成功连接{}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            if (retry == 0) {
                logger.error("客户端连接失败：重试次数已用完，放弃连接！");
                countDownLatch.countDown();
                // TODO: 2023/3/10 这里抛出异常有什么用？主线程会接收到吗？主线程接收到的channel是不是null？
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            int count = MAX_RETRY_COUNT - retry + 1;
            int delay = 1 << count;
            logger.error("{}:连接失败，第{}次重连……", new Date(), count);
            bootstrap.config().group().schedule(() -> {
                connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch);
            }, delay, TimeUnit.SECONDS);
        });
    }
}
