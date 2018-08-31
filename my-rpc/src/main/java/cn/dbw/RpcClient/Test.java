package cn.dbw.RpcClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		   Watcher watcher = new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if(event.getState()==Event.KeeperState.SyncConnected){
						System.out.println("建立连接成功");
						
					}
					if(event.getState()==Event.KeeperState.Disconnected){
						System.out.println("连接断开");
					}	
					System.out.println("aaa");
				}
			};
	       new Thread(()->{
	    	   ZooKeeper connect = Test.connect(watcher);
	    	   try {
	    		   System.out.println("zz");
				Thread.sleep(4000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	   try {
	    		   System.out.println("guanbi");
	    		   connect.register(watcher);
				connect.close();
				
				Thread.sleep(4000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       }).start();
	}
	
	public static ZooKeeper connect(Watcher watcher){
		 ZooKeeper keeper=null;
		try {
			keeper=new ZooKeeper("127.0.0.1:2181", 5000,watcher);
		} catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keeper;
	}
}
