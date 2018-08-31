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
		System.out.println("服务端收到数据");
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
		 //写出数据并添加监听事件，当操作完成，关闭通道的监听器
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
		 *  java自带的反射方法性能比较低
		 * 	Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);
		 *  method.setAccessible(true);
		 *  Object result = method.invoke(serviceBean, parameters);
		 */
	    //是由CGLib提供的反射API，性能比较高
		FastClass serviceFastClass = FastClass.create(serviceBean.getClass());
		//参数方法名，参数类型
		FastMethod fastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
		return fastMethod.invoke(serviceBean, parameters);
	}

}
