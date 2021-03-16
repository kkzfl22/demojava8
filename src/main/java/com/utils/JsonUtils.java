package com.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 使用gson进行转换的公共类
 *
 * @author liujun
 * @version 0.0.1
 */
public class JsonUtils {

  private static final Gson INSTANCE = new Gson();

  /** 格式化输出json对象 */
  private static final Gson INSTANCE_FORMAT = new GsonBuilder().setPrettyPrinting().create();

  public static Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();

  public static Type MAP_STRING_TYPE = new TypeToken<Map<String, String>>() {}.getType();



  /**
   * 将对象转换为json串
   *
   * @param data
   * @return
   */
  public static String toJson(Object data) {
    if (null == data) {
      return null;
    }

    return INSTANCE.toJson(data);
  }

  /**
   * 格式化输出json
   *
   * @param data
   * @return
   */
  public static String toFormatJson(Object data) {
    if (null == data) {
      return null;
    }
    return INSTANCE_FORMAT.toJson(data);
  }

  /**
   * 通用的转换类型信息
   *
   * @param str 字符串
   * @param typeInfo 类型信息
   * @param <T> 类型信息
   * @return 结果信息
   */
  public static <T> T fromJson(String str, Type typeInfo) {
    if (StringUtils.isEmpty(str)) {
      return null;
    }

    T target = (T) INSTANCE.fromJson(str, typeInfo);
    return target;
  }

  /**
   * 通用的转换类型信息
   *
   * @param str 字符串
   * @param classInfo 类信息
   * @param <T> 类型信息
   * @return 结果信息
   */
  public static <T> T fromJson(String str, Class classInfo) {
    if (StringUtils.isEmpty(str)) {
      return null;
    }

    T target = (T) INSTANCE.fromJson(str, classInfo);
    return target;
  }
}
