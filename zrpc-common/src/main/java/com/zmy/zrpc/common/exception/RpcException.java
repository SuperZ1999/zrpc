package com.zmy.zrpc.common.exception;

import com.zmy.zrpc.common.enumeration.RpcError;

public class RpcException extends RuntimeException{
    public RpcException() {
        super("rpc调用错误");
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }

    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
