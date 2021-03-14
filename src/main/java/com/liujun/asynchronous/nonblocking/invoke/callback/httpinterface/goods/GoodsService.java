package com.liujun.asynchronous.nonblocking.invoke.callback.httpinterface.goods;

import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.liujun.asynchronous.nonblocking.common.utils.RestTemplateUtils;
import com.liujun.command.threadpool.TaskThreadDataPool;
import com.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 用户模拟商品服务
 *
 * @author liujun
 * @version 0.0.1
 */
@RestController
@RequestMapping("/goods")
public class GoodsService {

  /** 日志信息 */
  private Logger logger = LoggerFactory.getLogger(GoodsService.class);

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @RequestMapping(
      value = "/getGoodsInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody GoodsDTO input) {

    // 将异步任务提交线程池执行
    TaskThreadDataPool.INSTANCE.submit(new RunTask(input));

    return ApiResponse.ok();
  }

  /** 异步通知操作 */
  public class RunTask implements Runnable {
    private GoodsDTO input;

    public RunTask(GoodsDTO input) {
      this.input = input;
    }

    @Override
    public void run() {
      GoodsDTO goods = new GoodsDTO();
      goods.setDataId(input.getDataId());
      goods.setGoodsPrice(1024);
      goods.setMessage("这是一个苹果,apple,还被咬了一口");

      ThreadUtils.sleep(10);

      // 发送异步通知操作
      callBackGoods(goods);
    }

    /**
     * 商品的回调操作
     *
     * @param goodsInfo 商品信息
     * @return 当前的用户的信息
     */
    private boolean callBackGoods(GoodsDTO goodsInfo) {

      logger.info("request goods start {} ", goodsInfo);

      HttpHeaders headers = new HttpHeaders();
      // 将对象装入HttpEntity中
      HttpEntity<GoodsDTO> request = new HttpEntity<>(goodsInfo, headers);
      ResponseEntity<String> result =
          restTemplate.postForEntity(
              "http://localhost:9010/order/goodsCallBack", request, String.class);
      if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
        return true;
      }

      return false;
    }
  }
}
