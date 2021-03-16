package com.liujun.asynchronous.nonblocking.invoke.eventbus.order;

import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientGoodsDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientUserDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.OrderDTO;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.DataTypeEnum;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.RabbitmqConfig;
import com.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟订单服务
 *
 * @author liujun
 * @version 0.0.1
 */
@RestController
@RequestMapping("/order")
public class OrderServerFacade {

  private Logger logger = LoggerFactory.getLogger(OrderServerFacade.class);

  /** 消息的发送接口 */
  @Autowired private RabbitTemplate orderRabbitTemplate;

  /** 事件处理 */
  @Autowired private OrderEventProcess eventProcess;

  @RequestMapping(
      value = "/orderInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody OrderDTO order) {
    logger.info("getUserInfo start {}", order);
    // 保存当前的发送数据
    OrderEventProcess.putOrder(order);
    // 获取用户信息的请求发送
    boolean userSender = this.senderQueryUser(order.getUserId());
    // 获取商品信息的请求发送
    boolean goodsSender = this.senderQueryGoods(order.getGoodId());
    logger.info("getUserInfo  request {} rsponse {} ", order.getUserId(), userSender);
    logger.info("getGoods  request {} rsponse {} ", order.getGoodId(), goodsSender);

    // 构建结果的响应
    return ApiResponse.ok();
  }

  @RequestMapping(
      value = "/getOrderResponse",
      method = {RequestMethod.POST})
  public ApiResponse getOrderInfo(@RequestBody OrderDTO order) {
    logger.info("getOrderResponse start {}", order);
    // 获取用户信息的请求发送
    OrderDTO userQueryRsp = eventProcess.builderRsp(order);
    logger.info("getOrderResponse  request {} rsponse {} ", order.getUserId(), userQueryRsp);

    // 构建结果的响应
    return ApiResponse.ok(userQueryRsp);
  }

  /**
   * 获取用户的信息
   *
   * @param userId 用户的id
   * @return 用户的信息
   */
  private boolean senderQueryUser(String userId) {
    logger.info("request get user info start {} ", userId);
    ClientUserDTO clientUser = new ClientUserDTO();
    clientUser.setUserId(userId);

    // 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行
    orderRabbitTemplate.convertAndSend(
        RabbitmqConfig.getUserReqExchangeName(),
        RabbitmqConfig.getUserReqQueueName(),
        // 数据的格式为json
        JsonUtils.toJson(clientUser),
        msg -> {
          msg.getMessageProperties()
              .getHeaders()
              .put(RabbitmqConfig.getHeadType(), DataTypeEnum.USER.getType());
          return msg;
        });

    return true;
  }

  /**
   * 获取商品信息
   *
   * @param dataId 商品信息
   * @return 当前的用户的信息
   */
  private boolean senderQueryGoods(String dataId) {
    logger.info("request goods start {} ", dataId);
    ClientGoodsDTO clientGoods = new ClientGoodsDTO();
    clientGoods.setDataId(dataId);

    // 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行
    orderRabbitTemplate.convertAndSend(
        RabbitmqConfig.getGoodsReqExchangeName(),
        RabbitmqConfig.getGoodsReqQueueName(),
        // 数据的格式为json
        JsonUtils.toJson(clientGoods),
        msg -> {
          msg.getMessageProperties()
              .getHeaders()
              .put(RabbitmqConfig.getHeadType(), DataTypeEnum.GOODS.getType());
          return msg;
        });

    return true;
  }
}
