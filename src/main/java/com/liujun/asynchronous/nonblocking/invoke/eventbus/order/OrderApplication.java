package com.liujun.asynchronous.nonblocking.invoke.eventbus.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单服务的入口
 *
 * @author liujun
 * @version 0.0.1
 */
@SpringBootApplication
public class OrderApplication {

  public static void main(String[] args) {
    String[] argsNew = new String[] {"-- server.port=9010"};
    SpringApplication.run(OrderApplication.class, argsNew);
  }
}
