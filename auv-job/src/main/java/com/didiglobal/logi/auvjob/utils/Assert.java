package com.didiglobal.logi.auvjob.utils;

public class Assert {

  /**
   * 断言,判断非空.
   *
   * @param obj 需要判空的对象
   * @param msg 错误信息
   */
  public static void notNull(Object obj, String msg) {
    if (obj == null) {
      throw new IllegalArgumentException(msg);
    }
  }
}