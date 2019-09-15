package com.utils.retry;

import java.util.Map;

/**
 * 重试的接口定义
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/01
 */
@FunctionalInterface
public interface RetryInf {

  /**
   * 重试的接口设置
   *
   * @param input
   * @return
   * @throws Exception
   */
  Object retry(Map<String, Object> input) throws Exception;
}
