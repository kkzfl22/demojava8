package com.liujun.asynchronous.nonblocking.invoke.synchronous;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientGoodsDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.ClientUserDTO;
import com.liujun.asynchronous.nonblocking.invoke.bean.OrderDTO;
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
    ClientUserDTO userRsp = this.getUserInfo(order.getUserId());
    // 获取商品信息
    ClientGoodsDTO clientGoodRsp = this.getGoods(order.getGoodId());
    ThreadUtils.sleep(1);

    OrderDTO orderRsp = this.builderRsp(userRsp, clientGoodRsp);

    logger.info("getUserInfo start {} rsp {} orderRsp {}  ", order, orderRsp);

    // 构建结果的响应
    return ApiResponse.ok(orderRsp);
  }

  /**
   * 构造响应
   *
   * @param userInfo 用户信息
   * @return 当前响应的用户信息
   */
  private OrderDTO builderRsp(ClientUserDTO userInfo, ClientGoodsDTO goodsInfo) {
    OrderDTO order = new OrderDTO();
    order.setUserId(userInfo.getUserId());
    order.setUserInfo(userInfo);
    order.setGoodId(goodsInfo.getDataId());
    order.setGoodsInfo(goodsInfo);
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
