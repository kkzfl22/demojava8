package com.demo.effectivejava.thirty.enums.good;

/**
 * doc:在该方法就紧跟在每个常量之后，即使你真的忘记了，编译器也会提醒你，
 * 
 * 因为枚举类型中的抽象方法必须被它所有常量中的具体方法所覆盖
 * 
 * @author liujun
 * @date 2018/07/10
 */
public enum Operation2 {

	PLUS("+") {
		double apply(double x, double y) {
			return x + y;
		}
	},

	MTNUS("-") {
		double apply(double x, double y) {
			return x - y;
		}
	},

	TIMES("*") {
		double apply(double x, double y) {
			return x * y;
		}
	},

	DIVIDE("/") {
		double apply(double x, double y) {
			return x / y;
		}
	};

	private final String symbol;

	Operation2(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	abstract double apply(double x, double y);

	public static void main(String[] args) {
		double x = Double.parseDouble(args[0]);
		double y = Double.parseDouble(args[1]);
		for (Operation2 op : Operation2.values()) {
			System.out.printf("%f %s %f = %f %n", x, op, y, op.apply(x, y));
		}
	}
}
