package com.liujun.asynchronous.nonblocking.invoke.eventbus.goods;

import com.google.gson.reflect.TypeToken;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.DataTypeEnum;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.RabbitmqConfig;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.user.UserDTO;
import com.rabbitmq.client.Channel;
import com.utils.JsonUtils;
import com.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事件监听器,用于接收事件并处理,用作商品的处理操作
 *
 * @author liujun
 * @version 0.0.1
 */
@Slf4j
@Service("goodsEventProcess")
public class GoodsEventProcess implements ChannelAwareMessageListener {

  /** 数据类型的标识信息 */
  public static final String DATA_TYPE = "DATA_TYPE";

  /** 消息的发送接口 */
  @Autowired private RabbitTemplate amqpTemplate;

  @Override
  public void onMessage(Message message, Channel channel) throws Exception {
    // 通过类型确定消息的处理类
    String dataType = String.valueOf(message.getMessageProperties().getHeaders().get(DATA_TYPE));
    // json串信息
    String msgInfo = new String(message.getBody());
    // 当数据接收完毕后，则进行回复消息
    if (DataTypeEnum.GOODS.getType().equals(dataType)) {
      GoodsDTO userData = this.receiveData(msgInfo);
      ThreadUtils.sleep(10);
      // 用户消息的发送
      this.sendData(userData, DataTypeEnum.GOODS.getType());
    }
  }

  /**
   * 保存用户信息
   *
   * @param data
   */
  private GoodsDTO receiveData(String data) {
    GoodsDTO goods = JsonUtils.fromJson(data, new TypeToken<GoodsDTO>() {}.getType());
    GoodsDTO rsp = new GoodsDTO();
    rsp.setDataId(goods.getDataId());
    goods.setGoodsPrice(1024);
    goods.setMessage("这是一个苹果,apple,还被咬了一口");
    return goods;
  }

  /**
   * 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行,仅测试使用，
   *
   * <p>线上环境一般需要使用确认机制。
   *
   * @param message 发送的消息
   * @param dataType 数据类型信息
   */
  private void sendData(GoodsDTO message, String dataType) {
    // 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行
    amqpTemplate.convertAndSend(
        RabbitmqConfig.getRspExchangeName(),
        RabbitmqConfig.getRspQueueName(),
        // 数据的格式为json
        JsonUtils.toJson(message),
        msg -> {
          msg.getMessageProperties().getHeaders().put(RabbitmqConfig.getHeadType(), dataType);
          return msg;
        });
  }
}
