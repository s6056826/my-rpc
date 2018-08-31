package cn.dbw.RpcServer.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import cn.dbw.RpcServer.rpcpo.RpcRequst;
import cn.dbw.RpcServer.rpcpo.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequst> {
	
	private final Map<String,Object> handlerMap;
    
	public RpcServerHandler(Map<String,Object> handlerMap) {
		this.handlerMap=handlerMap;
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequst msg)
			throws Exception {
		System.out.println("������յ�����");
		RpcResponse response=new RpcResponse();
		try {
			Object object = handle(msg);
			System.out.println("rs:"+object);
			response.setRequestId(msg.getRequestId());
			response.setResult(object);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(e);
		}
		 //д�����ݲ���Ӽ����¼�����������ɣ��ر�ͨ���ļ�����
		 ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	private Object handle(RpcRequst msg) throws InvocationTargetException{
		
		String requestId = msg.getRequestId();
		String className = msg.getClassName();
		String methodName = msg.getMethodName();
		Object serviceBean = handlerMap.get(className);
		Object[] parameters = msg.getParameters();
		Class<?>[] parameterTypes = msg.getParameterTypes();
		/** 
		 *  java�Դ��ķ��䷽�����ܱȽϵ�
		 * 	Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);
		 *  method.setAccessible(true);
		 *  Object result = method.invoke(serviceBean, parameters);
		 */
	    //����CGLib�ṩ�ķ���API�����ܱȽϸ�
		FastClass serviceFastClass = FastClass.create(serviceBean.getClass());
		//��������������������
		FastMethod fastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
		return fastMethod.invoke(serviceBean, parameters);
	}

}
