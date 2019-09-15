package com.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujun
 * @version 0.0.1
 * @date
 */
public class Main {

  public static void main(String[] args) {
    ConcurrentHashMap<String, String> value = new ConcurrentHashMap<>();

    String value22 = value.putIfAbsent("11", "11");
    System.out.println(value22);
  }
}
