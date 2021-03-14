package com.liujun.asynchronous.nonblocking.common.user;

import com.liujun.asynchronous.nonblocking.common.ApiResponse;
import com.utils.ThreadUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟的用户服务
 *
 * @author liujun
 * @version 0.0.1
 */
@RestController
@RequestMapping("/user")
public class UserService {

  /** 测试预期值 */
  public static final String CHECK_USER_ID = "1001";

  @RequestMapping(
      value = "/getUserInfo",
      method = {RequestMethod.POST})
  public ApiResponse getUserInfo(@RequestBody UserDTO input) {
    if (null != input && CHECK_USER_ID.equals(input.getUserId())) {
      UserDTO rsp = new UserDTO();
      rsp.setName("bug_null");
      rsp.setAddress("this is shanghai");
      rsp.setUserId(CHECK_USER_ID);

      ThreadUtils.sleep(5);

      return ApiResponse.ok(rsp);
    }
    return ApiResponse.fail();
  }
}
