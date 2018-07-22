package com.demo.effectivejava.seven.fortynine.bad;

import java.util.Comparator;

public class BadCode {

	static Integer i;

	public static void main(String[] args) {
		Comparator<Integer> naturalOrder = new Comparator<Integer>() {
			@Override
			public int compare(Integer first, Integer second) {
				return first < second ? -1 : (first == second ? 0 : 1);
			}
		};

		// first和second引用表示同一个int值的不同的Integer实例，这个比较器操作就会返回false

		int value = naturalOrder.compare(new Integer(42), new Integer(42));

		System.out.println(value);

		Integer intvalue = new Integer(42);
		Integer intvalue2 = new Integer(42);

		System.out.println(intvalue == intvalue2);
		System.out.println(intvalue > intvalue2);
		System.out.println(intvalue < intvalue2);
		System.out.println(intvalue.equals(intvalue2));

		// Integer首次初始化时为null，非值
		if (i == 42) {
			System.out.println("Unbelievable");
		}
	}

}
