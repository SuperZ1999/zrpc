package com.zmy.zrpc.core.netty.server;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.core.registry.DefaultServiceRegistry;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import com.zmy.zrpc.core.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
    private static RequestHandler requestHandler = new RequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            logger.info("服务器接收到请求: {}", msg);
            String interfaceName = msg.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object returnObject = requestHandler.handle(service, msg);
            ChannelFuture channelFuture = ctx.writeAndFlush(RpcResponse.success(returnObject));
            // 服务端发完数据就关闭了
            channelFuture.addListener(ChannelFutureListener.CLOSE);
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
