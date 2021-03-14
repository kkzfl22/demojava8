package com.liujun.asynchronous.nonblocking.common.user;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.liujun.asynchronous.nonblocking.common.APICodeEnum;
import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.common.goods.GoodsDTO;
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
 * 测试首页的相关方法
 *
 * @author liujun
 * @version 0.0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {UserApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestUserService {

  @Autowired private Gson gson;

  @Autowired protected TestRestTemplate restTemplate;

  @Test
  public void testUser() {
    GoodsDTO goodsRequest = new GoodsDTO();

    goodsRequest.setDataId("2001");

    // 将对象装入HttpEntity中
    HttpEntity<GoodsDTO> request = new HttpEntity<>(goodsRequest);

    ResponseEntity<String> result =
        restTemplate.postForEntity("/goods/getOrderInfo", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());

    ApiResponse<UserDTO> data =
        gson.fromJson(result.getBody(), new TypeToken<ApiResponse<UserDTO>>() {}.getType());

    System.out.println(data);

    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
  }
}
