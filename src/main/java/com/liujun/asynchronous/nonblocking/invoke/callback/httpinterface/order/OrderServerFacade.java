package com.liujun.asynchronous.nonblocking.invoke.callback.httpinterface.order;

import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.common.utils.RestTemplateUtils;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientGoodsDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientUserDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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

  /** 用来存储订单的map */
  private Map<String, OrderDTO> userMap = new HashMap<>();

  /** 商品服务 */
  private Map<String, OrderDTO> goodsMap = new HashMap<>();

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @RequestMapping(
          value = "/orderInfo",
          method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody OrderDTO order) {
    logger.info("getUserInfo start {}", order);
    // 获取用户信息的请求发送
    boolean userSender = this.getUserInfo(order.getUserId());
    // 获取商品信息的请求发送
    boolean goodsSender = this.getGoods(order.getGoodId());
    logger.info("getUserInfo  request {} rsponse {} ", order.getUserId(), userSender);
    logger.info("getGoods  request {} rsponse {} ", order.getGoodId(), goodsSender);
    userMap.put(order.getUserId(), order);
    goodsMap.put(order.getGoodId(), order);
    // 构建结果的响应
    return ApiResponse.ok();
  }

  /**
   * 异步的订单的响应
   *
   * @param order 订单信息
   * @return 获取响应信息
   */
  @RequestMapping(
          value = "/getResponse",
          method = {RequestMethod.POST})
  public ApiResponse getResponse(@RequestBody OrderDTO order) {
    logger.info("getResponse start {}", order);
    // 响应结果
    OrderDTO orderRsp = userMap.get(order.getUserId());
    if (null != orderRsp) {
      return ApiResponse.ok(orderRsp);
    }
    // 构建结果的响应
    return ApiResponse.ok();
  }

  /**
   * 用户信息响应的回调接口
   *
   * @param userDto 用户信息
   * @return 响应处理
   */
  @RequestMapping(
          value = "/userCallBack",
          method = {RequestMethod.POST})
  public ApiResponse getUserCallBack(@RequestBody ClientUserDTO userDto) {
    logger.info("/userCallBack  {}", userDto);
    // 设置用户信息
    OrderDTO orderInfo = userMap.get(userDto.getUserId());
    if (null != orderInfo) {
      orderInfo.setUserInfo(userDto);
    }
    return ApiResponse.ok();
  }

  /**
   * 商品服务的回调接口
   *
   * @param goodsInfo 商品信息
   * @return
   */
  @RequestMapping(
          value = "/goodsCallBack",
          method = {RequestMethod.POST})
  public ApiResponse getGoodsCallBack(@RequestBody ClientGoodsDTO goodsInfo) {
    logger.info("/goodscallBack {}", goodsInfo);
    // 设置用户信息
    OrderDTO orderInfo = goodsMap.get(goodsInfo.getDataId());
    if (null != orderInfo) {
      orderInfo.setGoodsInfo(goodsInfo);
    }
    return ApiResponse.ok();
  }

  /**
   * 获取用户的信息
   *
   * @param userId 用户的id
   * @return 用户的信息
   */
  private boolean getUserInfo(String userId) {
    logger.info("request get user info start {} ", userId);
    ClientUserDTO clientUser = new ClientUserDTO();
    clientUser.setUserId(userId);
    HttpHeaders headers = new HttpHeaders();
    // 将对象装入HttpEntity中
    HttpEntity<ClientUserDTO> request = new HttpEntity<>(clientUser, headers);
    ResponseEntity<String> result =
            restTemplate.exchange(
                    "http://localhost:9000/user/getUserInfo", HttpMethod.POST, request, String.class);
    if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
      return true;
    }
    return false;
  }

  /**
   * 获取商品信息
   *
   * @param dataId 商品信息
   * @return 当前的用户的信息
   */
  private boolean getGoods(String dataId) {
    logger.info("request goods start {} ", dataId);
    ClientGoodsDTO clientGoods = new ClientGoodsDTO();
    clientGoods.setDataId(dataId);
    HttpHeaders headers = new HttpHeaders();
    // 将对象装入HttpEntity中
    HttpEntity<ClientGoodsDTO> request = new HttpEntity<>(clientGoods, headers);
    ResponseEntity<String> result =
            restTemplate.postForEntity(
                    "http://localhost:9001/goods/getGoodsInfo", request, String.class);
    if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
      return true;
    }
    return false;
  }
}
