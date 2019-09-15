package com.liujun.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/08/12
 */
public class TestJedis {

  @Test
  public void testInfo() {

    Jedis jedis = new Jedis("localhost", 6379);

    System.out.println(jedis.info());
    System.out.println("-------------------");
    System.out.println(jedis.info("used_cpu_sys_children"));
  }
}
