package com.zmy.zrpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializerCode {
    JSON(1);

    private final int code;
}
