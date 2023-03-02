package com.zmy.zrpc.core.serializer;

public interface CommonSerializer {
    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            // TODO: 2023/3/2 这里能用枚举类吗？
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
