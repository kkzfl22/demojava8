package com.guava.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultiset;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;

public class GuavaCollection {

	private List<String> wordList = new ArrayList<>();

	@Before
	public void before() {
		wordList.add("count");
		wordList.add("word");
		wordList.add("the");
	}

	private void addItem(Multiset<String> multSet) {
		multSet.add("1");
		multSet.add("d");
		multSet.add("e");
		multSet.add("a");
		multSet.add("b");
		multSet.add("c");
		multSet.add("d");
		multSet.add("d");
		multSet.add("a");
		multSet.add("b");
		multSet.add("c");
		multSet.add("b");
		multSet.add("b");
		multSet.add("b");
	}

	public void countDocOld() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String word : wordList) {
			Integer count = map.get(word);
			map.put(word, (count == null) ? 1 : count + 1);
		}
		// count word “the”
		Integer count = map.get("the");
		System.out.println("count the count:" + count);
	}

	/**
	 * Guava提供了一个新集合类型 Multiset，它可以多次添加相等的元素。
	 * 
	 * 维基百科从数学角度这样定义Multiset：”集合[set]概念的延伸，
	 * 
	 * 它的元素可以重复出现…与集合[set]相同而与元组[tuple]相反的是，
	 * 
	 * Multiset元素的顺序是无关紧要的：Multiset {a, a, b}和{a, b,
	 * a}是相等的”。——译者注：这里所说的集合[set]是数学上的概念，Multiset继承自JDK中的Collection接口，而不是Set接口，所以包含重复元素并没有违反原有的接口契约。
	 */
	public void countDocNew() {
		HashMultiset<String> multdocSet = HashMultiset.create();
		multdocSet.addAll(wordList);
		// 给定元素在Multiset中的计数
		int countMult = multdocSet.count("the");
		System.out.println("count the the num new :" + countMult);
	}

	/**
	 * 把重复的元素放入集合
	 */
	public void userMultiset() {

		Multiset<String> multSet = HashMultiset.create();

		addItem(multSet);

		System.out.println("count multSet B num" + multSet.count("b"));
		System.out.println("totle size:" + multSet.size());

		// 打印元素信息,Multiset中不重复元素的集合，类型为Set<E>
		Set<String> sets = multSet.elementSet();

		System.out.println("set interface value:" + sets);

		System.out.print("multSet [");
		for (Iterator<String> iter = multSet.iterator(); iter.hasNext();) {
			System.out.print(iter.next() + ",\t");
		}
		System.out.print("]");

		System.out.println();

		System.out.print("multSet count [");
		for (Multiset.Entry<String> entry : multSet.entrySet()) {
			System.out.print(entry.getElement() + ";" + entry.getCount() + "\t");
		}
		System.out.print("]");
		System.out.println();

		// 移除2次
		multSet.remove("b", 3);
		System.out.println("b number:" + multSet.count("b"));
	}

	/**
	 * 进行Multiset的使用
	 */
	// @Test
	public void MultisetHashMapUse() {
		Multiset<String> mapMapSet = HashMultiset.create();
		addItem(mapMapSet);
		System.out.println("HashMultiset" + mapMapSet);
	}

	/**
	 * 基于红黑树（Red-Black tree）的 NavigableMap 实现。
	 * 
	 * 该映射根据其键的自然顺序进行排序，
	 */
	// @Test
	public void MultisetTreeMultisetUse() {
		Multiset<String> mapMapSet = TreeMultiset.create();
		addItem(mapMapSet);
		System.out.println("TreeMultiset" + mapMapSet);
	}

	/**
	 * 进行Multiset的使用,按插入顺序进行操作
	 */
	// @Test
	public void MultisetLinkedHashMultisetUse() {
		Multiset<String> mapMapSet = LinkedHashMultiset.create();
		addItem(mapMapSet);
		System.out.println("LinkedHashMultiset" + mapMapSet);
	}

	/**
	 * 支持获取的完全并发和更新的所期望可调整并发的哈希表
	 */
	// @Test
	public void MultisetConcurrentHashMapUse() {
		Multiset<String> mapMapSet = TreeMultiset.create();
		addItem(mapMapSet);
		System.out.println("ConcurrentHashMap" + mapMapSet);
	}

	/**
	 * 支持获取的完全并发和更新的所期望可调整并发的哈希表
	 */
	// @Test
	public void MultisetImmutableMapUse() {
		Multiset<String> mapMapSet = ImmutableMultiset.<String> builder().add("ad").build();
		addItem(mapMapSet);
		System.out.println("ImmutableMap" + mapMapSet);
	}

	/**
	 * Guava的Multimap就提供了一个方便地把一个键对应到多个值的数据结构
	 */
	public void multMapTest() {

		Multimap<String, String> multmap = ArrayListMultimap.create();

		for (int i = 0; i < 10; i++) {
			multmap.put("id", String.valueOf(i));
		}

		System.out.println(multmap.size());
		System.out.println(multmap.keys());
		System.out.println(multmap);
	}

	/**
	 * BiMap首先也是一种Map，他的特别之处在于，既提供键到值的映射，也提供值到键的映射，所以它是双向Map.
	 */
	public void biMapTest() {

		BiMap<String, String> weekNameMap = HashBiMap.create();
		weekNameMap.put("星期一", "Monday");
		weekNameMap.put("星期二", "Tuesday");
		weekNameMap.put("星期三", "Wednesday");
		weekNameMap.put("星期四", "Thursday");
		weekNameMap.put("星期五", "Friday");
		weekNameMap.put("星期六", "Saturday");
		weekNameMap.put("星期日", "Sunday");

		System.out.println("星期一:" + weekNameMap.get("星期一"));
		System.out.println("map:" + weekNameMap);
		System.out.println("Friday：" + weekNameMap.inverse().get("Friday"));
		System.out.println("inverse：" + weekNameMap.inverse());
	}

	/**
	 * ClassToInstanceMap<B>实现了Map<Class<? extends B>, B> -- 换句话说,
	 * (他是一个由B的子类和B的实例构成的Map)
	 */
	public void classToInstanceMapTest() {
		ClassToInstanceMap<Number> numberDefaults = MutableClassToInstanceMap.create();
		numberDefaults.putInstance(Integer.class, Integer.valueOf(0));
		System.out.println(numberDefaults.getInstance(Integer.class));
	}

	public void tableTest() {
		Table<String, String, Object> table = HashBasedTable.create();
		table.put("student", "name", "kk");
		table.put("student", "age", 11);
		table.put("student", "hight", 171);
		table.put("student", "address", "shanghai");

		System.out.println(table.get("student", "name"));

		for (Map.Entry<String, Map<String, Object>> iter : table.rowMap().entrySet()) {
			System.out.println(iter.getKey() + ": " + iter.getValue());
		}
	}

	/**
	 * RangeSet描述了一组不相连的、非空的区间。当把一个区间添加到可变的RangeSet时，所有相连的区间会被合并，空区间会被忽略。
	 */

	public void rangeSetTest() {
		RangeSet<Integer> rangSet = TreeRangeSet.create();
		rangSet.add(Range.closed(1, 10));
		System.out.println(rangSet);
		rangSet.add(Range.closed(11, 25));
		System.out.println(rangSet);
		rangSet.add(Range.closedOpen(14, 32));
		System.out.println(rangSet);

		rangSet.remove(Range.open(5, 13));
		System.out.println(rangSet);
	}

	@Test
	public void rangeMapTest() {
		RangeMap<Integer, String> ranMap = TreeRangeMap.create();
		ranMap.put(Range.openClosed(1, 20), "abc");//// [(1..20]=abc]
		System.out.println(ranMap);
		ranMap.put(Range.open(10, 15), "abf");// [(1..10]=abc, (10..15)=abf, [15..20]=abc]
		System.out.println(ranMap);
	}

}
