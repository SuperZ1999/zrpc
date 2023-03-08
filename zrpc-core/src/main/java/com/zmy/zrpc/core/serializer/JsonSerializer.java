package com.zmy.zrpc.core.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        Class<?>[] paramTypes = rpcRequest.getParamTypes();
        Object[] parameters = rpcRequest.getParameters();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            if (!paramType.isAssignableFrom(parameters[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(parameters[i]);
                parameters[i] = objectMapper.readValue(bytes, paramType);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        // TODO: 2023/3/8 这里有什么区别？
        return SerializerCode.JSON.getCode();
//        return SerializerCode.valueOf("JSON").getCode();
    }
}
