package com.zmy.zrpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    HESSIAN2(3),
    PROTOBUF(4);

    private final int code;
}
