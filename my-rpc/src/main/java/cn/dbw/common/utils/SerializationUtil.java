package cn.dbw.common.utils;

import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.dyuproject.protostuff.Pipe.Schema;

/**
 * ����protobuf�����л�����
 * @author dbw
 *
 */
public class SerializationUtil {
	
	//����һ�������schema����
	private static Map<Class<?>, Schema<?>> cacheMap=new ConcurrentHashMap<Class<?>, Schema<?>>();
    
}
