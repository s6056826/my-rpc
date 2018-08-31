import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestServer {
	
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring_server.xml");
	}

}
