package com.liujun.pattern.multif;

import java.util.HashMap;
import java.util.Map;

/**
 * 多重if的代码修改
 *
 * @author liujun
 * @version 0.0.1
 * @date 2019/07/17
 */
public class MultIfCode {

  private static final Map<Integer, BusiServiceInf> BUSI_MAP = new HashMap<>();

  static {
    BUSI_MAP.put(1, new BusiServiceOperate1());
    BUSI_MAP.put(2, new BusiServiceOperate2());
    BUSI_MAP.put(3, new BusiServiceOperate3());
    BUSI_MAP.put(4, new BusiServiceOperate4());
    BUSI_MAP.put(5, new BusiServiceOperate5());
  }

  public void ifCode(int valueCode) {
    BusiServiceInf busi = BUSI_MAP.get(valueCode);

    if (null != busi) {
      busi.busiService();
    }
  }
}
