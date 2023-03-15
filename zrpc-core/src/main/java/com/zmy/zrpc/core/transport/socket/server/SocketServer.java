package com.zmy.zrpc.core.transport.socket.server;

import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.common.factory.SingletonFactory;
import com.zmy.zrpc.common.factory.ThreadPoolFactory;
import com.zmy.zrpc.core.handler.RequestHandler;
import com.zmy.zrpc.core.hook.ShutdownHook;
import com.zmy.zrpc.core.register.NacosServiceRegistry;
import com.zmy.zrpc.core.register.ServiceRegistry;
import com.zmy.zrpc.core.transport.AbstractRpcServer;
import com.zmy.zrpc.core.transport.RpcServer;
import com.zmy.zrpc.core.provider.ServiceProviderImpl;
import com.zmy.zrpc.core.provider.ServiceProvider;
import com.zmy.zrpc.core.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private ExecutorService threadPool;
    private final RequestHandler requestHandler = new RequestHandler();
    private final CommonSerializer serializer;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistry();
        this.threadPool = ThreadPoolFactory.createDefaultThreadPool("zrpc");
        this.serializer = CommonSerializer.getByCode(serializerCode);
    }

    public void start() {
        scanServices();
        SingletonFactory.getInstance(ShutdownHook.class).addClearAllHook();
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器正在启动.....");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端已连接，ip为：" + socket.getInetAddress());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("启动服务器或者处理请求时发生错误：" + e);
        }
    }
}
