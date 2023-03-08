package com.zmy.zrpc.core.socket.server;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.common.entity.RpcResponse;
import com.zmy.zrpc.core.RequestHandler;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import com.zmy.zrpc.core.socket.util.ObjectReader;
import com.zmy.zrpc.core.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class RequestHandlerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object returnObject = requestHandler.handle(service, rpcRequest);
            ObjectWriter.writeObject(outputStream, RpcResponse.success(returnObject), serializer);
        } catch (IOException e) {
            logger.error("处理请求线程发生错误：" + e);
        }
    }
}
