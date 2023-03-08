package com.zmy.zrpc.core.socket.server;

import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.common.util.ThreadPoolFactory;
import com.zmy.zrpc.core.RequestHandler;
import com.zmy.zrpc.core.RpcServer;
import com.zmy.zrpc.core.registry.DefaultServiceRegistry;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
    private final RequestHandler requestHandler = new RequestHandler();
    private CommonSerializer serializer;

    public SocketServer() {
        threadPool = ThreadPoolFactory.createDefaultThreadPool("zrpc");
    }

    public void start(int port) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动.....");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端已连接，ip为：" + socket.getInetAddress());
                threadPool.execute(new RequestHandlerThread(socket, serviceRegistry, requestHandler, serializer));
            }
        } catch (IOException e) {
            logger.error("启动服务器或者处理请求时发生错误：" + e);
        }
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
