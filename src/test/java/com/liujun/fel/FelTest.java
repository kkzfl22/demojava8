package com.liujun.fel;

import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author liujun
 * @version 0.0.1
 */
public class FelTest {

  @Test
  public void test1() {
    String ruleExpress = "city.equals(\"杭州\") && age<=20";

    long count1 = 1000 * 10000;
    System.out.println("\nFelExpress1 数据量 1000 万：");
    execute(count1, ruleExpress);

    long count2 = 2000 * 10000;
    System.out.println("\nFelExpress1 数据量 2000 万：");
    execute(count2, ruleExpress);

    long count3 = 4000 * 10000;
    System.out.println("\nFelExpress1 数据量 4000 万：");
    execute(count3, ruleExpress);
  }

  @Test
  public void test2() {
    String ruleExpress = "city.equals(\"杭州\") && age<=20 && stringList.contains(str)";

    long count1 = 1000 * 10000;
    System.out.println("\nFelExpress2 数据量 1000 万：");
    execute(count1, ruleExpress);

    long count2 = 2000 * 10000;
    System.out.println("\nFelExpress2 数据量 2000 万：");
    execute(count2, ruleExpress);

    long count3 = 4000 * 10000;
    System.out.println("\nFelExpress2 数据量 4000 万：");
    execute(count3, ruleExpress);
  }

  @Test
  public void test3() {
    String ruleExpress = "a>1 && ((b>1 || c<1) || (a>1 && b<1 && c>1))";

    long count1 = 1000 * 10000;
    System.out.println("\nFelExpress3 数据量 1000 万：");
    execute(count1, ruleExpress);

    long count2 = 2000 * 10000;
    System.out.println("\nFelExpress3 数据量 2000 万：");
    execute(count2, ruleExpress);

    long count3 = 4000 * 10000;
    System.out.println("\nFelExpress3 数据量 4000 万：");
    execute(count3, ruleExpress);


    long count4 = 8000 * 10000;
    System.out.println("\nFelExpress3 数据量 8000 万：");
    execute(count4, ruleExpress);
  }

  // private void execute(long count, String expression) {
  //
  //  FelEngine fel = new FelEngineImpl();
  //  FelContext context = fel.getContext();
  //
  //  long start = System.currentTimeMillis();
  //  List<String> stringList = new ArrayList<>(2);
  //  stringList.add("hello");
  //  stringList.add("world");
  //  Random random = new Random();
  //
  //  fel.compile(expression, context);
  //
  //  for (int i = 0; i < count; i++) {
  //    int age = random.nextInt(50);
  //    if (i % 2 == 0) {
  //      context.set("city", "杭州");
  //    } else {
  //      context.set("city", "北京");
  //    }
  //    context.set("age", age);
  //    context.set("stringList", stringList);
  //    if (i % 3 == 0) {
  //      context.set("str", "hello");
  //    } else if (i % 3 == 1) {
  //      context.set("str", "world");
  //
  //    } else {
  //      context.set("str", "anything");
  //    }
  //    context.set("a", random.nextInt(2));
  //    context.set("b", random.nextInt(2));
  //    context.set("c", random.nextInt(2));
  //
  //    // Object res = fel.eval(expression, context);
  //    //  System.out.println(res);
  //
  //  }
  //  long end = System.currentTimeMillis();
  //  long intervalInMs = end - start;
  //  float avg = (float) count / intervalInMs * 1000;
  //
  //  System.out.println("总耗时毫秒：" + intervalInMs);
  //  System.out.println("每秒处理条数：" + avg);
  // }

  private void execute(long count, String expression) {

    long start = System.currentTimeMillis();

    FelEngine fel = new FelEngineImpl();
    FelContext context = fel.getContext();
    context.set("city", "城市");
    context.set("stringList", "stringList");
    context.set("age", 20);
    context.set("a", 1);
    context.set("b", 1);
    context.set("c", 1);

    List<String> stringList = new ArrayList<>(2);
    stringList.add("hello");
    stringList.add("world");

    Random random = new Random();
    Expression exp = fel.compile(expression, context);

    for (int i = 0; i < count; i++) {
      int age = random.nextInt(50);
      if (i % 2 == 0) {
        context.set("city", "杭州");
      } else {
        context.set("city", "北京");
      }
      context.set("age", age);
      context.set("stringList", stringList);
      if (i % 3 == 0) {
        context.set("str", "hello");
      } else if (i % 3 == 1) {
        context.set("str", "world");

      } else {
        context.set("str", "anything");
      }
      context.set("a", random.nextInt(2));
      context.set("b", random.nextInt(2));
      context.set("c", random.nextInt(2));

      // Object res = fel.eval(expression, context);
      //  System.out.println(res);

      Object res = exp.eval(context);
    }
    long end = System.currentTimeMillis();
    long intervalInMs = end - start;
    float avg = (float) count / intervalInMs * 1000;

    System.out.println("总耗时毫秒：" + intervalInMs);
    System.out.println("每秒处理条数：" + avg);
  }
}
