package com.liujun.locked.cas;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 商品服务
 *
 * @author liujun
 * @version 0.0.1
 */
public class Goods {

  /** 商品名称 */
  private String name;

  /** 商品库存数量 添加volatile，保证内存的可见性 */
  private volatile int goodsNum;

  private static final Unsafe UNSAFE = getUnsafe();

  /** 值的偏移量 */
  private static final long valueOffset;

  /** 获取对象操作值的偏移量 */
  static {
    try {
      valueOffset = UNSAFE.objectFieldOffset(Goods.class.getDeclaredField("goodsNum"));
    } catch (Exception ex) {
      throw new Error(ex);
    }
  }

  public Goods(String name, int goodsNum) {
    this.name = name;
    this.goodsNum = goodsNum;
  }

  /**
   * 获取unsafe对象
   *
   * @return
   */
  public static Unsafe getUnsafe() {
    // 通过反射得到theUnsafe对应的Field对象
    Field field;
    try {
      field = Unsafe.class.getDeclaredField("theUnsafe");
      // 设置该Field为可访问
      field.setAccessible(true);
      // 通过Field得到该Field对应的具体对象，传入null是因为该Field为static的
      Unsafe unsafe = (Unsafe) field.get(null);
      return unsafe;
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** 商品的库存扣减操作 */
  public void minusGoods(int num) {
    boolean updRsp;
    do {
      // 读取最新的volatile变量的值
      int goodsNumOld = UNSAFE.getIntVolatile(this, valueOffset);
      // 库存不足时，停止
      if (goodsNumOld - num < 0) {
        break;
      }
      int goodsNumNew = goodsNumOld - num;
      // 使用比较交换的原子操作执行更新
      updRsp = UNSAFE.compareAndSwapInt(this, valueOffset, goodsNumOld, goodsNumNew);
    } while (!updRsp);
  }

  /**
   * 检查是否足够下单
   *
   * @return false，当前商品已经不足已下单 true 当前库存可以可以下单
   */
  public boolean sellOut(int nums) {
    if (goodsNum >= nums) {
      return true;
    }
    return false;
  }

  /**
   * 获取商品数量
   *
   * @return 当前商品的数量
   */
  public int getGoods() {
    return goodsNum;
  }
}
