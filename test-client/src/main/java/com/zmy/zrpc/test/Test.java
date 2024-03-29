package com.zmy.zrpc.test;

import com.zmy.zrpc.core.serializer.Hessian2Serializer;

import java.util.Arrays;
import java.util.HashSet;

public class Test {
    public static void main(String[] args) {

    }

//    private static int bytesToInt(byte[] src) {
//        int value;
//        value = (src[0] & 0xFF)
//                | ((src[1] & 0xFF) << 8)
//                | ((src[2] & 0xFF) << 16)
//                | ((src[3] & 0xFF) << 24);
//        return value;
//    }

    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xFF)
                | ((bytes[1] & 0xFF) << 8)
                | ((bytes[2] & 0xFF) << 16)
                | ((bytes[3] & 0xFF) << 24);
    }

    private static byte[] intToBytes(int value) {
        byte[] des = new byte[4];
        des[3] = (byte) ((value >> 24) & 0xFF);
        des[2] = (byte) ((value >> 16) & 0xFF);
        des[1] = (byte) ((value >> 8) & 0xFF);
        des[0] = (byte) ((value) & 0xFF);
        return des;
    }
}
