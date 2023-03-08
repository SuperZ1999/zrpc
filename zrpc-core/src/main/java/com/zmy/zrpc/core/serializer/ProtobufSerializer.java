package com.zmy.zrpc.core.serializer;

import com.zmy.zrpc.common.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(ProtobufSerializer.class);

    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        return null;
    }

    @Override
    public int getCode() {
        return SerializerCode.PROTOBUF.getCode();
    }
}
