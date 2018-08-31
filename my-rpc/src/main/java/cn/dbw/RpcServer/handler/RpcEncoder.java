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
		//ֻ�Թ̶����������л�����
		if(clazz.isInstance(msg)){
			byte[] serialize = SerializationUtil.serialize(msg);
			//ƴ������ ͷ��Ϊ�ֽ����ݵĳ���,��װ���ݣ�����out���ӹ�������֮���͵�Զ��
			out.writeInt(serialize.length);
			out.writeBytes(serialize);
		}
		
	}
	
	
	

}
