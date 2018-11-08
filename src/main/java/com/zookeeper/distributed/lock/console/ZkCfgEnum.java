package com.zookeeper.distributed.lock.console;

/**
 * zk相关的配制信息
 * 
 * @since 2017年6月28日 下午3:06:31
 * @author liujun
 * @version 0.0.1
 */
public enum ZkCfgEnum {

	/**
	 * zk最基础的配制信息
	 */
	ZK_BASE_PATH("/jloader"),

	/**
	 * 路径标识符
	 */
	ZK_PATH_FLAG("/");

	/**
	 * 配制的key信息
	 */
	private String key;

	private ZkCfgEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
