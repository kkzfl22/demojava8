package com.demo.java8.tools.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 进行redis客户端的操作
 * 
 * @since 2018年5月23日 下午3:11:59
 * @version 0.0.1
 * @author liujun
 */
public class RedisUse {

	/**
	 * 视频压缩的列表
	 */
	private static final String KEY = "moveList";

	/**
	 * 声明独占锁
	 */
	private Lock lock = new ReentrantLock();

	/**
	 * 获取通道
	 */
	private Condition condi = lock.newCondition();

	public static void main(String[] args) {

		RedisUse use = new RedisUse();
		try {
			use.pop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void pop() throws InterruptedException {

		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		Jedis jedis = pool.getResource();

		try {

			lock.lock();

			while (true) {
				// 检查当前是否超过大小
				long maxlen = jedis.llen(KEY);
				if (maxlen <= 0) {
					System.out.println("休眠");
					condi.await(1, TimeUnit.SECONDS);
				} else {

					// 如果未超过大小，则直接放入
					String value = jedis.lindex(KEY, 0);

					System.out.println("获取数据:" + value);


					// 然后获取数据
					String getLpop = jedis.lpop(KEY);

					System.out.println("移除数据:" + getLpop);
				}
				System.out.println();
			}
		} finally {
			lock.unlock();
		}

	}

}
