package com.demo.effectivejava.seven.fortyfive.good;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Code {

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();

		for (Iterator<String> i = list.iterator(); i.hasNext();) {
			System.out.println(i.next());
		}

		List<String> list2 = new ArrayList<>();

		// 此可以很好的解决此类问题
		for (Iterator<String> i2 = list2.iterator(); i2.hasNext();) {
			System.out.println(i2.next());
		}

		// 局部变量最的作用域进行小化的循环的做法
		for (int i = 0, n = ThreadLocalRandom.current().nextInt(); i < n; i++) {
			System.out.println(i);
		}
	}

}
