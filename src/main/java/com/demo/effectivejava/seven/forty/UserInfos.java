package com.demo.effectivejava.seven.forty;

public class UserInfos {

	private final Integer userId;

	/**
	 * username
	 */
	private final String userName;

	/**
	 * 年龄
	 */
	private final Integer age;

	/**
	 * 身高
	 */
	private final Integer hight;

	public static class Builder {

		private final int userId;

		private final String userName;

		private int age;

		private int hight;

		public Builder(int userId, String userName) {
			this.userId = userId;
			this.userName = userName;
		}

		public Builder age(int age) {
			this.age = age;
			return this;
		}

		public Builder hight(int hight) {
			this.hight = hight;
			return this;
		}

		public UserInfos build() {
			return new UserInfos(this);
		}
	}

	private UserInfos(Builder builder) {
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.age = builder.age;
		this.hight = builder.hight;
	}

	@Override
	public String toString() {
		StringBuilder builder2 = new StringBuilder();
		builder2.append("UserInfos [userId=");
		builder2.append(userId);
		builder2.append(", userName=");
		builder2.append(userName);
		builder2.append(", age=");
		builder2.append(age);
		builder2.append(", hight=");
		builder2.append(hight);
		builder2.append("]");
		return builder2.toString();
	}

	public static void main(String[] args) {
		UserInfos user = new UserInfos.Builder(1, "kk").age(18).hight(172).build();
		System.out.println(user);
	}
}
