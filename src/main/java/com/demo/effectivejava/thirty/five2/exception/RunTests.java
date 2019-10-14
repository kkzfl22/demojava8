package com.demo.effectivejava.thirty.five2.exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RunTests {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws ClassNotFoundException {
		int tests = 0;
		int passed = 0;

		String className = "com.mockedandInjectable.effectivejava.thirty.five2.Sample2";

		Class testClass = Class.forName(className);

		for (Method m : testClass.getDeclaredMethods()) {
			// 如果指定类型的注释存在于此元素上，则返回 true，否则返回 false。
			// 此方法主要是为了便于访问标记注释而设计的。
			if (m.isAnnotationPresent(ExceptinTest.class)) {
				tests++;
				try {
					m.invoke(null);
					System.out.printf("Test %s failed : no exception %n", m);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// e.printStackTrace();
					Throwable exec = e.getCause();
					Class<? extends Exception> extType = m.getAnnotation(ExceptinTest.class).value();
					if (extType.isInstance(exec)) {
						passed++;
					} else {
						System.out.printf("Tests %s failed : expected %s ,got %s %n", m, extType.getName(), exec);
					}
				} catch (Exception e) {
					// e.printStackTrace();
					System.out.printf("INVALID @Test " + m);
				}
			}
		}

		System.out.printf("Passed:%d ,Failed: %d %n", passed, tests - passed);
	}

}
