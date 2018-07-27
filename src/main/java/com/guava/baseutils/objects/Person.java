package com.guava.baseutils.objects;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class Person implements Comparable<Person> {

	private String lastName;

	private String firstName;

	private int zipCode;

	public Person(String lastName, String firstName, int zipCode) {
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
	public int compareTo(Person o) {
		int cmp = lastName.compareTo(o.lastName);

		if (cmp != 0) {
			return cmp;
		}

		cmp = firstName.compareTo(o.firstName);

		if (cmp != 0) {
			return cmp;
		}

		return Integer.compare(zipCode, o.zipCode);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Person [lastName=");
		builder.append(lastName);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", zipCode=");
		builder.append(zipCode);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String[] args) {
		List<Person> listPsers = Lists.newArrayList(new Person("k", "K", 1), new Person("k", "K", 5),
				new Person("k", "K", 3), new Person("k", "K", 7));

		System.out.println(listPsers);
		Collections.sort(listPsers);
		System.out.println(listPsers);
	}

}
