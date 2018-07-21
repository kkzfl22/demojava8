package com.demo.effectivejava.seven.fortyfive.bad;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BugCode {

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();

		Iterator<String> i = list.iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}

		List<String> list2 = new ArrayList<>();

		Iterator<String> i2 = list2.iterator();
		// 拷贝-粘贴的代码很容易就造成了这个错误
		while (i.hasNext()) {
			System.out.println(i2.next());
		}
	}

}
