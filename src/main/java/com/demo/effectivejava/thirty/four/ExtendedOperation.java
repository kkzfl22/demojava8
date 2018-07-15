package com.demo.effectivejava.thirty.four;

import java.util.Arrays;
import java.util.Collection;

public enum ExtendedOperation implements Operation {

	EXP("^") {
		@Override
		public double apply(double x, double y) {
			return Math.pow(x, y);
		}
	},

	REMAINDER("%") {
		@Override
		public double apply(double x, double y) {
			return x % y;
		}

	};

	private final String symbol;

	ExtendedOperation(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}

	public static void main(String[] args) {
		double x = 10d;
		double y = 11d;
		test(ExtendedOperation.class, x, y);
		System.out.println("22--------------------");
		test(Arrays.asList(BasicOperation.values()), x, y);

	}

	/**
	 * <T extends Enum<T> & Operation>
	 * 确保了class对象妈表示枚举又表示Operation的子类型，这正是遍历元素和执行与每个元素相关联的操作时所需要的
	 * 
	 * @param opset
	 * @param x
	 * @param y
	 */
	private static <T extends Enum<T> & Operation> void test(Class<T> opset, double x, double y) {
		for (Operation oper : opset.getEnumConstants()) {
			System.out.printf("%f %s %f = %f \n", x, oper, y, oper.apply(x, y));
		}
	}

	/**
	 * doc:使用Collection<? extends Operation>，就有个有限制的通配符类型(bounded wildcar
	 * type)作这opSet参数类型
	 * 
	 * @param opset
	 * @param x
	 * @param y
	 */
	private static void test(Collection<? extends Operation> opset, double x, double y) {
		for (Operation op : opset) {
			System.out.printf("%f %s %f = %f %n", x, op, y, op.apply(x, y));
		}
	}
}
