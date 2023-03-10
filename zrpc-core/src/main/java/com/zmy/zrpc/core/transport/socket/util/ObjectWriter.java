package com.zmy.zrpc.core.transport.socket.util;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.PackageType;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class ObjectWriter {
    private static final Logger logger = LoggerFactory.getLogger(ObjectWriter.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static void writeObject(OutputStream outputStream, Object object, CommonSerializer serializer) throws IOException {
        outputStream.write(intToBytes(MAGIC_NUMBER));
        if (object instanceof RpcRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else if (object instanceof RpcResponse) {
            outputStream.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        } else {
            // TODO: 2023/3/8 抛出错误
        }
        outputStream.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();
    }

    private static byte[] intToBytes(int value) {
        byte[] des = new byte[4];
        des[0] = (byte) ((value >> 24) & 0xFF);
        des[1] = (byte) ((value >> 16) & 0xFF);
        des[2] = (byte) ((value >> 8) & 0xFF);
        des[3] = (byte) ((value) & 0xFF);
        return des;
    }
}
