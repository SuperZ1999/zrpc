package com.zmy.zrpc.test;

import com.zmy.zrpc.api.ByeService;
import com.zmy.zrpc.core.annotation.Service;

@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye," + name;
    }

}