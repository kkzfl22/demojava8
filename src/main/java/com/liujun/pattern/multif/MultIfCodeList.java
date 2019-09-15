package com.liujun.pattern.multif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多重if的代码修改
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/07/17
 */
public class MultIfCodeList {

  private static final List<BusiServiceInf> BUSI_LIST = new ArrayList<>();

  static {
    BUSI_LIST.add(new BusiServiceOperate1());
    BUSI_LIST.add(new BusiServiceOperate4());
    BUSI_LIST.add(new BusiServiceOperate5());
  }

  public void operate(int valueCode) {
    for (BusiServiceInf busi : BUSI_LIST) {
      busi.busiService();
    }
  }
}
