package com.guava.baseutils.optional.demo2;

import org.junit.Test;

import com.google.common.base.Optional;

public class GetUserinfo {

	
	@Test
	public void oldCode()
	{
		User user = getUserInfo();
		
		if(null != user)
		{
			System.out.println(user.getName());
		}
	}
	
	@Test
	public void optionCode()
	{
		Optional<User> user = Optional.fromNullable(getUserInfo());
	
		System.out.println(user.or(new User("def", 0)).getName());
	}
	
	
	
	public User getUserInfo()
	{
		return null;
	}
//	public User getUserInfo()
//	{
//		return new User("kk", 30);
//	}
	
}
