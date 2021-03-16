package com.liujun.asynchronous.nonblocking.invoke.eventbus.constants;

/**
 * 消息中间件的连接配制
 *
 * @author liujun
 * @version 0.0.1
 */
public class RabbitmqConfig {

  /** mq服务器所在的服务器 */
  private static final String HOST = "127.0.0.1";

  /** mq服务器的ip */
  private static final int PORT = 5672;

  /** 连接mq的用户名 */
  private static final String USER_NAME = "liujun";

  /** 连接mq的密码 */
  private static final String USER_PASSWORD = "liujun";

  /** 虚拟host */
  private static final String VIRTUAL_HOST = "/virtualhost_lj";

  /** 用户请求交换机名称 */
  private static final String USER_REQ_EXCHANGE_NAME = "user-exchange-req";

  /** 用户请求队列名称 */
  private static final String USER_REQ_QUEUE_NAME = "user-queue-req";

  /** 用户响应交换机名称 */
  private static final String RSP_EXCHANGE_NAME = "exchange-rsp";

  /** 用户响应队列名称 */
  private static final String RSP_QUEUE_NAME = "queue-rsp";

  /** 商品服务请求的交换机 */
  private static final String GOODS_REQ_EXCHANGE_NAME = "goods-exchange-req";

  /** 商品服务请求队列的交换机 */
  private static final String GOODS_REQ_QUEUE_NAME = "goods-queue-req";



  /** 数据在类型信息标识 */
  private static final String HEAD_TYPE = "DATA_TYPE";

  public static String getHost() {
    return HOST;
  }

  public static int getPort() {
    return PORT;
  }

  public static String getUserName() {
    return USER_NAME;
  }

  public static String getUserPassword() {
    return USER_PASSWORD;
  }

  public static String getVirtualHost() {
    return VIRTUAL_HOST;
  }

  public static String getUserReqExchangeName() {
    return USER_REQ_EXCHANGE_NAME;
  }

  public static String getUserReqQueueName() {
    return USER_REQ_QUEUE_NAME;
  }

  public static String getRspExchangeName() {
    return RSP_EXCHANGE_NAME;
  }

  public static String getRspQueueName() {
    return RSP_QUEUE_NAME;
  }

  public static String getGoodsReqExchangeName() {
    return GOODS_REQ_EXCHANGE_NAME;
  }

  public static String getGoodsReqQueueName() {
    return GOODS_REQ_QUEUE_NAME;
  }

  public static String getHeadType() {
    return HEAD_TYPE;
  }


}
