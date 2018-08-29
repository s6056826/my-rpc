package cn.dbw.RpcServer.handler;

import java.util.List;

import cn.dbw.common.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDecoder extends ByteToMessageDecoder {
	
	private Class<?> clazz;
	
	public RpcDecoder(Class<?> clazz) {
		this.clazz=clazz;
	}
	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if(in.readableBytes()<4)
			return;
		in.markReaderIndex();
		
		int dataLength = in.readInt();
		
		if(in.readableBytes()<dataLength){
			in.resetReaderIndex();
			return;
		}
		
		byte[] data=new byte[dataLength];
		in.readBytes(data);
        Object object = SerializationUtil.deserialize(data, clazz);
        out.add(object);
	}

}
