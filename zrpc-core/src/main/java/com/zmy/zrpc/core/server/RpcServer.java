package com.zmy.zrpc.core.server;

import com.zmy.zrpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;

    public RpcServer(ServiceRegistry serviceRegistry) {
        // TODO 改成常量
        threadPool = new ThreadPoolExecutor(5, 50, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100), Executors.defaultThreadFactory());
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
