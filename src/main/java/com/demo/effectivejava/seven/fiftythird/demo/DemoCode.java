package com.demo.effectivejava.seven.fiftythird.demo;

import java.util.Arrays;
import java.util.Set;

/**
 * doc:功能很强大，但有两个问题
 * 
 * 1,会产生3个运行时错误，如果不用反射方式的实例，3个错误都会成为编译时的错误。
 * 2，根据类名生成它的实例需要20行冗长的代码，而调用构造方法可以非常简洁的只使用一行代码。
 * 
 * 
 * @author liujun
 * @date 2018/07/22
 */
public class DemoCode {

	@SuppressWarnings({ "unchecked", "static-access" })
	public static void main(String[] args) {
		Class<?> ci = null;

		String className = "java.util.HashSet";

		try {
			ci = ci.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Class not found");
			System.exit(1);
		}

		Set<String> s = null;
		try {
			s = (Set<String>) ci.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.addAll(Arrays.asList("11", "22").subList(0, 2));
		System.out.println(s);
	}

}
