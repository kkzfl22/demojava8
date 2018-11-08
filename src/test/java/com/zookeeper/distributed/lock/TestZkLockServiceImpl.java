package com.zookeeper.distributed.lock;

import com.zookeeper.distributed.lock.bean.LockRspBean;
import com.zookeeper.distributed.lock.lock.ZkLockServiceImpl;

import java.util.concurrent.CountDownLatch;

public class TestZkLockServiceImpl {

  private static final int controlSize = 10;

  /** 进行并发同时开始遍的栅栏 */
  private static CountDownLatch count = new CountDownLatch(controlSize);

  public void lockTest() {
    new Thread(
            () -> {
              ZkLockServiceImpl zklock = new ZkLockServiceImpl("192.168.99.100:2181");

              try {
                count.countDown();
              } catch (Exception e) {
                e.printStackTrace();
              }
              LockRspBean lockRsp = zklock.lock("datalake/test/DistributedQueue/test", "tcp");
              zklock.unlock(lockRsp);
              lockRsp = zklock.lock("datalake/test/DistributedQueue/test", "tcp");
              zklock.unlock(lockRsp);
              lockRsp = zklock.lock("datalake/test/DistributedQueue/test", "tcp");
              zklock.unlock(lockRsp);
              lockRsp = zklock.lock("datalake/test/DistributedQueue/test", "tcp");
              zklock.unlock(lockRsp);
              lockRsp = zklock.lock("datalake/test/DistributedQueue/test", "tcp");
              zklock.unlock(lockRsp);
              System.out.println("结束....");
				zklock.shutdown();
            })
        .start();
  }

  public static void main(String[] args) {
    TestZkLockServiceImpl lockTest = new TestZkLockServiceImpl();

    for (int i = 0; i < controlSize; i++) {
      lockTest.lockTest();
    }

    count.countDown();
  }
}
