package com.demo.effectivejava.thirty.enums.good;

/**
 * doc:最佳实践
 * 
 * @author liujun
 * @date 2018/07/10
 */
public enum Operation {

	PLUS {
		double apply(double x, double y) {
			return x + y;
		}
	},

	MTNUS {
		double apply(double x, double y) {
			return x - y;
		}
	},

	TIMES {
		double apply(double x, double y) {
			return x * y;
		}
	},

	DIVIDE {
		double apply(double x, double y) {
			return x / y;
		}
	}
	;

	abstract double apply(double x, double y);
}
