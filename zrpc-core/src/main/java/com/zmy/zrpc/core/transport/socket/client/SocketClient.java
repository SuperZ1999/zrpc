package com.zmy.zrpc.core.transport.socket.client;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.common.util.RpcMessageChecker;
import com.zmy.zrpc.core.register.NacosServiceRegistry;
import com.zmy.zrpc.core.register.ServiceRegistry;
import com.zmy.zrpc.core.transport.RpcClient;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import com.zmy.zrpc.core.transport.socket.util.ObjectReader;
import com.zmy.zrpc.core.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private CommonSerializer serializer;
    private final ServiceRegistry serviceRegistry;

    public SocketClient() {
        serviceRegistry = new NacosServiceRegistry();
    }

    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            RpcResponse rpcResponse = (RpcResponse) ObjectReader.readObject(inputStream);
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            logger.error("发送rpc请求时发生错误：" + e);
            throw new RpcException("服务调用失败：", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
