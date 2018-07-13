package com.demo.effectivejava.thirty.enums.good;

public class OperSwitch {

	/**
	 * 如果枚举中的switch语句不是在枚举中实现特定于常量的行为是一种很好的选择，那么它们不宜什么用处？
	 * 
	 * 枚举中的switch语句适合于给外部的检举类型增加特定于常量的行为，假如Operation枚举不受你的控制，
	 * 
	 * 你希望它有一个实例方法来返回每个运算的反运算，你可以用下列静态方法模拟这种效果
	 * 
	 * @param op
	 * @return
	 */
	public static Operation inverse(Operation op) {
		switch (op) {
		case PLUS:
			return Operation.MTNUS;
		case MTNUS:
			return Operation.PLUS;
		case TIMES:
			return Operation.DIVIDE;
		case DIVIDE:
			return Operation.TIMES;
		default:
			throw new AssertionError("Unkonown op:" + op);
		}

	}

}
