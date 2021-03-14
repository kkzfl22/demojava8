package com.liujun.asynchronous.nonblocking.common.goods;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.liujun.asynchronous.nonblocking.common.APICodeEnum;
import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.common.user.UserApplication;
import com.liujun.asynchronous.nonblocking.common.user.UserDTO;
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
    classes = {GoodsApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestGoodsDTOService {

  @Autowired private Gson gson;

  @Autowired protected TestRestTemplate restTemplate;

  @Test
  public void testGoods() {
    GoodsDTO userRequest = new GoodsDTO();
    userRequest.setDataId("2001");
    // 将对象装入HttpEntity中
    HttpEntity<GoodsDTO> request = new HttpEntity<>(userRequest);
    ResponseEntity<String> result =
        restTemplate.postForEntity("/goods/getGoodsInfo", request, String.class);
    Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    ApiResponse<GoodsDTO> data =
        gson.fromJson(result.getBody(), new TypeToken<ApiResponse<GoodsDTO>>() {}.getType());
    System.out.println(data);
    Assert.assertEquals(data.getResult(), Boolean.TRUE);
    Assert.assertEquals(data.getCode(), APICodeEnum.SUCCESS.getErrorData().getCode());
  }
}
