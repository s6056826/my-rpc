package cn.dbw.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import cn.dbw.RpcServer.rpcpo.RpcRequst;
import cn.dbw.RpcServer.rpcpo.RpcResponse;

/**
 * JDK�Ķ�̬�������ֻ�ܴ���ʵ���˽ӿڵ��࣬������ʵ�ֽӿڵ���Ͳ���ʵ��JDK�Ķ�̬����
 * cglib���������ʵ�ִ���ģ�����ԭ���Ƕ�ָ����Ŀ��������һ�����࣬���������з���ʵ����ǿ������Ϊ���õ��Ǽ̳У�
 * ���Բ��ܶ�final���ε�����д��� 
 * @author wangxk
 *
 */
public class RpcProxy {
	
	private RpcDiscover discover;

	public RpcProxy(RpcDiscover discover) {
		this.discover = discover;
	}

	/**
	 * ʹ��jdk��̬�����������
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
					//��ȡԶ�̵ķ����ַ
					String address = discover.discover();
					String[] array = address.split(":");
	                String host = array[0];
	                int port = Integer.parseInt(array[1]);
	                //Զ�����ӵõ��ͻ��˶���
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
