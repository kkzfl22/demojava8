package com.demo.effectivejava.thirty.one.bad;

/**
 * doc:许多枚举天生就与一个单独的int值相关联，所有的枚举都有一个ordinal方法
 * 
 * 非常难于维护，如果常量进行重新排序 ，numberofMusicians方法就会遭到破坏
 * 
 * 
 * 
 * @author liujun
 * @date 2018/07/13
 */
public enum Ensemble {

	SOLE, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET, OCTET, NONET, DECTET;

	/**
	 * 
	 * ordinal()返回枚举常量的序数（它在枚举声明中的位置，其中初始常量序数为零）。 大多数程序员不会使用此方法。它被设计用于复杂的基于枚举的数据结构，比如 EnumSet
	 * 和 EnumMap。
	 * 
	 * @return
	 */
	public int numberOfMusicians() {
		return ordinal() + 1;
	}

	public static void main(String[] args) {
		System.out.println(Ensemble.SOLE.numberOfMusicians());
		System.out.println(Ensemble.DUET.numberOfMusicians());
		System.out.println(Ensemble.TRIO.numberOfMusicians());
		System.out.println(Ensemble.QUARTET.numberOfMusicians());
	}

}
