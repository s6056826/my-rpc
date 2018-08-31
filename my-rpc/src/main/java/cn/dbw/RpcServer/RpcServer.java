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
 * ʵ��ApplicationContextAware�ӿڵ�bean���Ի�ȡspring��ioc����
 * ʵ��InitializingBean�ӿڿ�����bean���Ի�֮�����afterPropertiesSet()����
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
     * һ��Springװ��Bean�Ĺ���   
1. ʵ����;  
2. ��������ֵ;  
3. ���ʵ����BeanNameAware�ӿ�,����setBeanName����Bean��ID����Name;  
4. ���ʵ��BeanFactoryAware�ӿ�,����setBeanFactory ����BeanFactory;  
5. ���ʵ��ApplicationContextAware,����setApplicationContext����ApplicationContext  
6. ����BeanPostProcessor��Ԥ�ȳ�ʼ������;  
7. ����InitializingBean��afterPropertiesSet()����;  
8. ���ö���init-method������  
9. ����BeanPostProcessor�ĺ��ʼ������;  


Spring�����رչ���   
1. ����DisposableBean��destroy();  
2. ���ö��Ƶ�destroy-method����;
     */
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
					            ChannelPipeline pipeline = ch.pipeline();
					            pipeline.addLast(new RpcDecoder(RpcRequst.class));
					            pipeline.addLast(new RpcEncoder(RpcResponse.class));
					            pipeline.addLast(new RpcServerHandler(handlerMap));
						}
					});
			 ChannelFuture channelFuture = bootstrap.bind(9999).sync();
			 System.out.println("�����������ɹ�.......�����˿ڣ�9999");
			 if(registry!=null){
				 registry.register(registerAddress);// ע������ַ
			 }
			 channelFuture.channel().closeFuture().sync();
			 System.out.println("�������ر�");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//���ŵĹر������¼�ѭ����
			boss.shutdownGracefully();
            worker.shutdownGracefully();
		}
	}

}
