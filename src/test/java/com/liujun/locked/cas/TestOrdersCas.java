package com.liujun.locked.cas;

import com.liujun.locked.threadpool.TaskThreadPool;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * 测试单个应用扣下单扣减库存的场景
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestOrdersCas {

  @Test
  public void useOrder() throws InterruptedException {
    int orderNumSum = 800;
    final Goods goods = new Goods("mac", orderNumSum);

    // 并发进行下单操作
    int maxOrder = 8;

    int count = 0;
    for (int i = 0; i < orderNumSum / maxOrder; i++) {
      CountDownLatch startLatch = new CountDownLatch(maxOrder);
      for (int j = 0; j < maxOrder; j++) {
        TaskThreadPool.INSTANCE.submit(
            () -> {
              startLatch.countDown();

              Orders instance = new Orders(goods);
              instance.createOrder(1);
            });

        count++;
      }
      // 执行等待结果
      try {
        startLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("结束,共运行:" + count + "次");

    TaskThreadPool.INSTANCE.shutdown();

    Thread.sleep(500);

    System.out.println("shutdown status:" + TaskThreadPool.INSTANCE.getPool().isShutdown());

    System.out.println("最后商品的库存:" + goods.getGoods());
    // Assert.assertEquals(0, goods.getGoods());
  }
}
