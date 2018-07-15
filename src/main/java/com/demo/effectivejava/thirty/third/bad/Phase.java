package com.demo.effectivejava.thirty.third.bad;

/**
 * doc:问题，编译器无法知道序数和数组索引 之间的关系，如果在过滤表中出了错，或者 修改phase或者 Phase.Transition枚举类型的时候
 * 忘记将它更新，程序就会运行时失败。这种
 * 失败的形式可能为ArrayIndexOutOfBoundsException,NullPointerException或者 没有任何提示的错误行为
 * 
 * @author liujun
 * @date 2018/07/15
 */
public enum Phase {

	OLID, LIQUID, GAS;

	public enum Transition {
		MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

		private static final Transition[][] TRANSITIONS = {
				// 元素1
				{ null, MELT, SUBLIME },
				// 元素2
				{ FREEZE, null, BOIL },
				// 元素3
				{ DEPOSIT, CONDENSE, null } };

		public static Transition from(Phase src, Phase dst) {
			return TRANSITIONS[src.ordinal()][dst.ordinal()];
		}
	}

}
