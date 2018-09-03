package com.liujun.test.curator.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.imps.CreateBuilderImpl;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.data.Stat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;

/**
 * 进行zookeeper的模拟操作
 *
 * @author liujun
 * @version 0.0.1
 * @date 2018/09/03
 */
public class ZookeeperServerTest2 {
  private static CuratorFramework client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    client = Mockito.mock(CuratorFramework.class);


    // Create a mock curator to return mock kafka IP data
    ExistsBuilder existsBuilder = Mockito.mock(ExistsBuilder.class);
    Mockito.when(existsBuilder.forPath("")).thenReturn(new Stat());

    CreateBuilder createBuilder = Mockito.mock(CreateBuilder.class);
    Mockito.when(createBuilder.forPath("/test", "test-data".getBytes())).thenReturn("this");


    GetDataBuilder getDataBuilder = Mockito.mock(GetDataBuilder.class);
    Mockito.when(getDataBuilder.forPath("")).thenReturn("new value".getBytes());

    CuratorFramework curatorFramework = Mockito.mock(CuratorFramework.class);


   Mockito.when(client.create()).thenReturn(createBuilder);
   Mockito.when(client.create().forPath("/test", "test-data".getBytes())).thenReturn("this");

  }

  @AfterClass
  public static void tearDownAfterClass() throws IOException {
    // server.stop();
    client.close();
  }

  @Test
  public void testFoobar() throws Exception {
    System.out.println("client: " + client);
    client.create().forPath("/test", "test-data".getBytes());

    byte[] data = client.getData().forPath("/test");
    System.out.println("data: " + new String(data));
  }

  @Test
  public void testFoobar2() throws Exception {
    System.out.println("client: " + client);
    client.create().forPath("/test", "test-data".getBytes());

    byte[] data = client.getData().forPath("/test");
    System.out.println("data: " + new String(data));
  }
}
