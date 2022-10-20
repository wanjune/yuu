package com.github.wanjune.yuu.base.value;

import com.github.wanjune.yuu.base.util.StringUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TaskMessageEnum implements Messageable {

  // 正常
  OK(200, "OK", "成功"),

  // 5xxx - 自定义系统错误
  SERVER_ERROR(5000, "Internal Server Error", "发生系统异常"),
  KAFKA_ACCESS_FAILED(5002, "Kafka Send Failed", "Kafka服务异常-{ext}"),
  REDIS_ACCESS_FAILED(5005, "Redis Access Failed", "Redis服务异常-{ext}"),
  OSS_ACCESS_FAILED(5009, "OSS Access Failed", "OSS服务异常-{ext}"),
  MAXCOMPUTE_ACCESS_FAILED(5010, "MaxCompute Access Failed", "MaxCompute服务异常-{ext}"),
  HOLOGRES_ACCESS_FAILED(5011, "Hologres Access Failed", "Hologres服务异常-{ext}"),
  SFTP_ACCESS_FAILED(5012, "SFTP Access Failed", "SFTP服务异常-{ext}"),
  OKHTTP_ACCESS_FAILED(5013, "OkHttp Access Failed", "调用外部接口异常-{ext}");

  // 状态码
  private final int code;
  // 消息
  private final String message;
  // 描述
  private final String desc;

  /**
   * 获取枚举对象
   *
   * @param code 状态码
   * @return 枚举对象
   */
  public static TaskMessageEnum of(final int code) {
    for (TaskMessageEnum messageEnum : TaskMessageEnum.values()) {
      if (messageEnum.code() == code) {
        return messageEnum;
      }
    }
    return null;
  }

  /**
   * 获取状态码
   *
   * @return 状态码
   */
  @Override
  public int code() {
    return this.code;
  }

  /**
   * 获取消息
   *
   * @return 消息
   */
  @Override
  public String message() {
    return StringUtil.notBlank(desc) ? desc : message;
  }

}
