package com.demo.effectivejava.seven.thirtynine.one;

import java.util.Date;

/**
 * doc:由于date本身是可变的，因此很容易违反这个约束条件
 * 
 * @author liujun
 * @date 2018/07/18
 */
public final class Period {

	private final Date start;

	private final Date end;

	/**
	 * doc:这个类似乎是不可变的，并强加了约束条件，周期的开始时间不能在结束时间之后
	 * @param start
	 * @param end
	 * @throws IllegalArgumentException if start is after end
	 */
	public Period(Date start, Date end) {
		if (start.compareTo(end) > 0) {
			throw new IllegalArgumentException(start + " after " + end);
		}

		this.start = start;
		this.end = end;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

}
