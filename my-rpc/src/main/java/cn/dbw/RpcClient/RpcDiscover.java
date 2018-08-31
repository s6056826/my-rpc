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
	 * ��ȡ�����ṩ����zookeeperע��ĵ�ַ
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
				//���ж��ע���ַ���������ѡһ��ע���ַ
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
	//������final���Σ���ʾ�ô��ݵĲ������õ�ַ���ܱ��޸�
	public void watchNode(final ZooKeeper zk){
		 try {
			 //ÿ�λ�ȡ�ӽڵ����ݣ���Ҫ���һ��data��
			 data.clear();
			 //��ȡzookeeperע��ڵ��µ������ӽڵ��·����
			 List<String> children = zk.getChildren(ZkServerConstant.ZK_REGISTRY_PATH, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					//���ӽڵ㷢���仯�����ӽڵ����ӻ�ɾ�����޸����ݣ������ע������Ϣ
					if(event.getType()==Event.EventType.NodeChildrenChanged){
						watchNode(zk);
					}
				}
			});
			 if(children!=null&&children.size()>0){
				children.stream().forEach(child->{
					try {
						//��ȡ�ڵ��������Ϣ����ע��������ĵ�ַ
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
