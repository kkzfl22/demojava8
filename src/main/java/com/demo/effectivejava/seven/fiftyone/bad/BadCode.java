package com.demo.effectivejava.seven.fiftyone.bad;

import java.util.concurrent.ThreadLocalRandom;

public class BadCode {

	public static void main(String[] args) {
		BadCode code = new BadCode();
		long startTime = System.currentTimeMillis();
		String value = code.stateMent(200000);
		long endTime = System.currentTimeMillis();
		System.out.println("bad use time:" + (endTime - startTime));
		// bad use time:61531
	}

	public String stateMent(int sumNum) {
		String result = "";
		for (int i = 0; i < sumNum; i++) {
			result += getStr(i);
		}
		return result;
	}

	public int getStr(int i) {
		return ThreadLocalRandom.current().nextInt(10000, 99999);
	}

}
