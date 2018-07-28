package com.guava.collections;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 不可变集合
 * 
 * 不可变对象有很多优点，包括：
 * 
 * 当对象被不可信的库调用时，不可变形式是安全的； 不可变对象被多个线程调用时，不存在竞态条件问题
 * 不可变集合不需要考虑变化，因此可以节省时间和空间。所有不可变的集合都比它们的可变形式有更好的内存利用率（分析和测试细节）；
 * 不可变对象因为有固定不变，可以作为常量来安全使用。
 * 
 * 
 * 
 * 
 * @author liujun
 * @date 2018/07/28
 */
public class GuavaFinalCollection {

	/**
	 * 不可变的set对象
	 */

	public void testImmuTableSet() {
		final Set<String> COLER_NAMES = ImmutableSet.of("red", "green", "orange", "blue", "purple");

		// 针对不可变的集合添加会发生java.lang.UnsupportedOperationException
		// COLER_NAMES.add("22");
		// COLER_NAMES.remove("22");
		System.out.println(COLER_NAMES);

		final Set<String> COLER_NAMES2 = ImmutableSet.copyOf(COLER_NAMES);

		System.out.println("obj1:" + System.identityHashCode(COLER_NAMES));
		System.out.println("obj2:" + System.identityHashCode(COLER_NAMES2));
		System.out.println(COLER_NAMES2);

		final Set<String> GOOGLE_COLORS22 = ImmutableSet.<String> builder()
				// 添加默认颜色信息
				.addAll(COLER_NAMES)
				// 添加棕色信息
				.add("brown")
				// 添加绿色
				.add("green2").build();

		System.out.println("ttobj1:" + System.identityHashCode(COLER_NAMES));
		System.out.println("ttobj2:" + System.identityHashCode(GOOGLE_COLORS22));
		System.out.println("result:" + GOOGLE_COLORS22);
		GOOGLE_COLORS22.add("32");
	}

	@Test
	public void ImmutableMapTest() {
		Map<String, String> map = ImmutableMap.of("aa", "v1", "ab", "v1", "ac", "v1", "ad", "v1", "af", "v1");

	}

}
