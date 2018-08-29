package cn.dbw.inter.impl;

import cn.dbw.inter.HelloService;
import cn.dbw.netty.annotation.RpcService;
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

	public String say(String msg) {
		// TODO Auto-generated method stub
		return "rpc:"+msg;
	}

}
