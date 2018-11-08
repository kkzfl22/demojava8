package com.zookeeper.distributed.lock.lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.zookeeper.distributed.lock.bean.LockRspBean;
import com.zookeeper.distributed.lock.console.ZkCfgEnum;
import com.zookeeper.distributed.lock.zk.ZKConnection;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 基于zookeeper的分布式加锁服务
 *
 * @since 2017年6月28日 下午4:05:02
 * @author liujun
 * @version 0.0.1
 */
public class ZkLockServiceImpl {

  /** 日志 */
  private Logger log = Logger.getLogger(ZkLockServiceImpl.class);

  /** 最大等待时间 */
  private static final int MAX_WAT_TIME = 24;

  /** 进行锁等待操作 */
  private final CyclicBarrier runWatchEvent = new CyclicBarrier(2);

  /** 线程池对象 */
  private ExecutorService executor = Executors.newFixedThreadPool(1);

  public ZkLockServiceImpl() {
    // 初始化连接
    this.getZkConn();
  }

  public ZkLockServiceImpl(String url) {
    // 初始化连接
    ZKConnection.getInstance().getConn(url);
  }

  public void shutdown() {
    executor.shutdown();
  }

  /**
   * 获取连接实例的方法
   *
   * @return zk的连接信息
   */
  private CuratorFramework getZkConn() {

    return ZKConnection.getInstance().getConn();
  }

  /**
   * 进行加锁服务，如果在第一次加锁尝试试败，则等待前一个节点完成之后，再进行操作
   *
   * @param baseNode 基础节点
   * @param lockNode 加锁的节点信息
   */
  public LockRspBean lock(String baseNode, String lockNode) {

    LockRspBean rspNode = tryLock(baseNode, lockNode);

    if (log.isDebugEnabled()) {
      log.debug("ZkLockServiceImpl lock rsp:" + rspNode);
    }

    // 如果加锁失败，则等待唤醒
    if (null != rspNode && !rspNode.isRsp()) {
      this.waitForNode(baseNode, lockNode, rspNode);
    }

    try {
      Thread.sleep(300L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return rspNode;
  }

  /**
   * 进行等待节点完成方法,则zk进行通知操作
   *
   * @param baseNode 基础节点
   * @param nodePath 加锁的节点
   */
  @SuppressWarnings("resource")
  public void waitForNode(String baseNode, String nodePath, LockRspBean lockBean) {

    try {
      // 最基础的节点
      String baseNodePath =
          ZkCfgEnum.ZK_PATH_FLAG.getKey() + baseNode + ZkCfgEnum.ZK_PATH_FLAG.getKey() + nodePath;

      if (log.isDebugEnabled()) {
        log.debug(
            "ZkLockServiceImpl waitForNode regist notifly path:"
                + nodePath
                + ",currPath:"
                + lockBean.getLockPath());
      }

      // 注册watach通知事件
      TreeCache nodeCache = new TreeCache(getZkConn(), lockBean.getWaitPath());
      nodeCache.start();

      nodeCache
          .getListenable()
          .addListener(
              new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, TreeCacheEvent event)
                    throws Exception {
                  switch (event.getType()) {
                    case NODE_REMOVED:
                      // 获取节点下所有节点
                      List<String> list = getZkConn().getChildren().forPath(baseNodePath);

                      // 对节点进行排序
                      Collections.sort(list);

                      // 如果当前节点最小，则返回 成功
                      if (lockBean
                          .getLockPath()
                          .equals(baseNodePath + ZkCfgEnum.ZK_PATH_FLAG.getKey() + list.get(0))) {
                        if (log.isDebugEnabled()) {
                          log.debug(
                              "ZkLockServiceImpl waitForNode zk notifly node:"
                                  + event.getData().getPath());
                        }

                        try {
                          // 将对正在等待的加锁进行唤醒，
                          runWatchEvent.await();
                        } catch (InterruptedException e) {
                          e.printStackTrace();
                        } catch (BrokenBarrierException e) {
                          e.printStackTrace();
                        } finally {
                          runWatchEvent.reset();
                        }
                      }
                      break;
                    default:
                      break;
                  }
                }
              },
              executor);

      Stat stat = getZkConn().checkExists().forPath(lockBean.getWaitPath());

      if (stat != null) {
        // 进行等待，由通知机制进行唤醒,最长等待24小时
        runWatchEvent.await(MAX_WAT_TIME, TimeUnit.HOURS);
      }

    } catch (Exception e) {
      e.printStackTrace();
      log.error("ZkLockServiceImpl waitForNode Exception", e);
    }
  }

  public LockRspBean tryLock(String baseNodePath, String lockNode) {
    // 组装最基本的路径
    String baseNode =
        ZkCfgEnum.ZK_PATH_FLAG.getKey() + baseNodePath + ZkCfgEnum.ZK_PATH_FLAG.getKey() + lockNode;

    // 获取zk的连接信息
    String path = ZKPaths.makePath(baseNode, lockNode);

    try {
      // 进行节点的创建操作
      String lockCurrPath =
          getZkConn().create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, new byte[0]);

      // 获取节点下所有节点
      List<String> list = getZkConn().getChildren().forPath(baseNode);

      // 对节点进行排序
      Collections.sort(list);

      // 如果当前节点最小，则返回 成功
      if (lockCurrPath.equals(baseNode + ZkCfgEnum.ZK_PATH_FLAG.getKey() + list.get(0))) {
        // 封装加锁成功信息
        return new LockRspBean(true, lockCurrPath);
      }
      // 找比自己节点小的节点
      else {
        // 如果不是最小的节点，找到比自己小1的节点
        String subMyZnode =
            lockCurrPath.substring(lockCurrPath.lastIndexOf(ZkCfgEnum.ZK_PATH_FLAG.getKey()) + 1);
        String waitNode =
            baseNode
                + ZkCfgEnum.ZK_PATH_FLAG.getKey()
                + list.get(Collections.binarySearch(list, subMyZnode) - 1);

        // 失败，则封装等待的节点
        return new LockRspBean(false, lockCurrPath, waitNode);
      }

    } catch (Exception e) {
      e.printStackTrace();
      log.error("ZkLockServiceImpl tryLock Exception", e);
    }

    return null;
  }

  public void unlock(LockRspBean lockBean) {
    if (null != lockBean) {
      try {
        getZkConn().delete().forPath(lockBean.getLockPath());
        if (log.isDebugEnabled()) {
          log.debug("ZkLockServiceImpl unlock success path:" + lockBean.getLockPath());
          System.out.println("ZkLockServiceImpl unlock success path:" + lockBean.getLockPath());
        }
      } catch (Exception e) {
        e.printStackTrace();
        log.error("ZkLockServiceImpl unlock Exception", e);
      }
    }
  }

  public void colose() {

    if (log.isDebugEnabled()) {
      log.debug("ZkLockServiceImpl colose ");
    }

    getZkConn().close();
  }
}
