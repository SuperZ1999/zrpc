package com.zmy.zrpc.core.netty.client;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.common.util.RpcMessageChecker;
import com.zmy.zrpc.core.RpcClient;
import com.zmy.zrpc.core.codec.CommonDecoder;
import com.zmy.zrpc.core.codec.CommonEncoder;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    private final String host;
    private final Integer port;
    private CommonSerializer serializer;

    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new CommonEncoder(serializer));
                ch.pipeline().addLast(new CommonDecoder());
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            logger.info("客户端连接到服务器 {}:{}", host, port);
            Channel channel = channelFuture.channel();
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("客户端发送消息: {}", rpcRequest);
                    } else {
                        logger.error("发送消息失败，原因：" + future.cause());
                    }
                });
                // 这里会阻塞，channel被关闭后就会继续向下运行
                channel.closeFuture().sync();
                // 关闭group，否则由于group没关闭，group的线程没关闭，client就不能停止
                bootstrap.config().group().shutdownGracefully();
                // TODO: 2023/3/2 这到底是什么东西？目前来看是一个全局的map
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest, rpcResponse);
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时发生错误：" + e);
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
