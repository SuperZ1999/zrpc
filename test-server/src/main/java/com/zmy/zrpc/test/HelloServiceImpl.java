package com.zmy.zrpc.test;

import com.zmy.zrpc.api.HelloObject;
import com.zmy.zrpc.api.HelloService;
import com.zmy.zrpc.core.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject helloObject) {
        logger.info("接收到信息：" + helloObject.getMessage());
        return "这是返回值：id=" + helloObject.getId();
    }
}
