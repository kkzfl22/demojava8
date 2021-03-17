package com.liujun.asynchronous.nonblocking.invoke.eventbus;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.liujun.asynchronous.nonblocking.common.APICodeEnum;
import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.common.utils.RestTemplateUtils;
import com.liujun.asynchronous.nonblocking.invoke.bean.OrderDTO;
import com.utils.ThreadUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 订单服务查询
 *
 * @author liujun
 * @version 0.0.1
 */
public class TestOrderServerFacade {

  /** json转换对象 */
  private Gson gson = new Gson();

  /** 发送对象 */
  protected RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @Test
  public void testOrder() {
    sendRequest();
    // 每1秒获取一次结果
    for (int i = 0; i < 11; i++) {
      checkResponse(i);
      System.out.println();
      ThreadUtils.sleep(1);
    }
  }

  private void sendRequest() {
    OrderDTO orderInfo = new OrderDTO();
    orderInfo.setGoodId("2001");
    orderInfo.setUserId("1001");
    // 将对象装入HttpEntity中
    HttpEntity<OrderDTO> request = new HttpEntity<>(orderInfo);
    ResponseEntity<String> result =
        restTemplate.postForEntity("http://localhost:9010/order/orderInfo", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    ApiResponse data = gson.fromJson(result.getBody(), new TypeToken<ApiResponse>() {}.getType());
    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
  }

  private void checkResponse(int index) {
    OrderDTO orderInfo = new OrderDTO();
    orderInfo.setGoodId("2001");
    orderInfo.setUserId("1001");
    // 将对象装入HttpEntity中
    HttpEntity<OrderDTO> request = new HttpEntity<>(orderInfo);
    ResponseEntity<String> result =
        restTemplate.postForEntity(
            "http://localhost:9010/order/getResponse", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    ApiResponse<OrderDTO> data =
        gson.fromJson(result.getBody(), new TypeToken<ApiResponse<OrderDTO>>() {}.getType());
    System.out.println(data);
    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
    if (index > 5) {
      Assert.assertNotNull(data.getData().getUserInfo());
    }
    if (index > 10) {
      Assert.assertNotNull(data.getData().getGoodsInfo());
    }
  }
}
