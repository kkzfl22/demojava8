package com.demo.effectivejava.thirty.five.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * doc:简直的运行测试类方法
 * 
 * @author liujun
 * @date 2018/07/16
 */
public class RunTests {

	public static void main(String[] args) throws ClassNotFoundException {
		int tests = 0;
		int passed = 0;

		String testClassName = "com.mockedandInjectable.effectivejava.thirty.five.Sample";

		Class testClass = Class.forName(testClassName);

		for (Method m : testClass.getDeclaredMethods()) {
			// 检查是否使用了注解Test
			if (m.isAnnotationPresent(Test.class)) {
				tests++;

				try {
					m.invoke(null);
					passed++;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					Throwable able = e.getCause();
					System.out.println(m + " failed " + able);
				} catch (Exception exc) {
					System.out.println("INVALID @Test : " + m);
				}
			}
		}

		System.out.printf("Passed %d ,Failed : %d %n", passed, tests - passed);
	}
}
