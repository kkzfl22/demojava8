package com.demo.effectivejava.seven.fortyfive.good;

public class ForCode {

	public static void main(String[] args) {
		// 此执行同样多的循环次数的getmax方法
		for (int i = 0, n = getMax(); i < n; i++) {
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
