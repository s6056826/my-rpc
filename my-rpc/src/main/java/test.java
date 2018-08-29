import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * 使用最优秀的protobuff序列化工具
 * ProtostuffIOUtil 进行java的序列化和反序列化
 * 其性能是java序列化的16倍
 * @author wangxk
 *
 */

public class test implements Serializable {
	
	private int a=1;
	
	public static void main(String[] args) throws IOException {
		ByteBuf buf=Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8);
		byte[] byteArray = ProtostuffIOUtil.toByteArray(new test(), RuntimeSchema.createFrom(test.class),LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
		test t=RuntimeSchema.createFrom(test.class).newMessage();
		ProtostuffIOUtil.mergeFrom(byteArray, t, RuntimeSchema.createFrom(test.class));
		System.out.println(byteArray.length);
		 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		 byte[] by=new byte[1024];
		 ObjectOutputStream op=new ObjectOutputStream(byteArrayOutputStream);
		 op.writeObject(new test());
	     byte[] bs = byteArrayOutputStream.toByteArray();
	     System.out.println(bs.length);
	}

}
