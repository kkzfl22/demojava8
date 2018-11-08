package com.zookeeper.distributed.lock.lock;

/**
 * 分布式锁的服务
 * 
 * @since 2017年6月28日 下午3:52:36
 * @author liujun
 * @version 0.0.1
 */
public interface LockServiceInf {

	/**
	 * 进行加锁，一直阻塞直到加锁成功
	 * 
	 * @param baseNode
	 *            加锁的基础节点
	 * @param lockNode
	 *            加锁的节点
	 */
	public void lock(String baseNode, String lockNode);

	/**
	 * 尝试加锁，返回结果，
	 * 
	 * @param baseNode
	 *            加锁的基础节点
	 * @param lockNode
	 *            加锁的节点
	 * @return 成功返回 null,失败返回 当前加锁节点需等待完成的节点
	 */
	public String tryLock(String baseNode, String lockNode);

	/**
	 * 进行当前已经加锁成功的节点进行解锁操作
	 * 
	 */
	public void unlock();

	/**
	 * 进行当前加锁服务的关闭操作，一般在系统停机时调用
	 */
	public void colose();
}
