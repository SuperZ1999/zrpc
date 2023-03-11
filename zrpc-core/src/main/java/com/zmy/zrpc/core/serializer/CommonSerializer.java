package com.zmy.zrpc.core.serializer;

public interface CommonSerializer {
    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer HESSIAN2_SERIALIZER = 3;
    Integer PROTOBUF_SERIALIZER = 4;

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            // TODO: 2023/3/2 这里能用枚举类吗？
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new Hessian2Serializer();
            case 4:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }
}
