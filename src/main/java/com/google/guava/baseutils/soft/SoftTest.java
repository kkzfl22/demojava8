package com.google.guava.baseutils.soft;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

public class SoftTest {

	/**
	 * 自定义排序比较器实现
	 */
	// @Test
	public void numSortCustom() {
		List<Integer> softBase = Lists.newArrayList(5, 3, 1, 8, 7);

		Ordering<Integer> byLengthOrder = new Ordering<Integer>() {
			@Override
			public int compare(Integer left, Integer right) {
				return Ints.compare(left, right);
			}
		};

		System.out.println(softBase);
		Collections.sort(softBase, byLengthOrder);

		System.out.println(softBase);

	}

	/**
	 * 对可排序类型做自然排序，如数字按大小，日期按先后排序
	 */
	// @Test
	public void numSortnatural() {
		List<Integer> softBase = Lists.newArrayList(5, 3, 1, 8, 7, 3);

		System.out.println(softBase);
		// 对可排序类型做自然排序，如数字按大小，日期按先后排序
		Collections.sort(softBase, Ordering.natural());

		System.out.println(softBase);
	}

	// @Test
	public void numSortReverse() {
		List<Integer> softBase = Lists.newArrayList(5, 3, 1, 8, 7, 3);

		System.out.println(softBase);
		// 对可排序类型做自然排序，如数字按大小，日期按先后排序
		Collections.sort(softBase, Ordering.natural().reverse());

		System.out.println(softBase);
	}

	/**
	 * 按对象的字符串形式做字典排序[lexicographical ordering]
	 */
	// @Test
	public void StrSort() {
		List<String> softBase = Lists.newArrayList("ed", "ab1", "bc", "ac", "ba", "ab2", "ad", "af");

		System.out.println(softBase);
		// 按对象的字符串形式做字典排序[lexicographical ordering]
		Collections.sort(softBase, Ordering.usingToString());

		System.out.println(softBase);
	}

	/**
	 * 按对象的字符串形式做字典排序[lexicographical ordering]
	 */
	// @Test
	public void StrSortReverse() {
		List<String> softBase = Lists.newArrayList("ed", "ab1", "bc", "ac", "ba", "ab2", "ad", "af");

		System.out.println(softBase);
		// 按对象的字符串形式做字典排序[lexicographical ordering]
		Collections.sort(softBase, Ordering.usingToString().reversed());

		System.out.println(softBase);
	}

	/**
	 * 合成另一个比较器，以处理当前排序器中的相等情况。
	 */
	// @Test
	public void StrSortCompound() {
		List<String> softBase = Lists.newArrayList("ed1", "ab", "bc1", "ac", "ba1", "ab2", "ad", "af", "ab1");

		System.out.println(softBase);

		Ordering<String> byLengthOrder = new Ordering<String>() {

			@Override
			public int compare(String left, String right) {
				return left.length() > right.length() ? 1 : left.length() < right.length() ? -1 : 0;
			}
		};

		// 首先检查长度，然后再按字符排序
		Collections.sort(softBase, byLengthOrder.compound(Ordering.usingToString()));

		System.out.println(softBase);
	}

	/**
	 * lexicographical() 返回一个按照字典元素迭代的Ordering；
	 * 
	 * 基于处理类型T的排序器，返回该类型的可迭代对象Iterable<T>的排序器。
	 */
	// @Test
	public void StrSortlexicographical() {
		List<String> softBase = Lists.newArrayList("ed1", "ab", "bc1", "ac", "ba1", "ab2", "ad", "af", "ab1");

		System.out.println(softBase);

		Ordering<Iterable<String>> tts = Ordering.natural().compound(Ordering.usingToString()).lexicographical();

		System.out.println(tts);
	}

	class Foo {
		@Nullable
		String sortedBy;
		int notSortedBy;

		public Foo(String sortedBy, int notSortedBy) {
			super();
			this.sortedBy = sortedBy;
			this.notSortedBy = notSortedBy;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Foo [sortedBy=");
			builder.append(sortedBy);
			builder.append(", notSortedBy=");
			builder.append(notSortedBy);
			builder.append("]");
			return builder.toString();
		}

	}

	@Test
	public void onResultOf() {

		List<Foo> softBase = Lists.newArrayList(new Foo("ab", 12), new Foo("ae", 10), new Foo("ad", 15),
				new Foo("af", 8));

		System.out.println(softBase);
		// 对集合中元素先调用Function，再按返回值用当前排序器排序。
		Ordering<Foo> ordering = Ordering.natural().onResultOf(t -> t.notSortedBy);

		Collections.sort(softBase, ordering);

		System.out.println(softBase);
	}

}
