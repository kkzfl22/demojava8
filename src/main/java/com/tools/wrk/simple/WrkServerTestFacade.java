package com.tools.wrk.simple;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * 混用线程池程序
 *
 * @author liujun
 * @version 0.0.1
 */
@RestController
@RequestMapping("/demo")
public class WrkServerTestFacade {

  private Random rand = new Random();

  @RequestMapping(
      value = "/get",
      method = {RequestMethod.GET})
  public int getData() {
    return rand.nextInt();
  }

  boolean first = false;

  @RequestMapping(
      value = "/post",
      method = {RequestMethod.POST})
  public int getDataPost(@RequestBody PostData data) {

    if (!first) {
      System.out.println(data);
      first = true;
    }

    return rand.nextInt();
  }

  public static final String SESSION_ID = "SESSION_ID";

  /**
   * 执行登录操作
   *
   * @param data
   * @param request
   * @return
   */
  @RequestMapping(
      value = "/login",
      method = {RequestMethod.POST})
  public int login(@RequestBody PostData data, HttpServletRequest request) {
    PostData userDat = (PostData) request.getSession().getAttribute(SESSION_ID);
    if (userDat == null) {
      request.getSession().setAttribute(SESSION_ID, data);
    }
    return 1;
  }

  /**
   * 执行登录操作
   *
   * @param request
   * @return
   */
  @RequestMapping(
      value = "/getUser",
      method = {RequestMethod.GET})
  public String getUser(HttpServletRequest request) {
    PostData userDat = (PostData) request.getSession().getAttribute(SESSION_ID);
    if (null == userDat) {
      return "this is null";
    }

    return userDat.toString();
  }
}

class PostData {
  private Integer data;
  private String name;

  public Integer getData() {
    return data;
  }

  public void setData(Integer data) {
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PostData{");
    sb.append("data=").append(data);
    sb.append(", name='").append(name).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
