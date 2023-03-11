package com.zmy.zrpc.common.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@NoArgsConstructor
public class ThreadPoolFactory {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private static final Map<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        ExecutorService pool = threadPoolMap.computeIfAbsent(threadNamePrefix, key -> createThreadPool(threadNamePrefix, daemon));
        if (pool.isShutdown() || pool.isTerminated()) {
            threadPoolMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPoolMap.put(threadNamePrefix, pool);
        }
        return pool;
    }

    public static void shutdownAll() {
        logger.info("关闭所有线程池......");
        threadPoolMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            try {
                boolean isTerminated = executorService.awaitTermination(10, TimeUnit.SECONDS);
                if (isTerminated) {
                    logger.info("线程池 [{}] 已关闭", entry.getKey());
                } else {
                    logger.info("线程池 [{}] 关闭超时", entry.getKey());
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.error("关闭线程池失败");
                executorService.shutdownNow();
            }
        });
    }

    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }

    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        if (threadNamePrefix != null) {
            builder.setNameFormat(threadNamePrefix + "-%d");
        }
        builder.setDaemon(daemon);
        return builder.build();
    }
}
