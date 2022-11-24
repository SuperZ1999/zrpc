package com.zmy.zrpc.test;

import com.zmy.zrpc.api.WorldService;
import com.zmy.zrpc.common.entity.HelloObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldServiceImpl implements WorldService {
    public static final Logger LOGGER = LoggerFactory.getLogger(WorldServiceImpl.class);

    @Override
    public String world(HelloObject helloObject) {
        LOGGER.debug("这是收到的消息：{}", helloObject.getMessage());
        return "这是调用的返回值：" + helloObject.getId();
    }
}
