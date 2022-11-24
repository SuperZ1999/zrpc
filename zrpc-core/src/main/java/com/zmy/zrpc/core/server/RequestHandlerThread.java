package com.zmy.zrpc.core.server;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandlerThread implements Runnable {
    public static final Logger LOGGER = LoggerFactory.getLogger(RequestHandlerThread.class);

    private final Socket socket;
    private final ServiceRegistry serviceRegistry;
    private final RequestHandler requestHandler;

    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        LOGGER.debug("已建立socket连接，等待消息中。。。");
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object service = serviceRegistry.getService(rpcRequest.getInterfaceName());
            Object returnObject = requestHandler.handle(rpcRequest, service);
//            Method method = service.getClass().getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
//            Object returnObject = method.invoke(service, rpcRequest.getParameters());
            LOGGER.debug("远程调用完成，回复消息中。。。");
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
            LOGGER.debug("已回复");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

