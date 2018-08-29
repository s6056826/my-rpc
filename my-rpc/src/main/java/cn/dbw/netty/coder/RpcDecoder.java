package cn.dbw.netty.coder;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDecoder extends ByteToMessageDecoder {
	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		 //标记当前可读索引
		 in.markReaderIndex();
		 if(in.readableBytes()<4){
			 return;
		 }
		 int readInt = in.readInt();
		 if(readInt<0){
			 //说明可读数据已经为0,关闭通道
			 ctx.close();
			 return;
		 }
		 if(in.readableBytes()<readInt){
			 //说明读取的数据包不完整
			 in.resetReaderIndex();
			 return;
		 }
		 
		 byte[] data=new byte[readInt];
		 in.readBytes(data);
		 //TODO
		 
	}

}
