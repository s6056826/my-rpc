package cn.dbw.RpcServer;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.dbw.RpcServer.handler.RpcDecoder;
import cn.dbw.RpcServer.handler.RpcEncoder;
import cn.dbw.RpcServer.handler.RpcServerHandler;
import cn.dbw.RpcServer.rpcpo.RpcRequst;
import cn.dbw.RpcServer.rpcpo.RpcResponse;
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
import io.netty.util.concurrent.DefaultThreadFactory;
/**
 * 实现ApplicationContextAware接口的bean可以获取spring的ioc容器
 * 实现InitializingBean接口可以在bean属性化之后调用afterPropertiesSet()方法
 * @author wangxk
 *
 */
public class RpcServer implements ApplicationContextAware,InitializingBean {
	

	private Map<String,Object> handlerMap=new ConcurrentHashMap<String, Object>(32);
	private final ExecutorService executorService=Executors.newSingleThreadExecutor(new DefaultThreadFactory("my-pool"));
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
		executorService.submit(()->{
			startServer();
		});
		
	
		
	}
/**
     * 一、Spring装配Bean的过程   
1. 实例化;  
2. 设置属性值;  
3. 如果实现了BeanNameAware接口,调用setBeanName设置Bean的ID或者Name;  
4. 如果实现BeanFactoryAware接口,调用setBeanFactory 设置BeanFactory;  
5. 如果实现ApplicationContextAware,调用setApplicationContext设置ApplicationContext  
6. 调用BeanPostProcessor的预先初始化方法;  
7. 调用InitializingBean的afterPropertiesSet()方法;  
8. 调用定制init-method方法；  
9. 调用BeanPostProcessor的后初始化方法;  


Spring容器关闭过程   
1. 调用DisposableBean的destroy();  
2. 调用定制的destroy-method方法;
     */
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		//获取所有带RpcService的bean
		Map<String, Object> beansWithAnnotation = arg0.getBeansWithAnnotation(RpcService.class);
		for(Object bean:beansWithAnnotation.values()){
			  //获取到接口名字
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
					            ChannelPipeline pipeline = ch.pipeline();
					            pipeline.addLast(new RpcDecoder(RpcRequst.class));
					            pipeline.addLast(new RpcEncoder(RpcResponse.class));
					            pipeline.addLast(new RpcServerHandler(handlerMap));
						}
					});
			 ChannelFuture channelFuture = bootstrap.bind(9999).sync();
			 System.out.println("启动服务器成功.......监听端口：9999");
			 if(registry!=null){
				 registry.register(registerAddress);// 注册服务地址
			 }
			 channelFuture.channel().closeFuture().sync();
			 System.out.println("服务器关闭");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//优雅的关闭两个事件循环组
			boss.shutdownGracefully();
            worker.shutdownGracefully();
		}
	}

}
