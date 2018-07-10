package com.demo.effectivejava.thirty.enums.good;

import java.util.HashMap;
import java.util.Map;

/**
 * doc:在该方法就紧跟在每个常量之后，即使你真的忘记了，编译器也会提醒你，
 * 
 * 因为枚举类型中的抽象方法必须被它所有常量中的具体方法所覆盖
 * 
 * @author liujun
 * @date 2018/07/10
 */
public enum Operation3 {

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

	Operation3(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	abstract double apply(double x, double y);

	private static final Map<String, Operation3> stringtoEnum = new HashMap<String, Operation3>();

	static {
		for (Operation3 op : values()) {
			stringtoEnum.put(op.symbol, op);
		}
	}

	public static Operation3 fromString(String symbol) {
		return stringtoEnum.get(symbol);
	}

	public static void main(String[] args) {

		double value = Operation3.fromString("+").apply(100, 200);

		System.out.println("count value :" + value);
	}
}
