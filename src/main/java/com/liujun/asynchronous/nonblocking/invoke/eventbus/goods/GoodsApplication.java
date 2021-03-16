package com.liujun.asynchronous.nonblocking.invoke.eventbus.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot入口
 *
 * @author liujun
 * @version 0.0.1
 */
@SpringBootApplication
public class GoodsApplication {

  public static void main(String[] args) {
    String[] argsNew = new String[] {"-- server.port=9001"};
    SpringApplication.run(GoodsApplication.class, argsNew);
  }
}
