package com.guava.optional.dmo3;

import java.util.Optional;

import org.junit.Test;

public class Java8Opeional {

	/**
	 * 默认的返回对象信息 ，使用java原生对象
	 */
	private static final User DEFUSER = new User("Unknown", 0);

	public String oldgetName(User u) {
		if (u == null)
			return "Unknown";
		return u.getName();
	}

	public String getName(User u) {
		return Optional.ofNullable(u).orElse(DEFUSER).getName();
	}

	@Test
	public void testRun() {
		User user = new User("kk", 12);
		System.out.println(this.oldgetName(user));
		System.out.println(this.oldgetName(null));

		System.out.println("over");

		System.out.println(this.getName(user));
		System.out.println(this.getName(null));

	}

}
