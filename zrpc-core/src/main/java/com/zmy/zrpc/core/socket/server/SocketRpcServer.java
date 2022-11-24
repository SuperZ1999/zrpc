package com.zmy.zrpc.core.socket.server;

import com.zmy.zrpc.core.RpcServer;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketRpcServer implements RpcServer{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;

    public SocketRpcServer(ServiceRegistry serviceRegistry) {
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY), Executors.defaultThreadFactory());
        this.serviceRegistry = serviceRegistry;
    }

    public void start(int port) {
        LOGGER.debug("启动服务器。。。");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                threadPool.execute(new RequestHandlerThread(socket, serviceRegistry, new RequestHandler()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
