package cn.dbw.netty.coder;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDecoder extends ByteToMessageDecoder {
	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		 //��ǵ�ǰ�ɶ�����
		 in.markReaderIndex();
		 if(in.readableBytes()<4){
			 return;
		 }
		 int readInt = in.readInt();
		 if(readInt<0){
			 //˵���ɶ������Ѿ�Ϊ0,�ر�ͨ��
			 ctx.close();
			 return;
		 }
		 if(in.readableBytes()<readInt){
			 //˵����ȡ�����ݰ�������
			 in.resetReaderIndex();
			 return;
		 }
		 
		 byte[] data=new byte[readInt];
		 in.readBytes(data);
		 //TODO
		 
	}

}
