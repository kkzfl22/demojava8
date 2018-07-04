package com.demo.effectivejava.genericity.collection;

/**
 * 通用化的API，设计通用的map存储，将key进行参数化
 * 
 * @since 2018年6月6日 下午4:29:52
 * @version 0.0.1
 * @author liujun
 */
public interface Favorites {

	/**
	 * 进行数据的存储放
	 * 
	 * @param type
	 * @param instance
	 */
	public <T> void putFavorite(Class<T> type, T instance);

	/**
	 * 进行数据的获取
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getFavorite(Class<T> type);

}
