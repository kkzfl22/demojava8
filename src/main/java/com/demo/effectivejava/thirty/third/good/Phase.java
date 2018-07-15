package com.demo.effectivejava.thirty.third.good;

import java.util.EnumMap;
import java.util.Map;

/**
 * doc1,利用EnumMap可以做的更好一些 * 与枚举类型键一起使用的专用 Map实现。
 * 2枚举映射中所有键都必须来自单个枚举类型，该枚举类型在创建映射时显式或隐式地指定。
 * 
 * 3 枚举映射在内部表示为数组。此表示形式非常紧凑且高效。
 * 
 * 4枚举映射根据其键的自然顺序 来维护（该顺序是声明枚举常量的顺序）。
 * 
 * 5:用来表示多维的系统，可以非常方便的获取,性能没有损失
 * 
 * @author liujun
 * @date 2018/07/15
 */
public enum Phase {
	SOLID, LIQUID, GAS;

	public enum Transition {
		MELT(SOLID, LIQUID), FREEZW(LIQUID, SOLID),

		BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),

		SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

		private final Phase src;
		private final Phase dst;

		Transition(Phase src, Phase dst) {
			this.src = src;
			this.dst = dst;
		}

		private static final Map<Phase, Map<Phase, Transition>> M = new EnumMap<>(Phase.class);

		static {
			for (Phase phase : Phase.values()) {
				M.put(phase, new EnumMap<>(Phase.class));
			}

			for (Transition trans : Transition.values()) {
				M.get(trans.src).put(trans.dst, trans);
			}
		}

		public static Transition from(Phase src, Phase dst) {
			return M.get(src).get(dst);
		}
	}

	public static void main(String[] args) {
		System.out.println(Phase.Transition.from(GAS, SOLID));
	}

}
