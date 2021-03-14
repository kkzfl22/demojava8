package com.liujun.asynchronous.nonblocking.common.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot入口
 *
 * @author liujun
 * @version 0.0.1
 */
@SpringBootApplication
public class UserApplication {

  public static void main(String[] args) {
    String[] argsNew = new String[] {"-- server.port=9000"};
    SpringApplication.run(UserApplication.class, argsNew);
  }
}
