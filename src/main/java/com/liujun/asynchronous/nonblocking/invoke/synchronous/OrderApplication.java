package com.liujun.asynchronous.nonblocking.invoke.synchronous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

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
