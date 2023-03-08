package com.zmy.zrpc.test;

import com.zmy.zrpc.core.serializer.Hessian2Serializer;

public class Test {
    public static void main(String[] args) {
        Hessian2Serializer serializer = new Hessian2Serializer();
        byte[] bytes = serializer.serialize(123);
        Object o = serializer.deserialize(bytes, Integer.class);
        System.out.println(o);
    }
}
