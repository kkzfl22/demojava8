package com.zookeeper.distributed.lock.console;

/**
 * 分布式锁的配制信息
 * 
 * @since 2017年7月3日 下午4:29:48
 * @author liujun
 * @version 0.0.1
 */
public enum ZkLockEnum {

	/**
	 * zk最顶层节点
	 */
	ZK_ROOT_PATH("jloader"),

	/**
	 * 最基础的loader的目录信息
	 */
	ZK_BASE_PATH(ZK_ROOT_PATH.getCfgMsg() + "/lock");

	/**
	 * 配制信息
	 */
	private String cfgMsg;

	private ZkLockEnum(String cfgMsg) {
		this.cfgMsg = cfgMsg;
	}

	public String getCfgMsg() {
		return cfgMsg;
	}

	public void setCfgMsg(String cfgMsg) {
		this.cfgMsg = cfgMsg;
	}

}
