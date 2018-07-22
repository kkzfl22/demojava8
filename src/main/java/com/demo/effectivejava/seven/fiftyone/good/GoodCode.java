package com.demo.effectivejava.seven.fiftyone.good;

import java.util.concurrent.ThreadLocalRandom;

import com.demo.effectivejava.seven.fiftyone.bad.BadCode;

/**
 * 1:现在 JVM对于字符串已经做了很大优化，所以性能提升的空间很小
 * 
 * @author liujun
 * @date 2018/07/22
 */
public class GoodCode {

	public static void main(String[] args) {
		BadCode code = new BadCode();
		long startTime = System.currentTimeMillis();
		code.stateMent(200000);
		long endTime = System.currentTimeMillis();
		System.out.println("good use time:" + (endTime - startTime));
		// good use time:63440

	}

	public String stateMent(int sumNum) {
		StringBuilder result = new StringBuilder(sumNum * 5 + 20);
		for (int i = 0; i < sumNum; i++) {
			result.append(getStr(i));
		}
		return result.toString();
	}

	public int getStr(int i) {
		return ThreadLocalRandom.current().nextInt(10000, 99999);
	}

}
