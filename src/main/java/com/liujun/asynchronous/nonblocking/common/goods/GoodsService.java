package com.liujun.asynchronous.nonblocking.common.goods;

import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.utils.ThreadUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模拟商品服务
 *
 * @author liujun
 * @version 0.0.1
 */
@RestController
@RequestMapping("/goods")
public class GoodsService {

  /** 商品的id */
  public static final String GOODS_ID = "2001";

  @RequestMapping(
      value = "/getGoodsInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody GoodsDTO input) {
    if (null != input && GOODS_ID.equals(input.getDataId())) {
      GoodsDTO goods = new GoodsDTO();
      goods.setDataId(GOODS_ID);
      goods.setGoodsPrice(1024);
      goods.setMessage("这是一个苹果,apple,还被咬了一口");

      ThreadUtils.sleep(10);

      return ApiResponse.ok(goods);
    }
    return ApiResponse.fail();
  }
}
