package com.demo.parttern.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试对象，用户信息
 *
 * @author liujun
 * @version 0.0.1
 */
public class UserInfo {

  /** 用户的id */
  private Integer id;

  /** 用户名 */
  private String userName;

  /** 密码 */
  private String password;

  /** 爱好 */
  private List<String> like = new ArrayList<>();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<String> getLike() {
    return like;
  }

  /**
   * 添加多个爱好
   *
   * @param like 爱好集合
   */
  public void addLikeList(List<String> like) {
    this.like.addAll(like);
  }

  /**
   * 添加两个爱好
   *
   * @param like1 爱好1
   * @param like2 爱好2
   */
  public void addList2(String like1, String like2) {
    this.like.add(like1);
    this.like.add(like2);
  }

  /**
   * 添加三个爱好
   *
   * @param like1 爱好1
   * @param like2 爱好2
   * @param like3 爱好3
   */
  public void addList3(String like1, String like2, String like3) {
    this.like.add(like1);
    this.like.add(like2);
    this.like.add(like3);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("UserInfo{");
    sb.append("id=").append(id);
    sb.append(", userName='").append(userName).append('\'');
    sb.append(", password='").append(password).append('\'');
    sb.append(", like=").append(like);
    sb.append('}');
    return sb.toString();
  }
}
