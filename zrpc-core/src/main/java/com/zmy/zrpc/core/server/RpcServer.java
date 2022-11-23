package com.zmy.zrpc.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServer {
    public static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private final ExecutorService threadPool;

    public RpcServer() {
        threadPool = new ThreadPoolExecutor(5, 50, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100), Executors.defaultThreadFactory());
    }

    public void register(Object service, int port) {
        LOGGER.debug("启动服务器。。。");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                WorkerThread workerThread = new WorkerThread(socket, service);
                threadPool.execute(workerThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
