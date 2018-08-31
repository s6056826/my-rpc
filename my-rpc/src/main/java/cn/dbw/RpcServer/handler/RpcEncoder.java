package cn.dbw.RpcServer.handler;

import cn.dbw.common.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {
	
	private Class<?> clazz;
	

	public RpcEncoder(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		//只对固定的类型序列化编码
		if(clazz.isInstance(msg)){
			byte[] serialize = SerializationUtil.serialize(msg);
			//拼接数据 头部为字节数据的长度,组装数据，经由out链加工完数据之后发送到远程
			out.writeInt(serialize.length);
			out.writeBytes(serialize);
		}
		
	}
	
	
	

}
