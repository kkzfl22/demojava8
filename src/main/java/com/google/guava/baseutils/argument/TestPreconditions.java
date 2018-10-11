package com.google.guava.baseutils.argument;

import org.junit.Test;

import com.google.common.base.Preconditions;

/**
 * doc:前置条件：让方法调用的前置条件判断更简单。
 * 
 * @author liujun
 * @date 2018/07/27
 */
public class TestPreconditions {

	@Test
	public void checkArgument() {
		// 检查boolean是否为true，用来检查传递给方法的参数。 IllegalArgumentException
		Preconditions.checkArgument(true);
		Preconditions.checkArgument(false);
	}

	@Test
	public void checkNotNull() {
		// 检查value是否为null，该方法直接返回value，因此可以内嵌使用checkNotNull。 NullPointerException
		Preconditions.checkNotNull(true);
		Preconditions.checkNotNull(null);
	}

	@Test
	public void checkState() {
		// 用来检查对象的某些状态。 IllegalStateException
		Preconditions.checkState(true);
		Preconditions.checkState(false, "check error");
	}

	@Test
	public void checkElementIndex() {
		// 检查index作为索引值对某个列表、字符串或数组是否有效。index>=0 && index<size *
		// IndexOutOfBoundsException
		Preconditions.checkElementIndex(2, 5);
		Preconditions.checkElementIndex(2, 1);
	}

	@Test
	public void checkPositionIndex() {
		// 检查index作为位置值对某个列表、字符串或数组是否有效。index>=0 && index<=size *
		// IndexOutOfBoundsException
		Preconditions.checkPositionIndex(5, 5);
		Preconditions.checkPositionIndex(5, 2);
	}

	@Test
	public void checkPositionIndexes() {
		// 检查[start, end]表示的位置范围对某个列表、字符串或数组是否有效* IndexOutOfBoundsException
		Preconditions.checkPositionIndexes(2, 5, 8);
	}

}
