package com.liujun.asynchronous.nonblocking.invoke.bean;

/**
 * 订单相关的信息
 *
 * @author liujun
 * @version 0.0.1
 */
public class OrderDTO {

  /** 用户的id */
  private String userId;

  /** 用户的信息 */
  private ClientUserDTO userInfo;

  /** 商品信息 */
  private String goodId;

  /** 商品信息 */
  private ClientGoodsDTO goodsInfo;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public ClientUserDTO getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(ClientUserDTO userInfo) {
    this.userInfo = userInfo;
  }

  public String getGoodId() {
    return goodId;
  }

  public void setGoodId(String goodId) {
    this.goodId = goodId;
  }

  public ClientGoodsDTO getGoodsInfo() {
    return goodsInfo;
  }

  public void setGoodsInfo(ClientGoodsDTO goodsInfo) {
    this.goodsInfo = goodsInfo;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("OrderDTO{");
    sb.append("userId='").append(userId).append('\'');
    sb.append(", userInfo=").append(userInfo);
    sb.append(", goodId='").append(goodId).append('\'');
    sb.append(", goodsInfo=").append(goodsInfo);
    sb.append('}');
    return sb.toString();
  }
}
