package com.liujun.base.type;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Type的获得 有很多场景下我们可以获得Type，比如：
 *
 * <p>1，当我们拿到一个Class，用Class. getGenericInterfaces()方法得到Type[]，也就是这个类实现接口的Type类型列表。
 *
 * <p>2，当我们拿到一个Class，用Class.getDeclaredFields()方法得到Field[]，也就是类的属性列表，然后用Field.
 * getGenericType()方法得到这个属性的Type类型。
 *
 * <p>3，当我们拿到一个Method，用Method. getGenericParameterTypes()方法获得Type[]，也就是方法的参数类型列表。 ————————————————
 *
 * @author liujun
 * @version 0.0.1
 */
public class TypeUse implements Serializable {

  private int intType;

  private Integer obj2;

  public static void test(
      TypeUse p0,
      List<TypeUse> p1,
      Map<String, TypeUse> p2,
      List<String>[] p3,
      Map<String, TypeUse>[] p4,
      List<? extends TypeUse> p5,
      Map<? extends TypeUse, ? super TypeUse> p6,
      ArrayList<TypeUse> p7
      // T p7
      ) {}

  public static void main(String[] args)
      throws NoSuchFieldException, IllegalAccessException, InstantiationException {

    // 获得类实现的接口
    Type[] dataType = TypeUse.class.getGenericInterfaces();

    // 获取属性的类型
    Field intTypeField = TypeUse.class.getDeclaredField("intType");
    Type fieldType = intTypeField.getGenericType();

    Method[] methods = TypeUse.class.getMethods();

    for (int i = 0; i < methods.length; i++) {
      Method oneMethod = methods[i];

      if (oneMethod.getName().equals("test")) {
        Type[] types = oneMethod.getGenericParameterTypes();

        System.out.println(DataParse.getRawType(types[0]));
        System.out.println(DataParse.getRawType(types[1]));
        System.out.println(DataParse.getRawType(types[2]));
        System.out.println(DataParse.getRawType(types[3]));
        System.out.println(DataParse.getRawType(types[4]));
        System.out.println(DataParse.getRawType(types[5]));
        System.out.println(DataParse.getRawType(types[6]));
        System.out.println(DataParse.getRawType(types[7]));

        // 第一个参数，TypeUse p0
        Class type0 = (Class) types[0];
        System.out.println("type0:" + type0.getName());

        // 第二个参数，List<TypeUse> p1
        Type type1 = types[1];
        ParameterizedType type1Data = (ParameterizedType) type1;
        System.out.println(type1Data);
        Type[] parameterizedType1 = ((ParameterizedType) type1).getActualTypeArguments();
        Class parameterizedType1_0 = (Class) parameterizedType1[0];
        System.out.println("parameterizedType1_0:" + parameterizedType1_0.getName());
        Class<?> dataTypeInfo = DataParse.getRawType(types[1]);

        List data = null;

        if (dataTypeInfo.equals(List.class)) {
          data = new ArrayList();
        }

        Object instance = parameterizedType1_0.newInstance();
        data.add(instance);

        // 第三个参数，Map<String,TypeUse> p2
        Type type2 = types[2];
        Type[] parameterizedType2 = ((ParameterizedType) type2).getActualTypeArguments();
        Class parameterizedType2_0 = (Class) parameterizedType2[0];
        System.out.println("parameterizedType2_0:" + parameterizedType2_0.getName());
        Class parameterizedType2_1 = (Class) parameterizedType2[1];
        System.out.println("parameterizedType2_1:" + parameterizedType2_1.getName());

        // 第四个参数，List<String>[] p3
        Type type3 = types[3];
        Type genericArrayType3 = ((GenericArrayType) type3).getGenericComponentType();
        ParameterizedType parameterizedType3 = (ParameterizedType) genericArrayType3;
        Type[] parameterizedType3Arr = parameterizedType3.getActualTypeArguments();
        Class class3 = (Class) parameterizedType3Arr[0];
        System.out.println("class3:" + class3.getName());

        // 第五个参数，Map<String,TypeUse>[] p4
        Type type4 = types[4];
        Type genericArrayType4 = ((GenericArrayType) type4).getGenericComponentType();
        ParameterizedType parameterizedType4 = (ParameterizedType) genericArrayType4;
        Type[] parameterizedType4Arr = parameterizedType4.getActualTypeArguments();
        Class class4_0 = (Class) parameterizedType4Arr[0];
        System.out.println("class4_0:" + class4_0.getName());
        Class class4_1 = (Class) parameterizedType4Arr[1];
        System.out.println("class4_1:" + class4_1.getName());

        // 第六个参数，List<? extends TypeUse> p5
        Type type5 = types[5];
        Type[] parameterizedType5 = ((ParameterizedType) type5).getActualTypeArguments();
        Type[] parameterizedType5_0_upper = ((WildcardType) parameterizedType5[0]).getUpperBounds();
        Type[] parameterizedType5_0_lower = ((WildcardType) parameterizedType5[0]).getLowerBounds();

        // 第七个参数，Map<? extends TypeUse,? super TypeUse> p6
        Type type6 = types[6];
        Type[] parameterizedType6 = ((ParameterizedType) type6).getActualTypeArguments();
        Type[] parameterizedType6_0_upper = ((WildcardType) parameterizedType6[0]).getUpperBounds();
        Type[] parameterizedType6_0_lower = ((WildcardType) parameterizedType6[0]).getLowerBounds();
        Type[] parameterizedType6_1_upper = ((WildcardType) parameterizedType6[1]).getUpperBounds();
        Type[] parameterizedType6_1_lower = ((WildcardType) parameterizedType6[1]).getLowerBounds();
      }
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("TypeUse{");
    sb.append("intType=").append(intType);
    sb.append("obj2=").append(obj2);
    sb.append('}');
    return sb.toString();
  }
}
