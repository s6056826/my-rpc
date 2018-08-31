package cn.dbw.RpcClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.dbw.RpcServer.handler.RpcDecoder;
import cn.dbw.RpcServer.handler.RpcEncoder;
import cn.dbw.RpcServer.rpcpo.RpcRequst;
import cn.dbw.RpcServer.rpcpo.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
	
	
	private String host;
	private int port;
	private RpcResponse response;
	private Lock lock=new ReentrantLock();
	private Object obj=new Object();
	
	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}



	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg)
			throws Exception {
		this.response=msg;
		 synchronized (obj) {
	            obj.notifyAll(); // �յ���Ӧ�������߳�
	        }
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	public RpcResponse send(RpcRequst rpcRequst) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            .addLast(new RpcEncoder(RpcRequst.class)) // �� RPC ������б��루Ϊ�˷�������
                            .addLast(new RpcDecoder(RpcResponse.class)) // �� RPC ��Ӧ���н��루Ϊ�˴�����Ӧ��
                            .addLast(RpcClient.this); // ʹ�� RpcClient ���� RPC ����
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(rpcRequst).sync();

            synchronized (obj) {
                obj.wait(); // �յ���Ӧ�������߳�
            }
            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            group.shutdownGracefully();
        }
		return response;
		
		
	}
	
	
	


}
