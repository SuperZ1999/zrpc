package com.zmy.zrpc.core.socket.client;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.core.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketRpcClient implements RpcClient {
    public static final Logger LOGGER = LoggerFactory.getLogger(RpcRequest.class);

    public static Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            LOGGER.debug("发送request。。。");
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            LOGGER.debug("发送request完成，等待回复。。。");
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
