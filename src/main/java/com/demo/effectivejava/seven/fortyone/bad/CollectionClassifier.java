package com.demo.effectivejava.seven.fortyone.bad;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionClassifier {

	public static String classify(Set<?> s) {
		return "Set";
	}

	public static String classify(List<?> lst) {
		return "list";
	}

	public static String classify(Collection<?> c) {
		return "unknow collection";
	}
	
	public static String classify2(Collection<?> c)
	{
		return c instanceof Set ? "Set" : c instanceof List ? "List" : "Unknown Collection";
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		Collection<?>[] collections = {
				// 使用hash表参数
				new HashSet<>(),
				// 集合参数
				new ArrayList<BigInteger>(),
				// map表参数
				new HashMap<String, String>().values() };

		for (Collection<?> collection : collections) {
			System.out.println(classify(collection));
		}
		

		// unknow collection
		// unknow collection
		// unknow collection

		// 对于重载方法的选择是静态的，而对于覆盖方法，的选择是动态的。选择被覆盖的方法的正确版本是在运行时进行的，选择的依据是被调用方法所在的对旬的运行时类型

		for (Collection<?> collection : collections) {
			System.out.println(classify2(collection));
		}
		
		
		// 使用此可以解决问题，但还是避免不了做强制转换
		for (Collection<?> collection : collections) {
			if (collection instanceof Set) {
				System.out.println(classify((Set) collection));
			} else if (collection instanceof List) {
				System.out.println(classify((List) collection));
			} else if (collection instanceof Collection) {
				System.out.println(classify((Collection) collection));
			}
		}
	}

}
