package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloObject;
import com.zmy.zrpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject helloObject) {
        LOGGER.info("接收到信息：" + helloObject.getMessage());
        return "这是返回值：id=" + helloObject.getId();
    }
}
