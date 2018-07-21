package com.demo.effectivejava.seven.fortytwo.good;

public class Sample {
	
	/**
	 * 声明方法带有两个参数，一个指定类型的正常参数，另一个是这种类型的varargs参数
	 * @param firstArg
	 * @param remainingArgs
	 * @return
	 */
	static int min(int firstArg,int... remainingArgs)
	{
		int min = firstArg;
		
		for (int i : remainingArgs) {
			if(i < min)
			{
				min = i;
			}
		}
		
		return min;
	}

}
