package com.liujun.base.type;

import java.lang.reflect.*;

/**
 * @author liujun
 * @version 0.0.1
 */
public class DataParse {

  public static Class<?> getRawType(Type type) {
    if (null == type) {
      throw new NullPointerException("type is null");
    }

    if (type instanceof Class<?>) {
      return (Class<?>) type;
    }

    // 如果当前类型为参数
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Type rawType = parameterizedType.getRawType();
      if (!(rawType instanceof Class)) {
        throw new IllegalArgumentException("rawType is error");
      }
      return (Class<?>) rawType;
    }
    // 类型为数组
    if (type instanceof GenericArrayType) {
      Type arrayType = ((GenericArrayType) type).getGenericComponentType();
      return Array.newInstance((getRawType(arrayType)), 0).getClass();
    }
    if (type instanceof TypeVariable) {
      return Object.class;
    }
    if (type instanceof WildcardType) {
      return getRawType(((WildcardType) type).getUpperBounds()[0]);
    }

    throw new IllegalArgumentException(
        "exception parameterizedType or GenericArrayType but <"
            + type
            + "> is of type "
            + type.getClass().getName());
  }
}
