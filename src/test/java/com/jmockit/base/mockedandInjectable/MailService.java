package com.jmockit.base.mockedandInjectable;

/**
 * 发送邮件服务接口
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/10/13
 */
public interface MailService {

  /**
   * 发送邮件
   *
   * @param userId 邮件接受人id
   * @param content 邮件内容
   * @return 发送成功了，就返回true,否则返回false
   */
  boolean sendMail(long userId, String content);
}
