package com.guava.baseutils.optional;

import org.junit.Test;
import com.google.common.base.Optional;

public class OptionalTest {

	public void testOptional() {
		//获得一个Optional对象，其内部包含了一个非null的T数据类型实例，若T=null，则立刻报错。
		Optional<Integer> intopt = Optional.of(1);
		// 如果Optional实例持有者包含一个非null的实例，则返回True，否则返回false
		if (intopt.isPresent()) {
			System.out.println("possible isPresent:" + intopt.isPresent());
			System.out.println("possible value:" + intopt.get());
		}
	}
	
	@Test
	public void testOption2()
	{
		//获得一个Optional对象，其内部包含了一个非null的T数据类型实例，若T=null，则立刻报错。
		Optional<Integer> possOpt = Optional.of(6);
		//获得一个Optional对象，其内部包含了空值,创建引用缺失的Optional实例
		Optional<Integer> absOpt = Optional.absent();
		//Optional.fromNullable(T)：将一个T的实例转换为Optional对象，T的实例可以不为空，
		//也可以为空[Optional.fromNullable(null)，和Optional.absent()等价。
		Optional<Integer> nullableOpt = Optional.fromNullable(null);
		Optional<Integer> notnullableOpt = Optional.fromNullable(10);
		
		
		
		if(possOpt.isPresent())
		{
			System.out.println("Possible isParent:"+possOpt.isPresent());
			//T get()：返回Optional包含的T实例，该T实例必须不为空；否则，对包含null的Optional实例调用get()会抛出一个IllegalStateException异常
			System.out.println("Possible value:"+possOpt.get());
		}
		
		if(absOpt.isPresent())
		{
			System.out.println("absentOpt isParent:"+absOpt.isPresent());
		}
		
		if(nullableOpt.isPresent())
		{
			System.out.println("fromnullable isParent:"+nullableOpt.isPresent());
		}
		
		if(notnullableOpt.isPresent())
		{
			System.out.println("NoNullableOpt isParent:"+notnullableOpt.isPresent());
		}
	}

}
