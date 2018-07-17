package com.demo.effectivejava.thirty.sex.good;

import java.util.HashSet;
import java.util.Set;

public class Bigram {

	private final char first;
	private final char second;

	public Bigram(char first, char second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object bs) {
		if (!(bs instanceof Bigram)) {
			return false;
		}
		Bigram b = (Bigram) bs;
		return b.first == first && b.second == second;
	}

	public int hashCode() {
		return 31 * first + second;
	}

	public static void main(String[] args) {
		Set<Bigram> s = new HashSet<>();

		for (int i = 0; i < 10; i++) {
			for (char ch = 'a'; ch <= 'z'; ch++) {
				s.add(new Bigram(ch, ch));
			}
		}

		System.out.println(s.size()); // 260
		// 程序结果运行会打印260，非26

		// 原因在于equals方法的参数为Object类型，因此Bigram从Object继承了equalus方法，这个equals方法测试对象同一性，就像==操作符一样

	}

}
