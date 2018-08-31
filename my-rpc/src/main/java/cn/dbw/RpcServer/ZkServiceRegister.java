package cn.dbw.RpcServer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import cn.dbw.RpcServer.constant.ZkServerConstant;

public class ZkServiceRegister {
	
	private final CountDownLatch countDownLatch=new CountDownLatch(1);
	
	private String registryAddress="";
	
	public ZkServiceRegister(String registryAddress) {
		this.registryAddress=registryAddress;
	}
	
	
	public ZooKeeper connectServer(){
		ZooKeeper zooKeeper = connectServer(registryAddress,5000);
		return zooKeeper;
	}
	
	
	public void register(String data){
	 if(data!=null){
	 ZooKeeper zk=connectServer();
		if(zk!=null){
			createNode(zk, data);
		}
	 }
	}
	
	public ZooKeeper connectServer(String connectString,int sessionTimeout){
		ZooKeeper zk=null;
		try {
			   zk=new ZooKeeper(connectString, sessionTimeout, new Watcher() {
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					if(event.getState()==Event.KeeperState.SyncConnected){
						countDownLatch.countDown();
					}
				}
			});
			 //此方法必须要同步等待客户端成功连接zooker服务器才返回
			   countDownLatch.await();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return zk;
	}
	
	public void createNode(ZooKeeper zk,String data){
		if(zk!=null){
			byte[] bdata = data.getBytes();
			try {
				zk.create(ZkServerConstant.ZK_DATA_PATH,bdata, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			} catch (KeeperException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
