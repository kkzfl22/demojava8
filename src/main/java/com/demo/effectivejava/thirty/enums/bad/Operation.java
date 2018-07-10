package com.demo.effectivejava.thirty.enums.bad;

/**
 * doc：进行enum相关的最佳实践
 * 
 * @author liujun
 * @date 2018/07/10
 */
public enum Operation {

	PLUS, MTNUS, TIMES, DIVIDE;

	double apply(double x, double y) {
		switch (this) {
		case PLUS:
			return x + y;
		case MTNUS:
			return x - y;
		case TIMES:
			return x * y;
		case DIVIDE:
			return x / y;
		}

		throw new AssertionError("Unknown op:" + this);
	}

}
