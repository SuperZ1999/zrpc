package com.zmy.zrpc.common.entity;

import com.zmy.zrpc.common.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {
    private Integer statusCode;
    private String message;
    private T data;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setData(data);
        rpcResponse.setStatusCode(ResponseCode.SUCCESS.getCode());
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(ResponseCode responseCode) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatusCode(responseCode.getCode());
        rpcResponse.setMessage(responseCode.getMessage());
        return rpcResponse;
    }
}
