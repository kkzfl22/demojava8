package com.liujun.fel;

import com.greenpineyu.fel.function.Function;
import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.function.CommonFunction;
import org.junit.Test;
import com.greenpineyu.fel.context.AbstractContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    long count5 = 20000 * 10000;
    System.out.println("\nFelExpress3 数据量 20000 万：");
    execute(count5, ruleExpress);
  }

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

  @Test
  public void runMethod() {
    Function fun =
        new CommonFunction() {

          public String getName() {
            return "hello";
          }

          /*
           * 调用hello("xxx")时执行的代码
           */
          @Override
          public Object call(Object[] arguments) {
            Object msg = null;
            if (arguments != null && arguments.length > 0) {
              msg = arguments[0];
            }
            return Objects.toString(msg);
          }
        };
    FelEngine e = new FelEngineImpl();
    // 添加函数到引擎中。
    e.addFun(fun);
    String exp = "hello('fel')";
    // 解释执行
    Object eval = e.eval(exp);
    System.out.println("hello " + eval);
    // 编译执行
    Expression compile = e.compile(exp, null);
    eval = compile.eval(null);
    System.out.println("hello " + eval);
  }

  @Test
  public void runCase() {
    // 负责提供气象服务的上下文环境
    FelContext ctx =
        new AbstractContext() {
          @Override
          public Object get(String name) {
            if ("天气".equals(name)) {
              return "晴";
            }
            if ("温度".equals(name)) {
              return 25;
            }
            return null;
          }
        };
    FelEngine fel = new FelEngineImpl(ctx);
    Object eval = fel.eval("'天气:'+天气+';温度:'+温度");
    System.out.println(eval);
  }

  @Test
  public void execute23() {

    long start = System.currentTimeMillis();

    FelEngine fel = new FelEngineImpl();
    FelContext context = fel.getContext();
    context.set("city", "城市");

    String expression = "city.equals(\"杭州\") &&  ? 200 : 300 ";

    Expression exp = fel.compile(expression, context);

    int count = 10;

    for (int i = 0; i < count; i++) {

      if (i % 2 == 0) {
        context.set("city", "杭州");
      } else {
        context.set("city", "北京");
      }

      Object res = exp.eval(context);
      System.out.println(res);
    }
    long end = System.currentTimeMillis();
    long intervalInMs = end - start;
    float avg = (float) count / intervalInMs * 1000;

    System.out.println("总耗时毫秒：" + intervalInMs);
    System.out.println("每秒处理条数：" + avg);
  }
}
