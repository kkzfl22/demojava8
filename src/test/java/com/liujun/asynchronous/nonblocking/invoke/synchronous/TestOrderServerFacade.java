package com.liujun.asynchronous.nonblocking.invoke.synchronous;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.liujun.asynchronous.nonblocking.common.APICodeEnum;
import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.common.goods.GoodsDTO;
import com.liujun.asynchronous.nonblocking.common.user.UserApplication;
import com.liujun.asynchronous.nonblocking.common.user.UserDTO;
import com.liujun.asynchronous.nonblocking.invoke.common.OrderDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 订单服务查询
 *
 * @author liujun
 * @version 0.0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {OrderApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestOrderServerFacade {

  @Autowired private Gson gson;

  @Autowired protected TestRestTemplate restTemplate;

  @Test
  public void testOrder() {
    OrderDTO orderInfo = new OrderDTO();
    orderInfo.setGoodId("2001");
    orderInfo.setUserId("1001");
    // 将对象装入HttpEntity中
    HttpEntity<OrderDTO> request = new HttpEntity<>(orderInfo);
    ResponseEntity<String> result =
        restTemplate.postForEntity("/order/orderInfo", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    ApiResponse<OrderDTO> data =
        gson.fromJson(result.getBody(), new TypeToken<ApiResponse<OrderDTO>>() {}.getType());
    System.out.println(data);
    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
    Assert.assertNotNull(data.getData().getGoodsInfo());
    Assert.assertNotNull(data.getData().getUserInfo());
  }
}
