package com.demo.effectivejava.thirty.third.bad;

import java.util.HashSet;
import java.util.Set;

/**
 * doc: 假设有一个香草的数组，表示一座花园中的植物,如果想按照类型（1年生，多年生或者2年生植物)进行组织之后将这些植物列出来，
 * 如果这样的话，需要构建三个集合中，每种类型一个，并且遍历整座花园，将每种香草放到相应的集合中，
 * 有些程序员会将这些集合放到一个按照类型的序数进行索引的数组中来实现这一点
 * 
 * @author liujun
 * @date 2018/07/13
 */
public class Herb {

	public enum Type {
		ANNUAL, PERENNIAL, BIENNIAL
	}

	private final String name;

	private final Type type;

	Herb(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Herb [name=" + name + ", type=" + type + "]";
	}

	/**
	 * 问题1,因为数组不能与泛型兼容，程序需要进行未受检的转换，并且不能正确无误地进行编译。 因为数组不知道它的索引代表着什么，你必须手工标注这些索引的输出.
	 *
	 * 问题2，当你访问一个按照枚举的序数进行索引 的数组时，使用正确的int值 就是你的职责了，int不能提供枚举的类型安全 你如果使用了错误的值
	 * ，程序就会悄悄的完成错误的工作，或者幸运的话，会抛出ArrayIndexOutOfBoundException异常
	 *
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void print() {

		Herb[] garden = new Herb[3];

		Set<Herb>[] herbsByType = new Set[Herb.Type.values().length];

		for (int i = 0; i < herbsByType.length; i++) {
			herbsByType[i] = new HashSet<>();
		}

		for (Herb h : garden) {
			herbsByType[h.type.ordinal()].add(h);
		}

		for (int i = 0; i < herbsByType.length; i++) {
			System.out.printf("%s:%s%n", Herb.Type.values()[i], herbsByType[i]);
		}

	}
	
}
