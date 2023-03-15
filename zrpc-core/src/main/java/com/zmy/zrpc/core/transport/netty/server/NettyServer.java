package com.zmy.zrpc.core.transport.netty.server;

import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.common.factory.SingletonFactory;
import com.zmy.zrpc.core.hook.ShutdownHook;
import com.zmy.zrpc.core.provider.ServiceProvider;
import com.zmy.zrpc.core.provider.ServiceProviderImpl;
import com.zmy.zrpc.core.register.NacosServiceRegistry;
import com.zmy.zrpc.core.register.ServiceRegistry;
import com.zmy.zrpc.core.transport.AbstractRpcServer;
import com.zmy.zrpc.core.transport.RpcServer;
import com.zmy.zrpc.core.codec.CommonDecoder;
import com.zmy.zrpc.core.codec.CommonEncoder;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyServer extends AbstractRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistry();
        this.serializer = CommonSerializer.getByCode(serializerCode);
    }

    @Override
    public void start() {
        scanServices();
        SingletonFactory.getInstance(ShutdownHook.class).addClearAllHook();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(2);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                // TODO 这是什么？
                .handler(new LoggingHandler(LogLevel.INFO))
                // TODO 这几个选项都是什么？
                .option(ChannelOption.SO_BACKLOG, 256)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                // TODO 为什么是SocketChannel?
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new CommonEncoder(serializer));
                        ch.pipeline().addLast(new CommonDecoder());
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时，发生错误：" + e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
