package com.liujun.asynchronous.nonblocking.invoke.eventbus.order;

import com.google.gson.reflect.TypeToken;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientGoodsDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientUserDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.OrderDTO;
import com.liujun.asynchronous.nonblocking.invoke.eventbus.constants.DataTypeEnum;
import com.rabbitmq.client.Channel;
import com.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件监听器,用于接收mq发送的消息,接收来自rabbitmq的消息数据,进行订单的处理
 *
 * @author liujun
 * @version 0.0.1
 */
@Slf4j
@Service("orderEventProcess")
public class OrderEventProcess implements ChannelAwareMessageListener {

  /** 数据类型的标识信息 */
  public static final String DATA_TYPE = "DATA_TYPE";

  /** 用来用户的map */
  private static final Map<String, OrderDTO> USER_MAP = new ConcurrentHashMap<>();

  /** 用户存储商品的map */
  private static final Map<String, OrderDTO> GOODS_MAP = new ConcurrentHashMap<>();

  @Override
  public void onMessage(Message message, Channel channel) throws Exception {
    // 通过类型确定消息的处理类
    String dataType = String.valueOf(message.getMessageProperties().getHeaders().get(DATA_TYPE));
    // json串信息
    String msgInfo = new String(message.getBody());
    // 回调保存用户
    if (DataTypeEnum.USER.getType().equals(dataType)) {
      this.saveUser(msgInfo);
    }
    // 保存商品信息
    else if (DataTypeEnum.GOODS.getType().equals(dataType)) {
      this.saveGoods(msgInfo);
    }
  }

  /**
   * 构造响应信息
   *
   * @param orderInfo 用户查询请求信息
   * @return 响应结果
   */
  public OrderDTO builderRsp(OrderDTO orderInfo) {
    return USER_MAP.get(orderInfo.getUserId());
  }

  /**
   * 保存用户信息
   *
   * @param data
   */
  private void saveUser(String data) {
    ClientUserDTO userRsp = JsonUtils.fromJson(data, new TypeToken<ClientUserDTO>() {}.getType());
    OrderDTO oderInfo = USER_MAP.get(userRsp.getUserId());
    if (oderInfo != null) {
      oderInfo.setUserInfo(userRsp);
    }
  }

  /**
   * 保存商品信息
   *
   * @param data
   */
  private void saveGoods(String data) {
    ClientGoodsDTO goodsRsp =
        JsonUtils.fromJson(data, new TypeToken<ClientGoodsDTO>() {}.getType());
    OrderDTO orderInfo = GOODS_MAP.get(goodsRsp.getDataId());
    if (orderInfo != null) {
      orderInfo.setGoodsInfo(goodsRsp);
    }
  }

  /**
   * 执行消息的存储操作
   *
   * @param orderInfo
   */
  public static void putOrder(OrderDTO orderInfo) {
    USER_MAP.put(orderInfo.getUserId(), orderInfo);
    GOODS_MAP.put(orderInfo.getGoodId(), orderInfo);
  }
}
