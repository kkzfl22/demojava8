package com.google.guava.baseutils.optional.demo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Optional;

public class CountAge {

	private List<Employee> getList() {
		List<Employee> result = new ArrayList<>();

		Employee emp1 = new Employee("张三", 20);
		result.add(emp1);

		Employee emp2 = new Employee("李四", 21);
		result.add(emp2);

		Employee emp3 = new Employee("王五", 22);
		result.add(emp3);

		result.add(null);

//		final List<Employee> list = Lists.newArrayList(new Employee("em1", 30), new Employee("em2", 40), null,
//				new Employee("em4", 18));

		return result;

	}

	@Test
	public void oldCode() {
		List<Employee> list = getList();
		int sum = 0;
		// 计算一组员工的总年龄
		for (Employee emp : list) {
			if (emp != null) {
				sum += emp.getAge();
			}
		}

		System.out.println("oldCode 总年龄:" + sum);
	}

	@Test
	public void newCode() {
		List<Employee> list = getList();

		int sum = 0;

		for (Employee emp : list) {
			sum += Optional.fromNullable(emp).or(new Employee("default", 0)).getAge();
		}

		System.out.println("newcod 总年龄:" + sum);
	}

}
