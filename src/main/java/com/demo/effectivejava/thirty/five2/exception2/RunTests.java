package com.demo.effectivejava.thirty.five2.exception2;

import java.lang.reflect.Method;

public class RunTests {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws ClassNotFoundException {
		int tests = 0;
		int passed = 0;

		String className = "com.mockedandInjectable.effectivejava.thirty.five2.exception2.Sample3";

		Class testClass = Class.forName(className);

		for (Method m : testClass.getDeclaredMethods()) {
			// 检查注解是否存在此元素
			if (m.isAnnotationPresent(ExceptionTests.class)) {
				try {
					tests++;
					m.invoke(null);
					System.out.printf("Tests %d failed : no exception %n", m);
				} catch (Throwable e) {
					Throwable exc = e.getCause();
					Class<? extends Exception>[] excTypes = m.getAnnotation(ExceptionTests.class).value();
					int oldPassed = passed;
					for (Class<? extends Exception> excType : excTypes) {
						if (excType.isInstance(exc)) {
							passed++;
							break;
						}
					}
					if (passed == oldPassed) {
						System.out.printf("Tests %s failed : %s %n", m, exc);
					}
				}
			}

		}
		System.out.printf("Passed:%d ,Failed: %d %n", passed, tests - passed);
	}

}
