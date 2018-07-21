package com.demo.effectivejava.seven.fortyfive.bad;

public class ForBugCode {

	public static void main(String[] args) {
		// 此执行同样多的循环次数的getmax方法
		for (int i = 0; i < getMax(); i++) {
			System.out.println(i);
		}

		System.out.println("进入次数:" + count);
	}

	public static int count = 0;

	public static int getMax() {
		count++;
		return 100;
	}
}
