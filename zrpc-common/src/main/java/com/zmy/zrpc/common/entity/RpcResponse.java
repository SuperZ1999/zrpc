package com.zmy.zrpc.common.entity;

import com.zmy.zrpc.common.enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {
    private Integer statusCode;
    private String message;
    private T data;
    private String requestId;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(ResponseCode.SUCCESS.getCode());
        rpcResponse.setData(data);
        rpcResponse.setRequestId(requestId);
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(ResponseCode responseCode, String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(responseCode.getCode());
        rpcResponse.setMessage(responseCode.getMessage());
        rpcResponse.setRequestId(requestId);
        return rpcResponse;
    }
}
