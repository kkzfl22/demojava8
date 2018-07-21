package com.demo.effectivejava.seven.fortytwo.bad;

public class Sample {

	static int sum(int... args) {
		int sum = 0;
		for (int i : args) {
			sum += i;
		}

		return sum;
	}

	/**
	 * 想要计算多个int参数的最小值 .如果客户端没有传递参数，那这个方法的定义就不太好了。可以运行时检查数组的长度
	 * 
	 * 这种解决方法有几个问题:
	 * 
	 * 1,如果客户端调用这个方法时，并没有传递参数进行，就它就在运行时而不是编译时失败。
	 * 
	 * 2，这段 代码不是很美观
	 * 
	 * @param args
	 * @return
	 */
	static int min(int... args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("Too few arguments");
		}

		int min = args[0];

		for (int i = 1; i < args.length; i++) {
			if (args[i] < min) {
				min = args[i];
			}
		}

		return min;
	}

}
