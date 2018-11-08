package com.zookeeper.distributed.lock.zk;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;

/**
 * zk的公共信息
 *
 * @since 2017年6月28日 下午3:22:23
 * @author liujun
 * @version 0.0.1
 */
public class ZKConnection {

  /** 日志操作 */
  private Logger log = Logger.getLogger(ZKConnection.class);

  // static {
  // curatorFramework = createConnection();
  // Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
  // @Override
  // public void run() {
  // if (curatorFramework != null)
  // curatorFramework.close();
  // }
  // }));
  // }

  private ZKConnection() {}

  /** 连接的连接实例 */
  private static final ZKConnection Zkconn = new ZKConnection();

  /**
   * 获取连接实例
   *
   * @return
   */
  public static ZKConnection getInstance() {
    return Zkconn;
  }

  /** 获取连接信息 */
  private CuratorFramework conn;

  public CuratorFramework getConn() {
    // 如果当前未连接，或者连接出现问题，需要重新进行连接
    if (conn == null || !conn.getZookeeperClient().isConnected()) {

      // 获取zk的连接信息
      conn = this.createConnection("");
    }

    return conn;
  }

  public CuratorFramework getConn(String url) {
    // 如果当前未连接，或者连接出现问题，需要重新进行连接
    if (conn == null || !conn.getZookeeperClient().isConnected()) {
      // 获取zk的连接信息
      conn = this.createConnection(url);
    }

    return conn;
  }

  /**
   * 创建zk连接
   *
   * @param connurl zk的地址
   * @return 结果信息
   */
  private CuratorFramework createConnection(String connurl) {

    while (true) {
      CuratorFramework curatorFramework =
          CuratorFrameworkFactory.newClient(connurl, new ExponentialBackoffRetry(100, 6));

      // start connection
      curatorFramework.start();

      // wait 3 second to establish connect
      try {
        curatorFramework.blockUntilConnected(5, TimeUnit.SECONDS);

        if (curatorFramework.getZookeeperClient().isConnected()) {
          return curatorFramework;
        }
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
        ignored.printStackTrace();
      }
      // fail situation
      curatorFramework.close();
      System.out.println(
          " error ,failed to connect to zookeeper service : " + connurl + " curr sleep 30000");
      log.error(" failed to connect to zookeeper service : " + connurl + " curr sleep 30000");
      // 如果连接失败，则休眠30秒后再进行连接
      try {
        Thread.sleep(30000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // @SuppressWarnings({ "resource", "unused" })
  // private void addChildPathCache(String path, PathChildrenCacheListener
  // listener) {
  // ExecutorService executor = Executors.newFixedThreadPool(5);
  //
  // try {
  // // 监听子节点的变化情况
  // final PathChildrenCache childrenCache = new
  // PathChildrenCache(getConnection(), path, true);
  // childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
  // childrenCache.getListenable().addListener(listener, executor);
  // } catch (Exception e) {
  // LOG.error("ZKUtils addChildPathCache Exception:", e);
  // throw new RuntimeException(e);
  // }
  // }
  //
  // public static void main(String[] args) throws Exception {
  //
  // ZKConnection zkInstance = new ZKConnection();
  //
  // CuratorFramework client = zkInstance.getConnection();
  //
  // ExecutorService executor = Executors.newFixedThreadPool(5);
  // // System.out.println(client.getZookeeperClient().isConnected());
  // zkInstance.addChildPathCache(client, ZkCfgEnum.ZK_BASE_PATH.getKey(),
  // true, executor);
  //
  // client.create().inBackground().forPath("/jloader/tcp");
  //
  // Thread.sleep(8000);
  // }
  //
  // @SuppressWarnings("resource")
  // private void addChildPathCache(CuratorFramework client, final String
  // path, final boolean addChild,
  // final ExecutorService executor) throws Exception {
  // // 监听子节点的变化情况
  // final PathChildrenCache childrenCache = new PathChildrenCache(client,
  // path, true);
  // childrenCache.start(PathChildrenCache.StartMode.NORMAL);
  //
  // childrenCache.getListenable().addListener(new PathChildrenCacheListener()
  // {
  // @Override
  // public void childEvent(CuratorFramework client, PathChildrenCacheEvent
  // event) throws Exception {
  // switch (event.getType()) {
  // case CHILD_ADDED:
  // if (addChild) {
  // addChildPathCache(client, event.getData().getPath(), false, executor);
  // }
  // System.out.println("CHILD_ADDED: " + event.getData().getPath());
  // break;
  // case CHILD_REMOVED:
  // System.out.println("CHILD_REMOVED: " + event.getData().getPath());
  // break;
  // case CHILD_UPDATED:
  // System.out.println("CHILD_UPDATED: " + event.getData().getPath());
  // break;
  // default:
  // break;
  // }
  // }
  // }, executor);
  //
  // }

}
