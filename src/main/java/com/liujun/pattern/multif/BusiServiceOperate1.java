package com.liujun.pattern.multif;

/**
 * @author liujun
 * @version 0.0.1
 * @date 2019/07/17
 */
public class BusiServiceOperate1 implements BusiServiceInf {
  @Override
  public void busiService() {
    CommonBusi.CommMethod(1);
    System.out.println("执行业务逻辑1");
  }
}
