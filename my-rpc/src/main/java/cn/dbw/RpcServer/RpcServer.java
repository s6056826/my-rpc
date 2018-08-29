package cn.dbw.RpcServer;

import java.net.Socket;
import java.util.Map;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.dbw.netty.annotation.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
/**
 * ʵ��ApplicationContextAware�ӿڵ�bean���Ի�ȡspring��ioc����
 * ʵ��InitializingBean�ӿڿ�����bean���Ի�֮�����afterPropertiesSet()����
 * @author wangxk
 *
 */
public class RpcServer implements ApplicationContextAware,InitializingBean {
	

	private Map<String,Object> handlerMap=new ConcurrentHashMap<String, Object>(32);
	private final ZkServiceRegister registry;
	private final String registerAddress;
	public RpcServer(ZkServiceRegister registor,String registerAddress){
		this.registry=registor;
		this.registerAddress=registerAddress;
	}
	
	
//	public RpcServer() throws InterruptedException {
//		ServerBootstrap bootstrap=new ServerBootstrap();
//		bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
//		    .childHandler(new ChannelInitializer<SocketChannel>() {
//				@Override
//				protected void initChannel(SocketChannel ch) throws Exception {
//					ChannelPipeline pipeline = ch.pipeline();
//					pipeline.addLast();
//				}
//			
//			});
//		ChannelFuture channelFuture = bootstrap.bind(9999).sync();
//		channelFuture.channel().close().sync();
//	}

	public void afterPropertiesSet() throws Exception {
		
		
	}
    
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		//��ȡ���д�RpcService��bean
		Map<String, Object> beansWithAnnotation = arg0.getBeansWithAnnotation(RpcService.class);
		for(Object bean:beansWithAnnotation.values()){
			  //��ȡ���ӿ�����
			  String interfaceName = bean.getClass().getAnnotation(RpcService.class).value().getName();
			  handlerMap.put(interfaceName, bean);
		}
	}
	
	private void startServer(){
		EventLoopGroup boss=new NioEventLoopGroup();
	    EventLoopGroup worker=new NioEventLoopGroup();
	    try {
			ServerBootstrap bootstrap=new ServerBootstrap();
			bootstrap.group(boss,worker).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 124)
			        .childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
					            
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
