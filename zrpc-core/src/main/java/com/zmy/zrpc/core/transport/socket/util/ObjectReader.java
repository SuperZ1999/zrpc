package com.zmy.zrpc.core.transport.socket.util;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.PackageType;
import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ObjectReader {
    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream inputStream) throws IOException {
        byte[] numberBytes = new byte[4];
        inputStream.read(numberBytes);
        int magic = bytesToInt(numberBytes);
        if (magic != MAGIC_NUMBER) {
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        inputStream.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        inputStream.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        inputStream.read(numberBytes);
        int length = bytesToInt(numberBytes);
        byte[] bytes = new byte[length];
        inputStream.read(bytes);
        return serializer.deserialize(bytes, packageClass);
    }

    private static int bytesToInt(byte[] bytes) {
        return (bytes[3] & 0xFF)
                | ((bytes[2] & 0xFF) << 8)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[0] & 0xFF) << 24);
    }
}
