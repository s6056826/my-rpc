package cn.dbw.RpcClient;

import io.netty.util.internal.ThreadLocalRandom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;




import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import cn.dbw.RpcServer.constant.ZkServerConstant;

public class RpcDiscover {
	
	private volatile static List<String> data=new ArrayList<String>();
	private final CountDownLatch countDownLatch=new CountDownLatch(1);
	private final ExecutorService executorService=Executors.newSingleThreadExecutor();
	private String registerAddress;
	
	public RpcDiscover(String registerAddress) {
		this.registerAddress=registerAddress;
	}
	
	/**
	 * 获取服务提供者在zookeeper注册的地址
	 * @return
	 */
	public String discover(){
		ZooKeeper zooKeeper = connect();
		watchNode(zooKeeper);
		String regdata=null;
		if(data!=null){
			int size = data.size();
			if(size==1){
				regdata=data.get(0);
			}else{
				//若有多个注册地址，则随机挑选一个注册地址
				regdata=data.get(ThreadLocalRandom.current().nextInt(size));
			}
		}
		return regdata;
	}
    
	private ZooKeeper connect(){
		ZooKeeper keeper=null;
		try {
			keeper=new ZooKeeper(registerAddress, 5000, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if(event.getState()==Event.KeeperState.SyncConnected){
						countDownLatch.countDown();
					}
				}
			});
			countDownLatch.await();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keeper;
	}
	//参数加final修饰，表示该传递的参数引用地址不能被修改
	public void watchNode(final ZooKeeper zk){
		 try {
			 //每次获取子节点数据，需要清除一下data表
			 data.clear();
			 //获取zookeeper注册节点下的所有子节点的路径名
			 List<String> children = zk.getChildren(ZkServerConstant.ZK_REGISTRY_PATH, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					//若子节点发生变化，即子节点增加或删除或修改数据，则更新注册表的信息
					if(event.getType()==Event.EventType.NodeChildrenChanged){
						watchNode(zk);
					}
				}
			});
			 if(children!=null&&children.size()>0){
				children.stream().forEach(child->{
					try {
						//获取节点的数据信息，即注册服务器的地址
						byte[] data2 = zk.getData(ZkServerConstant.ZK_REGISTRY_PATH+"/"+child, false, null);
						data.add(new String(data2));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			 }
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
