package com.demo.effectivejava.seven.fortyone.bad;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SetList {

	public static void main(String[] args) {
		Set<Integer> set = new TreeSet<>();
		List<Integer> list = new ArrayList<>();
		
		for (int i = -3; i < 3; i++) {
			set.add(i);
			list.add(i);
		}
		
		for (int i = 0; i < 3; i++) {
			set.remove(i);
			//因为list有两个remove接口，remove(object),remove(int),这里会自动进行拆箱操作，所以会调用remove(int),
			//解决此问题需要显示的转换为Object，不拆箱操作,list.remove((Integer)i);
			list.remove(i);
			//list.remove((Integer)i);
		}
		
		System.out.println(set + " " + list);
	}

}
