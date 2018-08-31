import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.dbw.RpcClient.RpcClient;
import cn.dbw.RpcClient.RpcProxy;
import cn.dbw.RpcServer.handler.RpcDecoder;
import cn.dbw.RpcServer.handler.RpcEncoder;
import cn.dbw.RpcServer.rpcpo.RpcRequst;
import cn.dbw.RpcServer.rpcpo.RpcResponse;
import cn.dbw.inter.HelloService;


public class TestClient2 {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring_client.xml");
		RpcProxy rpcProxy = context.getBean(RpcProxy.class);
		HelloService helloService = rpcProxy.create(HelloService.class);
		String say = helloService.say("hello my rpc success");
		System.out.println(say);

	}
	


}
