package com.guava.baseutils.objects;

import org.junit.Test;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * 常见Objects方法
 * 
 * @author liujun
 * @date 2018/07/27
 */
public class ObjectsTest {

	/**
	 * 当一个对象中的字段可以为null时，实现Object.equals方法会很痛苦，
	 * 
	 * 因为不得不分别对它们进行null检查。使用Objects.equal帮助你执行null敏感的equals判断，
	 * 
	 * 从而避免抛出NullPointerException。
	 */
	@Test
	public void equals() {
		System.out.println(Objects.equal("a", "a"));// returns true
		System.out.println(Objects.equal(null, "a"));// returns false
		System.out.println(Objects.equal("a", null));// returns false
		System.out.println(Objects.equal(null, null));// returns true
	}

	/**
	 * 用对象的所有字段作散列[hash]运算应当更简单。
	 * 
	 * Guava的Objects.hashCode(Object...)会对传入的字段序列计算出合理的、顺序敏感的散列值。
	 * 
	 * 你可以使用Objects.hashCode(field1, field2, …, fieldn)来代替手动计算散列值。
	 */
	@Test
	public void hashCodeout() {
		System.out.println(Objects.hashCode("a"));
	}

	@Test
	public void toStringOut() {
		// Returns "ClassName{x=1}"
		System.out.println(MoreObjects.toStringHelper(this).add("x", 1).toString());
		// Returns "MyObject{x=1}"
		System.out.println(MoreObjects.toStringHelper("MyObject").add("object", 2).toString());
	}

}
