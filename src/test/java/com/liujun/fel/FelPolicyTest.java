package com.liujun.fel;

import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.AbstractContext;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.function.CommonFunction;
import com.greenpineyu.fel.function.Function;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author liujun
 * @version 0.0.1
 */
public class FelPolicyTest {

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
