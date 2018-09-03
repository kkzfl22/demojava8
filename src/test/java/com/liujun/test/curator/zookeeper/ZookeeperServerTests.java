package com.liujun.test.curator.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * 进行zookeeper的模拟操作
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/03
 */
public class ZookeeperServerTests {
  private static TestingServer server;
  private static CuratorFramework client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    server = new TestingServer(2181, true);
    server.start();

    client = CuratorFrameworkFactory.newClient("localhost", new ExponentialBackoffRetry(1000, 3));
    client.start();
  }

  @AfterClass
  public static void tearDownAfterClass() throws IOException {
    server.stop();
    client.close();
  }

  @Test
  public void testFoobar() throws Exception {
    System.out.println("client: " + client);
    client.create().forPath("/test", "test-data".getBytes());

    byte[] data = client.getData().forPath("/test");
    System.out.println("data: " + new String(data));
  }
}
