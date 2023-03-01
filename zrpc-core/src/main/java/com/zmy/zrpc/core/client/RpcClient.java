package com.zmy.zrpc.core.client;

import com.zmy.zrpc.common.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRequest(String host, int port, RpcRequest rpcRequest) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("发送rpc请求时发生错误：" + e);
            return null;
        }
    }
}
