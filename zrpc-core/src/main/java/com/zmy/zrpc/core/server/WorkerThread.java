package com.zmy.zrpc.core.server;

import com.zmy.zrpc.common.entity.HelloObject;
import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class WorkerThread implements Runnable{
    public static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private final Socket socket;
    private Object service;

    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        LOGGER.debug("已建立socket连接，等待消息中。。。");
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Method method = service.getClass().getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequest.getParameters());
//            objectOutputStream.writeObject(returnObject);
            LOGGER.debug("远程调用完成，回复消息中。。。");
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
            LOGGER.debug("已回复");
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
