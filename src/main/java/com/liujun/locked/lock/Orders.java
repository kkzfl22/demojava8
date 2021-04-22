package com.liujun.locked.lock;

/**
 * 订单服务
 *
 * <p>单体应用
 *
 * <p>以一个下单场景为例，需要扣减库存
 *
 * @author liujun
 * @version 0.0.1
 */
public class Orders {

  /** 商品服务 */
  private Goods goods;

  public Orders(Goods goods) {
    this.goods = goods;
  }

  /**
   * 创建订单
   *
   * @return
   */
  public boolean createOrder(int num) {

    // 执行商品的减库存操作
    if (goods.sellOut(num)) {
      // 执行扣减库存操作
      goods.minusGoods(num);
      return true;
    }
    return false;
  }

  public int getGoods() {
    return goods.getGoods();
  }
}
