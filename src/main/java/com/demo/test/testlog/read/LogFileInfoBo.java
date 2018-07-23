package com.demo.test.testlog.read;

import java.util.ArrayList;
import java.util.List;

/**
 * doc:当前描述日志中的信息
 * 
 * @author liujun
 * @date 2018/07/23
 */
public class LogFileInfoBo {

	/**
	 * 运行的线程数
	 */
	private int threaNum;

	/**
	 * 质量参数
	 */
	private int quality;

	/**
	 * 运行的任务列表
	 */
	private List<RunTimeBo> runTimeList = new ArrayList<RunTimeBo>();

	public int getThreaNum() {
		return threaNum;
	}

	public int getQuality() {
		return quality;
	}

	public List<RunTimeBo> getRunTimeList() {
		return runTimeList;
	}

	public void setThreaNum(int threaNum) {
		this.threaNum = threaNum;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public void addRunTimeList(RunTimeBo runtimeItem) {
		this.runTimeList.add(runtimeItem);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogFileInfoBo [threaNum=");
		builder.append(threaNum);
		builder.append(", quality=");
		builder.append(quality);
		builder.append(", runTimeList=");
		builder.append(runTimeList);
		builder.append("]");
		return builder.toString();
	}
	

}

class RunTimeBo {
	
	/**
	 *开始时间
	 */
	private String startTime;
	
	/**
	 *结束时间
	 */
	private String endTime;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RunTimeBo [startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append("]");
		return builder.toString();
	}
	
	

}
