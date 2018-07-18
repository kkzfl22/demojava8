package com.demo.effectivejava.seven.thirtyeight;

import java.math.BigInteger;

public class ParamException {

	/**
	 * d:要用 Javadoc的@throws标签（tag)在文档中说明违反参数值 限制时会抛出的异常
	 * 
	 * @param m
	 * @return
	 * @throws ArithmeticException if m is less than or equal to 0
	 */
	public BigInteger mod(BigInteger m) {
		if (m.signum() <= 0) {
			throw new ArithmeticException("Modulus <= 0" + m);
		}

		return null;
	}

	/**
	 * 对于未被导出的方法，作为包的创建者，你可以控制这些方法将在哪些情况下被调用，因此你可以，也应该确保只将有交换的参数值传递进来。因此非公有的方法通常应该使用断言
	 * 
	 * 在运行层面将-ea参数传递给JVM
	 * 
	 * @param a
	 * @param offset
	 * @param length
	 */
	private static void sort(long a[], int offset, int length) {
		assert a != null;
		assert offset >= 0 && offset <= a.length;
		assert length >= 0 && length <= a.length - offset;

		System.out.println(a);
	}

	public static void main(String[] args) {
		
		
		ParamException.sort(null, 0, 12);
	}

}
