package com.zmy.zrpc.core.transport.netty.server;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.factory.ThreadPoolFactory;
import com.zmy.zrpc.core.handler.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler requestHandler;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private final ExecutorService threadPool;


    public NettyServerHandler() {
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(() -> {
            try {
                logger.info("服务器接收到请求: {}", msg);
                Object response = requestHandler.handle(msg);
                ChannelFuture channelFuture = ctx.writeAndFlush(response);
                // 服务端如发送数据失败，就关闭channel
                channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } finally {
                // TODO: 2023/3/2 这是干嘛的？
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
