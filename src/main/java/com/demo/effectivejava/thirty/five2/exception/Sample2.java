package com.demo.effectivejava.thirty.five2.exception;

public class Sample2 {

	@ExceptinTest(ArithmeticException.class)
	public static void m1() {
		int i = 0;
		i = i / i;
	}
	
	@SuppressWarnings("unused")
	@ExceptinTest(ArithmeticException.class)
	public static void m2()
	{
		int[] a = new int[0];
		int i = a[1];
	}
	
	@ExceptinTest(ArithmeticException.class)
	public static void m3() {
	}
	
}
