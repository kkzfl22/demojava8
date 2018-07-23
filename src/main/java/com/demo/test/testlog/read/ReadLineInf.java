package com.demo.test.testlog.read;

/**
 * 进行接口的读取操作
 * 
 * @author liujun
 * @date 2018/07/23
 */
public interface ReadLineInf {

	/**
	 * 进行数据读取操作，
	 * 
	 * @param line   行数据
	 * @param fileBo 设置的对象信息
	 * @return 解析完毕
	 */
	boolean readBean(String line, LogFileInfoBo fileBo);

}
