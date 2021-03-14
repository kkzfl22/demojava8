package com.liujun.asynchronous.nonblocking.invoke.threadpool;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.invoke.common.ClientGoodsDTO;
import com.liujun.asynchronous.nonblocking.invoke.common.ClientUserDTO;
import com.liujun.asynchronous.nonblocking.invoke.common.OrderDTO;
import com.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

  private Gson gson = new Gson();

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = this.getRestTemplate();

  @RequestMapping(
      value = "/orderInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody OrderDTO order) {

    logger.info("getUserInfo start {}", order);

    // 用户信息
    Future userRsp = this.asynchronousUser(order.getUserId());
    // 商品信息
    Future goodsRsp = this.asynchronousGoods(order.getGoodId());

    OrderDTO orderRsp = null;
    try {
      orderRsp = this.builderRsp(order, userRsp, goodsRsp);
    } catch (ExecutionException e) {
      logger.error("get userRps : ExecutionException  ", e);
    } catch (InterruptedException e) {
      logger.error("get userRps : InterruptedException  ", e);
    }

    ThreadUtils.sleep(1);

    logger.info("getUserInfo start {} rsp {} orderRsp {}  ", order, orderRsp);

    // 构建结果的响应
    return ApiResponse.ok(orderRsp);
  }

  /**
   * 异步调用用户服务
   *
   * @param goods 商品信息
   * @return 商品的结果
   */
  public Future asynchronousGoods(String goods) {
    Future rspData =
        ScheduleTaskThreadPool.INSTANCE.submit(
            () -> {
              return this.getGoods(goods);
            });

    return rspData;
  }

  /**
   * 异步调用用户
   *
   * @param userId
   * @return
   */
  public Future asynchronousUser(String userId) {
    Future rspData =
        ScheduleTaskThreadPool.INSTANCE.submit(
            () -> {
              return this.getUserInfo(userId);
            });

    return rspData;
  }

  /**
   * 构造响应
   *
   * @param userInfo 用户信息
   * @return 当前响应的用户信息
   */
  private OrderDTO builderRsp(OrderDTO orderInfo, Future userInfo, Future goodsInfo)
      throws ExecutionException, InterruptedException {
    OrderDTO order = new OrderDTO();
    order.setUserId(orderInfo.getUserId());
    order.setUserInfo((ClientUserDTO) userInfo.get());
    order.setGoodId(orderInfo.getGoodId());
    order.setGoodsInfo((ClientGoodsDTO) goodsInfo.get());
    return order;
  }

  /**
   * 获取用户的信息
   *
   * @param userId 用户的id
   * @return 用户的信息
   */
  private ClientUserDTO getUserInfo(String userId) {

    logger.info("request get user info start {} ", userId);

    ClientUserDTO clientUser = new ClientUserDTO();
    clientUser.setUserId(userId);
    HttpHeaders headers = new HttpHeaders();
    // 将对象装入HttpEntity中
    HttpEntity<ClientUserDTO> request = new HttpEntity<>(clientUser, headers);
    ResponseEntity<String> result =
        restTemplate.postForEntity("http://localhost:9000/user/getUserInfo", request, String.class);
    if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
      ApiResponse<ClientUserDTO> data =
          gson.fromJson(result.getBody(), new TypeToken<ApiResponse<ClientUserDTO>>() {}.getType());
      // 如果操作成功，则返回结果
      if (data.getResult()) {
        ClientUserDTO rsp = data.getData();

        logger.info("request get user info start {} rsp {} ", userId, rsp);

        return rsp;
      }
    }
    return null;
  }

  /**
   * 获取商品信息
   *
   * @param dataId 商品信息
   * @return 当前的用户的信息
   */
  private ClientGoodsDTO getGoods(String dataId) {

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
      ApiResponse<ClientGoodsDTO> data =
          gson.fromJson(
              result.getBody(), new TypeToken<ApiResponse<ClientGoodsDTO>>() {}.getType());
      // 如果操作成功，则返回结果
      if (data.getResult()) {
        ClientGoodsDTO goodsRsp = data.getData();
        logger.info("request goods start {} , response {} ", dataId, goodsRsp);
        return goodsRsp;
      }
    }
    return null;
  }

  /**
   * 获取连接管理对象
   *
   * @return
   */
  private RestTemplate getRestTemplate() {
    return new RestTemplate(getClientHttpRequestFactory());
  }

  /**
   * 获取连接处理的工厂信息
   *
   * @return
   */
  private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(25000);
    clientHttpRequestFactory.setReadTimeout(25000);
    return clientHttpRequestFactory;
  }
}
