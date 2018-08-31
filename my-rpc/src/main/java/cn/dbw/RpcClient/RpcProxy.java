package cn.dbw.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import cn.dbw.RpcServer.rpcpo.RpcRequst;
import cn.dbw.RpcServer.rpcpo.RpcResponse;

/**
 * JDK的动态代理机制只能代理实现了接口的类，而不能实现接口的类就不能实现JDK的动态代理，
 * cglib是针对类来实现代理的，他的原理是对指定的目标类生成一个子类，并覆盖其中方法实现增强，但因为采用的是继承，
 * 所以不能对final修饰的类进行代理。 
 * @author wangxk
 *
 */
public class RpcProxy {
	
	private RpcDiscover discover;

	public RpcProxy(RpcDiscover discover) {
		this.discover = discover;
	}

	/**
	 * 使用jdk动态创建代理对象
	 * @param interfaceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> interfaceClass){
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				RpcRequst requst=new RpcRequst();
				requst.setRequestId(UUID.randomUUID().toString());
				requst.setClassName(method.getDeclaringClass().getName());
				requst.setMethodName(method.getName());
				requst.setParameterTypes(method.getParameterTypes());
				requst.setParameters(args);
				
				if(discover!=null){
					//获取远程的服务地址
					String address = discover.discover();
					String[] array = address.split(":");
	                String host = array[0];
	                int port = Integer.parseInt(array[1]);
	                //远程连接得到客户端对象
	                RpcClient rpcClient = new RpcClient(host, port);
	                RpcResponse response = rpcClient.send(requst);
	                
	                if(response.getError()!=null){
	                	throw response.getError();
	                }else{
	                	return response.getResult();
	                }
				}
				return null;
			}
		});
	}

	
	

}
