package com.utils.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 进行重试的方法的函数式编程
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/06/01
 */
public class RetryFunctionImpl {

  /** 重试配制 */
  private static final String DEFAULT_SLEEP_TIME = "5,10,30,0";

  /** 休眠的时间单位，秒 */
  private static final long TIME_SECOND = 1000;

  /** 休眠时间 */
  private static final List<Long> SLEEP_TIME_LIST = new ArrayList<>();

  static {
    String value = DEFAULT_SLEEP_TIME;
    String[] sleepValues = value.split(",");

    // 将休眠时间加入到集合中
    for (String sleepvalues : sleepValues) {
      SLEEP_TIME_LIST.add(Long.parseLong(sleepvalues));
    }
  }

  public static final RetryFunctionImpl INSTANCE = new RetryFunctionImpl();

  /** 日志 */
  private Logger logger = LoggerFactory.getLogger(RetryFunctionImpl.class);

  /**
   * 进行重试的逻辑操作
   *
   * @param reInstance
   * @param dataInput
   * @return
   * @throws Exception
   */
  public Object apply(RetryInf reInstance, Map<String, Object> dataInput) throws Exception {
    Exception exception = null;

    int retryIndex = 0;
    // 进行重试操作
    for (long sleepTime : SLEEP_TIME_LIST) {
      try {
        Object result = reInstance.retry(dataInput);

        return result;
      } catch (Exception e) {
        e.printStackTrace();

        logger.error("RetryFunctionImpl exception: num: {} maxretry {}", retryIndex, getMaxRetry(), e);

        exception = e;
      }

      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime * TIME_SECOND);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      retryIndex++;
    }

    if (null != exception) {
      throw exception;
    } else {
      throw new Exception(
          "retry "
              + getMaxRetry()
              + ",object:"
              + reInstance
              + ",param "
              + dataInput
              + " exception ");
    }
  }

  /**
   * 进行重试的逻辑操作,看我重试不返回异常
   *
   * @param reInstance
   * @param dataInput
   * @return
   * @throws Exception
   */
  public Object applyRun(RetryInf reInstance, Map<String, Object> dataInput) {
    try {
      return this.apply(reInstance, dataInput);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("RetryFunctionImpl applyNext exception: maxretry {}", e);
    }

    return null;
  }

  /**
   * 获取最大重试次数
   *
   * @return
   */
  private int getMaxRetry() {
    return SLEEP_TIME_LIST.size() - 1;
  }
}
