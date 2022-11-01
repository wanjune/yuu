package com.github.wanjune.yuu.value;

public interface Messageable {

  /**
   * 获取状态码
   *
   * @return 状态码
   */
  default int code() {
    return 0;
  }

  /**
   * 获取消息
   *
   * @return 消息
   */
  default String message() {
    return "";
  }

}
