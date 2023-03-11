package com.zmy.zrpc.core.hook;

import com.zmy.zrpc.common.factory.ThreadPoolFactory;
import com.zmy.zrpc.common.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private ShutdownHook() {
    }

    public void addClearAllHook() {
        logger.info("添加服务端关闭前自动注销所有服务和关闭线程池的钩子");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("程序关闭，正在执行关闭钩子。。。");
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutdownAll();
        }));
    }
}
