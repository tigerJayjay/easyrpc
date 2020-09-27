package com.tiger.easyrpc.serialization.protostuff.util;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Protostuff序列化反序列化工具类
 */
public class ProtostuffUtil {
    //缓存schema，构建schema比较耗时
    private static ConcurrentMap<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private  static <T> Schema<T> getSchema(Class<T> clazz){
        Schema<T> schema = (Schema<T>)schemaCache.get(clazz);
        if(schema == null){
            schema = RuntimeSchema.createFrom(clazz);
            if(schema != null){
                schemaCache.put(clazz,schema);
            }
        }
        return schema;
    }

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    public static synchronized <T> byte[] serializer(T obj){
        Class<T> clazz = (Class<T>)obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(1024*1024);
        Schema<T> schema = getSchema(clazz);
        try {
            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        } catch (Exception e) {
            throw  new RuntimeException(e);
        } finally {
            buffer.clear();
        }
    }

    public static  synchronized <T> T deserializer(byte[] bytes,Class<T> clazz){
        try {
            T t = clazz.newInstance();
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes,t,schema);
            return t;
        } catch (InstantiationException e) {
            throw  new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw  new RuntimeException(e);
        }
    }

}
