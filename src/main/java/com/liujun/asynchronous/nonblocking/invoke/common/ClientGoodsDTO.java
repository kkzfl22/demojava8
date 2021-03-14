package com.liujun.asynchronous.nonblocking.invoke.common;

/**
 * 商品信息
 *
 * @author liujun
 * @version 0.0.1
 */
public class ClientGoodsDTO {

  /** 商品的id */
  private String dataId;

  /** 商品的价格 */
  private int goodsPrice;

  /** 商品的描述 */
  private String message;

  public String getDataId() {
    return dataId;
  }

  public void setDataId(String dataId) {
    this.dataId = dataId;
  }

  public int getGoodsPrice() {
    return goodsPrice;
  }

  public void setGoodsPrice(int goodsPrice) {
    this.goodsPrice = goodsPrice;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Goods{");
    sb.append("dataId='").append(dataId).append('\'');
    sb.append(", goodsPrice=").append(goodsPrice);
    sb.append(", message='").append(message).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
