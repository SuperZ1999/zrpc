package com.zmy.zrpc.core.codec;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.PackageType;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommonEncoder extends MessageToByteEncoder<Object> {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else if (msg instanceof RpcResponse) {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        } else {
            // TODO: 2023/3/2 抛出错误
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
