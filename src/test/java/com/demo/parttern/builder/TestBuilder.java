package com.demo.parttern.builder;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 测试构建器对象
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestBuilder {

  /** 正常的构建器测试 */
  @Test
  public void normal() {

    List<String> likes = Arrays.asList("java", "C++", "看书");

    UserInfo userInfo =
        Builder.of(UserInfo::new)
            .with(UserInfo::setId, 1)
            .with(UserInfo::setUserName, "liujun")
            .with(UserInfo::setPassword, "password")
            .with(UserInfo::addList2, "跑步", "打球")
            .with(UserInfo::addList3, "写代码1", "写代码2", "写代码3")
            .with(UserInfo::addLikeList, likes)
            .builder();

    System.out.println(userInfo);
  }
}
