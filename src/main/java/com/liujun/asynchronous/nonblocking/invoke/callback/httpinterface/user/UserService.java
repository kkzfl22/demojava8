package com.liujun.asynchronous.nonblocking.invoke.callback.httpinterface.user;

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
 * 模拟的用户服务
 *
 * @author liujun
 * @version 0.0.1
 */
@RestController
@RequestMapping("/user")
public class UserService {

  /** 日志信息 */
  private Logger logger = LoggerFactory.getLogger(UserService.class);

  /** 获取连接处理对象 */
  private RestTemplate restTemplate = RestTemplateUtils.INSTANCE.getRestTemplate();

  @RequestMapping(
      value = "/getUserInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody UserDTO input) {

    // 提交任务，进行异步执行，执行回调操作
    TaskThreadDataPool.INSTANCE.submit(new RunTask(input));

    return ApiResponse.ok();
  }

  /** 异步通知操作 */
  public class RunTask implements Runnable {
    private UserDTO input;

    public RunTask(UserDTO input) {
      this.input = input;
    }

    @Override
    public void run() {
      UserDTO rsp = new UserDTO();
      rsp.setUserId(input.getUserId());
      rsp.setName("bug_null");
      rsp.setAddress("this is shanghai");
      rsp.setUserId(input.getUserId());

      ThreadUtils.sleep(5);

      // 发送异步通知操作
      callBackUsers(rsp);
    }

    /**
     * 获取商品信息
     *
     * @param userInfo 商品信息
     * @return 当前的用户的信息
     */
    private boolean callBackUsers(UserDTO userInfo) {

      logger.info("request user start {} ", userInfo);

      HttpHeaders headers = new HttpHeaders();
      // 将对象装入HttpEntity中
      HttpEntity<UserDTO> request = new HttpEntity<>(userInfo, headers);
      ResponseEntity<String> result =
          restTemplate.postForEntity(
              "http://localhost:9010/order/userCallBack", request, String.class);
      if (HttpStatus.OK.value() == result.getStatusCodeValue()) {
        return true;
      }

      return false;
    }
  }
}
