package cn.dbw.common.utils;

import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * 基于protobuf的序列化工具
 * @author dbw
 *
 */
public class SerializationUtil {
	
	//构建一个缓存的schema容器
	private static Map<Class<?>, Schema<?>> cacheMap=new ConcurrentHashMap<Class<?>, Schema<?>>();
    //Objenesis是一个强大的java反射框架
	private static Objenesis objenesis=new ObjenesisStd();
   
    /**
     * 动态获取schema
     * @param cls
     * @return
     * @throws InstantiationException 
     */
    public static <T> Schema<T> getSchema(Class<T> cls) throws InstantiationException{
    	Schema<T> schema =(Schema<T>) cacheMap.get(cls);
    	if(schema==null){
    		schema = RuntimeSchema.createFrom(cls);
    		if(schema!=null){
    			cacheMap.put(cls, schema);
    		}
    	}
    	return schema;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            T message = (T) objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
