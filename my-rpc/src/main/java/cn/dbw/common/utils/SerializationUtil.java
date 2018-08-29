package cn.dbw.common.utils;

import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.dyuproject.protostuff.Pipe.Schema;

/**
 * 基于protobuf的序列化工具
 * @author dbw
 *
 */
public class SerializationUtil {
	
	//构建一个缓存的schema容器
	private static Map<Class<?>, Schema<?>> cacheMap=new ConcurrentHashMap<Class<?>, Schema<?>>();
    
}
