package com.guava.collections.collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class CollectionsTest {

	public void staticfunction() {
		// List<String> list = new
		// ArrayList<String>();

		List<String> list = Lists.newArrayList();
		Map<String, String> map = Maps.newLinkedHashMap();

		Set<String> sets = Sets.newHashSet("1", "2");
		List<String> elements = Lists.newArrayList("alpha", "beta", "gama");

		// 创建10个大小的固定数组
		List<String> elements2 = Lists.newArrayListWithCapacity(10);
		// 创建一个可扩容的动态数组
		List<String> exappo100 = Lists.newArrayListWithExpectedSize(100);
	}

	public void IterablesTest() {

		List<String> elements2 = Lists.newArrayListWithCapacity(10);
		elements2.add("1");
		elements2.add("2");
		elements2.add("3");

		Iterable<String> result = Iterables.concat(elements2);
	}

	public void uniqueIndex() {
		List<String> elements2 = Lists.newArrayListWithCapacity(10);
		elements2.add("1");
		elements2.add("22");
		elements2.add("333");
		elements2.add("aaaa");
		ImmutableMap<Integer, String> stringsByIndex = Maps.uniqueIndex(elements2, new Function<String, Integer>() {
			public Integer apply(String string) {
				return string.length();
			}
		});
		ImmutableMap<String, String> stringsByIndex2 = Maps.uniqueIndex(elements2, new Function<String, String>() {
			public String apply(String string) {
				return string;
			}
		});

		System.out.println(stringsByIndex);
		System.out.println(stringsByIndex2);
	}

	/**
	 * 用来比较两个Map以获取所有不同点。该方法返回MapDifference对象
	 */
	public void difference() {
		Map<String, Integer> left = ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 5, "f", 1);
		Map<String, Integer> left2 = ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4, "g", 12);
		MapDifference<String, Integer> diff = Maps.difference(left, left2);

		// 两个Map中都有的映射项，包括匹配的键与值
		System.out.println(diff.entriesInCommon());
		// 键相同但是值不同值映射项。返回的Map的值类型为MapDifference.ValueDifference，以表示左右两个不同的值
		System.out.println(diff.entriesDiffering());
		// 键只存在于左边Map的映射项
		System.out.println(diff.entriesOnlyOnLeft());
		// 键只存在于右边Map的映射项
		System.out.println(diff.entriesOnlyOnRight());
	}

	@Test
	public void ImmutableSetTest() {
		ImmutableSet<String> digits = ImmutableSet.of("zero", "one", "two", "three", "four", "five", "six", "seven",
				"eight", "nine");
		Function<String, Integer> lengthFunction = new Function<String, Integer>() {
			@Override
			public Integer apply(String input) {
				return input.length();
			}
		};

		ImmutableListMultimap<Integer, String> digLength = Multimaps.index(digits, lengthFunction);

		System.out.println(digLength);
	}
}
