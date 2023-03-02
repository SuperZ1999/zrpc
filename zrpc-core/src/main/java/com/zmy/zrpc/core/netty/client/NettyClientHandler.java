package com.zmy.zrpc.core.netty.client;

import com.zmy.zrpc.common.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse<Object>> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse<Object> msg) throws Exception {
        try {
            logger.info("客户端收到响应：{}", msg);
            AttributeKey<RpcResponse<Object>> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg);
            // 把响应放进Attribute之后马上关闭channel，channel关闭后sendRequest方法中的阻塞点就会放开，向下运行
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时发生错误：");
        cause.printStackTrace();
        ctx.close();
    }
}
