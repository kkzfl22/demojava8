package com.liujun.network.zerocopy.zeroCopy;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 测试普通的socket服务
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/09/15
 */
public class TestZeroSocket {

  private static final String BASE_PATH = "D:\\java\\test\\randomdata\\";

  private static Thread thread;

  @BeforeClass
  public static void startServer() {
    thread =
        new Thread(
            () -> {
              JavaZeroSocketServer.INSTACE.startServer();
              JavaZeroSocketServer.INSTACE.serverRecivice();
            });
    thread.start();
  }

  @Test
  public void runtransfer() {
    this.runMethod("bigdata_4M.data");
    this.runMethod("bigdata_1M.data");
    this.runMethod("bigdata_2M.data");
    this.runMethod("bigdata_4M.data");
    this.runMethod("bigdata_6M.data");
    this.runMethod("bigdata_8M.data");
    this.runMethod("bigdata_16M.data");
    this.runMethod("bigdata_32M.data");
    this.runMethod("bigdata_64M.data");
    this.runMethod("bigdata_256M.data");
    this.runMethod("bigdata_512M.data");
    this.runMethod("bigdata_1G.data");
    this.runMethod("bigdata_2G.data");
    this.runMethod("bigdata_16M.data");
  }

  @Test
  public void runtransferOne() {
    this.runMethod("bigdata_256M.data");
  }

  private void runMethod(String name) {
    long startTime = System.currentTimeMillis();
    JavaZeroSocketClient.INSTANCE.socketClient(BASE_PATH + name);
    long endTime = System.currentTimeMillis();
    System.out.println(name + "\t" + (endTime - startTime));
  }

  @AfterClass
  public static void stopServer() {
    System.out.println("关闭");

    JavaZeroSocketServer.INSTACE.stopServer();
  }
}
