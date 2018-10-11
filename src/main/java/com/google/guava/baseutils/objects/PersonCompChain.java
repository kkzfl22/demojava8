package com.google.guava.baseutils.objects;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

public class PersonCompChain implements Comparable<PersonCompChain> {

	private String lastName;

	private String firstName;

	private int zipCode;

	public PersonCompChain(String lastName, String firstName, int zipCode) {
		super();
		this.lastName = lastName;
		this.firstName = firstName;
		this.zipCode = zipCode;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PersonCompChain [lastName=");
		builder.append(lastName);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", zipCode=");
		builder.append(zipCode);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 使用guava来进行改写下
	 */
	@Override
	public int compareTo(PersonCompChain o) {

		return ComparisonChain.start()
				// 比对字句
				.compare(this.lastName, o.lastName)
				// 比对群
				.compare(this.firstName, o.firstName)
				// 比对code
				.compare(this.zipCode, o.zipCode)
				// 返回值
				.result();
	}

	public static void main(String[] args) {
		List<PersonCompChain> listPsers = Lists.newArrayList(new PersonCompChain("k", "K", 1),
				new PersonCompChain("k", "K", 5), new PersonCompChain("k", "K", 3), new PersonCompChain("k", "K", 7));

		System.out.println(listPsers);
		Collections.sort(listPsers);
		System.out.println(listPsers);
	}

}
