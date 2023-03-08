package com.zmy.zrpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcError {
    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    SERIALIZER_NOT_FOUND("找不到序列化器");

    private final String message;
}
