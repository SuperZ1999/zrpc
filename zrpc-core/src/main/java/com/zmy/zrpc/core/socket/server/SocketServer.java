package com.zmy.zrpc.core.socket.server;

import com.zmy.zrpc.core.RequestHandler;
import com.zmy.zrpc.core.RpcServer;
import com.zmy.zrpc.core.registry.DefaultServiceRegistry;
import com.zmy.zrpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 60;
    private ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
    private final RequestHandler requestHandler = new RequestHandler();

    public SocketServer() {
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<Runnable>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动.....");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端已连接，ip为：" + socket.getInetAddress());
                threadPool.execute(new RequestHandlerThread(socket, serviceRegistry, requestHandler));
            }
        } catch (IOException e) {
            logger.error("启动服务器或者处理请求时发生错误：" + e);
        }
    }
}