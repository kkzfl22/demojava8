package com.demo.effectivejava.seven.thirtynine.third;

import java.util.Date;

/**
 * 
 * @author liujun
 * @date 2018/07/18
 */
public final class Period {

	private final Date start;

	private final Date end;

	/**
	 * 
	 * 
	 * @param start
	 * @param end
	 * @throws IllegalArgumentException if start is after end
	 */
	public Period(Date start, Date end) {
		this.start = new Date(start.getTime());
		this.end = new Date(end.getTime());

		if (this.start.compareTo(this.end) > 0) {
			throw new IllegalArgumentException(start + " after " + end);
		}
	}

	public Date getStart() {
		return new Date(start.getTime());
	}

	public Date getEnd() {
		return new Date(end.getTime());
	}

}
