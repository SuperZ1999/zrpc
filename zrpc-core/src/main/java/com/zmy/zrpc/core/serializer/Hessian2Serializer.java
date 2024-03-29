package com.zmy.zrpc.core.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.zmy.zrpc.common.enumeration.SerializerCode;
import com.zmy.zrpc.common.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Hessian2Serializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(Hessian2Serializer.class);

    @Override
    public byte[] serialize(Object obj) {
        Hessian2Output hessian2Output = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            logger.info("使用Hessian2序列化");
            hessian2Output = new Hessian2Output(byteArrayOutputStream);
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessian2Output != null) {
                try {
                    hessian2Output.close();
                } catch (IOException e) {
                    logger.error("关闭hessian输出流时有错误发生:", e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        logger.info("使用Hessian2反序列化");
        Hessian2Input hessian2Input = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessian2Input = new Hessian2Input(byteArrayInputStream);
            return hessian2Input.readObject();
        } catch (IOException e) {
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        } finally {
            if (hessian2Input != null) {
                try {
                    hessian2Input.close();
                } catch (IOException e) {
                    logger.error("关闭hessian输入流时有错误发生:", e);
                }
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.HESSIAN2.getCode();
    }
}
