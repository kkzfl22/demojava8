package com.liujun.asynchronous.nonblocking.invoke.eventbus.user;

import com.google.gson.reflect.TypeToken;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.DataTypeEnum;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import com.utils.JsonUtils;
import com.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事件监听器,用于接收事件并处理,用作用户的处理
 *
 * @author liujun
 * @version 0.0.1
 */
@Service("userEventProcess")
public class UserEventProcess implements ChannelAwareMessageListener {

  /** 日志 */
  private Logger logger = LoggerFactory.getLogger(UserEventProcess.class);

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
    if (DataTypeEnum.USER.getType().equals(dataType)) {
      UserDTO userData = this.receiveData(msgInfo);
      ThreadUtils.sleep(5);

      logger.info("user event process  request {} response {}",msgInfo,userData);

      // 用户消息的发送
      this.sendData(userData, DataTypeEnum.USER.getType());
    }
  }

  /**
   * 保存用户信息
   *
   * @param data
   */
  private UserDTO receiveData(String data) {
    UserDTO userRsp = JsonUtils.fromJson(data, new TypeToken<UserDTO>() {}.getType());
    UserDTO rsp = new UserDTO();
    rsp.setUserId(userRsp.getUserId());
    rsp.setName("bug_null");
    rsp.setAddress("this is shanghai");

    return rsp;
  }

  /**
   * 使用 convertAndSend 方法时的结果：输出时没有顺序，不需要等待，直接运行,仅测试使用，
   *
   * <p>线上环境一般需要使用确认机制。
   *
   * @param message 发送的消息
   * @param dataType 数据类型信息
   */
  private void sendData(UserDTO message, String dataType) {
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
