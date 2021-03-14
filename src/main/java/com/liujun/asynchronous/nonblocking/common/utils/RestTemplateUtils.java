package com.liujun.asynchronous.nonblocking.common.utils;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 获取resttemplate对象
 *
 * @author liujun
 * @version 0.0.1
 */
public class RestTemplateUtils {

  /** 实例对象 */
  public static final RestTemplateUtils INSTANCE = new RestTemplateUtils();

  /**
   * 获取连接管理对象
   *
   * @return
   */
  public RestTemplate getRestTemplate() {
    return new RestTemplate(getClientHttpRequestFactory());
  }

  /**
   * 获取连接处理的工厂信息
   *
   * @return
   */
  private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(5000);
    clientHttpRequestFactory.setReadTimeout(5000);
    return clientHttpRequestFactory;
  }
}
