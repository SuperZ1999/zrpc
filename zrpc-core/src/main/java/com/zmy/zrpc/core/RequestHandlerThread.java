package com.zmy.zrpc.core;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import com.zmy.zrpc.core.socket.server.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandlerThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;

    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object returnObject = requestHandler.handle(service, rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("处理请求线程发生错误：" + e);
        }
    }
}
