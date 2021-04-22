package com.liujun.ssh2;

import org.springframework.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujun
 * @version 0.0.1
 */
public class TestData {
  /** BeanCopier的缓存 */
  private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE =
      new ConcurrentHashMap<>();

  /** 工具类应隐藏 public 构造器 */
  private TestData() {}

  /**
   * BeanCopier的copy
   *
   * @param source 源文件的
   * @param target 目标文件
   */
  public static void copy(Object source, Object target) {
    String key = genKey(source.getClass(), target.getClass());
    BeanCopier beanCopier;
    if (BEAN_COPIER_CACHE.containsKey(key)) {
      beanCopier = BEAN_COPIER_CACHE.get(key);
    } else {
      beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
      BEAN_COPIER_CACHE.put(key, beanCopier);
    }
    beanCopier.copy(source, target, null);
  }

  public static void copy2(Object source, Object target) {
    String key = genKey(source.getClass(), target.getClass());
    BeanCopier beanCopier = BEAN_COPIER_CACHE.get(key);
    if (beanCopier == null) {
      BeanCopier createNew = BeanCopier.create(source.getClass(), target.getClass(), false);
      beanCopier = BEAN_COPIER_CACHE.putIfAbsent(key, createNew);
      if (null == beanCopier) {
        beanCopier = createNew;
      }
    }
    beanCopier.copy(source, target, null);
  }

  /**
   * 生成key
   *
   * @param srcClazz 源文件的class
   * @param tgtClazz 目标文件的class
   * @return string
   */
  private static String genKey(Class<?> srcClazz, Class<?> tgtClazz) {
    return srcClazz.getName() + tgtClazz.getName();
  }
}
