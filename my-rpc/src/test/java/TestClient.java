import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.dbw.RpcClient.RpcProxy;
import cn.dbw.inter.HelloService;



//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations="classpath:spring_client.xml")
public class TestClient {
	
	@Autowired
	private RpcProxy proxy;
	
	
	//@Test
	public void test(){
	 	HelloService helloService = proxy.create(HelloService.class);
	 	String say = helloService.say("hello my rpc success");
	 	System.out.println(say);
	}
	

}
