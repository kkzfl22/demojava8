package com.jmockit.base.mockedandInjectable;

/**
 * 订单服务类
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public class OrderService {

  /** 邮件服务类，用于向某用户发邮件。 */
  private MailService mailService;
  /** 用户身份校验类，用于校验某个用户是不是合法用户 */
  private UserCheckService userCheckService;

  /**
   * 构造函数
   *
   * @param mailService 邮件服务
   */
  public OrderService(MailService mailService) {
    this.mailService = mailService;
  }

  /**
   * 下订单
   *
   * @param buyerId 买家ID
   * @param itemId 商品id
   * @return 返回 下订单是否成功
   */
  public boolean submitOrder(long buyerId, long itemId) {
    // 先校验用户身份
    if (!userCheckService.check(buyerId)) {
      // 用户身份不合法
      return false;
    }
    // 下单逻辑代码，
    // 省略...
    // 下单完成，给买家发邮件
    if (!this.mailService.sendMail(buyerId, "下单成功")) {
      // 邮件发送成功
      return false;
    }
    return true;
  }
}
