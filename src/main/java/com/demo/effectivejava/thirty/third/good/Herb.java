package com.demo.effectivejava.thirty.third.good;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
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
	 * 有一种更好的方法可以达到同样的效果，数组实际上充当着从枚举到值的映射，因此可能还要用到Map,
	 * 
	 * 更具体的说，有一种非常快速的Map实现专门用于枚举键，称作java.util.EnumMap
	 */
	public void print() {

		Herb[] garden = new Herb[] { new Herb("1", Herb.Type.ANNUAL) };

		Map<Herb.Type, Set<Herb>> herbsByType = new EnumMap<Herb.Type, Set<Herb>>(Herb.Type.class);

		for (Herb.Type t : Herb.Type.values()) {
			herbsByType.put(t, new HashSet<Herb>());
		}

		for (Herb herb : garden) {
			herbsByType.get(herb.type).add(herb);
		}

		System.out.println(herbsByType);

	}

	public static void main(String[] args) {

		Herb herss = new Herb("this", Herb.Type.ANNUAL);
		herss.print();

	}

}
