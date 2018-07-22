package com.demo.effectivejava.seven.fortynine.good;

public class BadCode2 {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		long sum = 0l;

		for (long i = 0; i < Integer.MAX_VALUE; i++) {
			sum += i;
		}

		System.out.println(sum);

		long end = System.currentTimeMillis();

		System.out.println("use time:" + (end - startTime));

		// 用时816毫秒
	}

}
