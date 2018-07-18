package com.demo.effectivejava.seven.thirtymime.two;

import java.util.Date;

/**
 * doc:虽然替换构造器就可以成功地避免上述的攻击，但是改变Period实践仍然是有可能的，
 * 
 * 因为它的访问提供了对其可这对内部了成员的访问能力。
 * 
 * @author liujun
 * @date 2018/07/18
 */
public final class Period {

	private final Date start;

	private final Date end;

	/**
	 * doc:使用了新的构造器之后，上述的攻击对于Period实际不再有效。
	 * 
	 * 保护性的拷贝是在检查参数的有效性之前进行，并且有效性检查是针对拷贝之后的对象，而不是针对原始对象
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
		return start;
	}

	public Date getEnd() {
		return end;
	}

}
