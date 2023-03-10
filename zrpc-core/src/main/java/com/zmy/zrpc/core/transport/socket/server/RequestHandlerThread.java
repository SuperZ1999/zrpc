package com.zmy.zrpc.core.transport.socket.server;

import com.zmy.zrpc.common.entity.RpcRequest;
import com.zmy.zrpc.core.handler.RequestHandler;
import com.zmy.zrpc.core.provider.ServiceProvider;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import com.zmy.zrpc.core.transport.socket.util.ObjectReader;
import com.zmy.zrpc.core.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class RequestHandlerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object response = requestHandler.handle(rpcRequest);
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("处理请求线程发生错误：" + e);
        }
    }
}
