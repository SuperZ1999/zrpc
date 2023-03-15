package com.zmy.zrpc.core.transport.netty.server;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.factory.ThreadPoolFactory;
import com.zmy.zrpc.core.handler.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler requestHandler;


    public NettyServerHandler() {
        requestHandler = new RequestHandler();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接……");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            if (msg.getHeartBeat()) {
                logger.info("接收到客户端心跳包……");
                return;
            }
            logger.info("服务器接收到请求: {}", msg);
            Object response = requestHandler.handle(msg);
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(response);
            } else {
                logger.error("通道不可写");
            }
        } finally {
            // TODO: 2023/3/2 这是干嘛的？
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
