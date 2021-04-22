package com.liujun.locked.base;

/**
 * 商品服务
 *
 * @author liujun
 * @version 0.0.1
 */
public class Goods {

  /** 商品名称 */
  private String name;

  /** 商品库存数量 */
  private int goodsNum;

  public Goods(String name, int goodsNum) {
    this.name = name;
    this.goodsNum = goodsNum;
  }

  /** 商品的库存扣减操作 */
  public void minusGoods(int num) {
    goodsNum -= num;
  }

  /**
   * 检查是否足够下单
   *
   * @return false，当前商品已经不足已下单 true 当前库在意可以下单
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
