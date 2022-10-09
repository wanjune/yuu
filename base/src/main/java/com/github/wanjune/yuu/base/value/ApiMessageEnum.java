package com.github.wanjune.yuu.base.value;

import com.github.wanjune.yuu.base.util.StringUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@SuppressWarnings("all")
public enum ApiMessageEnum implements Messageable {
  // 1xx 信息
  CONTINUE(100, "Continue", ""),
  SWITCHING_PROTOCOLS(101, "Switching Protocols", ""),
  PROCESSING(102, "Processing", ""),
  CHECKPOINT(103, "Checkpoint", ""),
  // 2xx 成功
  OK(200, "OK", ""),
  CREATED(201, "Created", ""),
  ACCEPTED(202, "Accepted", ""),
  NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information", ""),
  NO_CONTENT(204, "No Content", ""),
  RESET_CONTENT(205, "Reset Content", ""),
  PARTIAL_CONTENT(206, "Partial Content", ""),
  MULTI_STATUS(207, "Multi-Status", ""),
  ALREADY_REPORTED(208, "Already Reported", ""),
  IM_USED(226, "IM Used", ""),
  // 3xx 重定向
  MULTIPLE_CHOICES(300, "Multiple Choices", ""),
  MOVED_PERMANENTLY(301, "Moved Permanently", ""),
  FOUND(302, "Found", ""),
  @Deprecated
  MOVED_TEMPORARILY(302, "Moved Temporarily", ""),
  SEE_OTHER(303, "See Other", ""),
  NOT_MODIFIED(304, "Not Modified", ""),
  @Deprecated
  USE_PROXY(305, "Use Proxy", ""),
  TEMPORARY_REDIRECT(307, "Temporary Redirect", ""),
  PERMANENT_REDIRECT(308, "Permanent Redirect", ""),
  // 4xx 客户端错误
  BAD_REQUEST(400, "Bad Request", "错误的请求"),
  UNAUTHORIZED(401, "Unauthorized", "未授权"),
  PAYMENT_REQUIRED(402, "Payment Required", ""),
  FORBIDDEN(403, "Forbidden", ""),
  NOT_FOUND(404, "Not Found", ""),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed", ""),
  NOT_ACCEPTABLE(406, "Not Acceptable", ""),
  PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required", ""),
  REQUEST_TIMEOUT(408, "Request Timeout", ""),
  CONFLICT(409, "Conflict", ""),
  GONE(410, "Gone", ""),
  LENGTH_REQUIRED(411, "Length Required", ""),
  PRECONDITION_FAILED(412, "Precondition Failed", ""),
  PAYLOAD_TOO_LARGE(413, "Payload Too Large", ""),
  @Deprecated
  REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large", ""),
  URI_TOO_LONG(414, "URI Too Long", ""),
  @Deprecated
  REQUEST_URI_TOO_LONG(414, "Request-URI Too Long", ""),
  UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", ""),
  REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable", ""),
  EXPECTATION_FAILED(417, "Expectation Failed", ""),
  I_AM_A_TEAPOT(418, "I'm a teapot", ""),
  @Deprecated
  INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource", ""),
  @Deprecated
  METHOD_FAILURE(420, "Method Failure", ""),
  @Deprecated
  DESTINATION_LOCKED(421, "Destination Locked", ""),
  UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", ""),
  LOCKED(423, "Locked", ""),
  FAILED_DEPENDENCY(424, "Failed Dependency", ""),
  TOO_EARLY(425, "Too Early", ""),
  UPGRADE_REQUIRED(426, "Upgrade Required", ""),
  PRECONDITION_REQUIRED(428, "Precondition Required", ""),
  TOO_MANY_REQUESTS(429, "Too Many Requests", ""),
  REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large", ""),
  UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons", ""),
  // 5xx 服务器错误
  INTERNAL_SERVER_ERROR(500, "Internal Server Error", ""),
  NOT_IMPLEMENTED(501, "Not Implemented", ""),
  BAD_GATEWAY(502, "Bad Gateway", ""),
  SERVICE_UNAVAILABLE(503, "Service Unavailable", ""),
  GATEWAY_TIMEOUT(504, "Gateway Timeout", ""),
  HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported", ""),
  VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates", ""),
  INSUFFICIENT_STORAGE(507, "Insufficient Storage", ""),
  LOOP_DETECTED(508, "Loop Detected", ""),
  BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded", ""),
  NOT_EXTENDED(510, "Not Extended", ""),
  NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required", ""),

  // 4xxx - 自定义业务错误
  METHOD_ARGUMENT_TYPE_MISMATCH(4001, "Method Argument Type Mismatch", "参数类型不匹配"),
  METHOD_ARGUMENT_NOT_EXIST(4002, "Method Argument Not Exist", "参数不能为空"),
  METHOD_ARGUMENT_NOT_NULL(4003, "Method Argument Not Null", "参数[{ext}]为必填项"),
  METHOD_ARGUMENT_INVALID(4004, "Method Argument Invalid", "参数[{ext}]格式不正确"),
  METHOD_ARGUMENT_PARSE_FAILED(4005, "Method Argument Parse Failed", "参数解析(JSON)失败"),
  API_UNKNOWN(4100, "API Unknown", "接口不存在或暂不可用"),
  API_DISABLED(4101, "API Disabled", "接口未启用或被禁用"),
  API_REQUEST_MAX_LIMIT(4102, "Request Count Limited", "接口今日最大请求次数超限,暂不可用"),
  CLIENTCD_NOT_NULL(4103, "Clientcd Not Null", "客户端ID和安全KEY不能为空"),
  CLIENTCD_INVALID(4104, "Clientcd Invalid", "客户端认证失败"),
  CLIENTCD_DISABLED(4105, "Clientcd Disabled", "该客户端未启用或被禁用"),
  CLIENTCD_UNAUTHORIZED(4106, "Clientcd Unauthorized", "该客户端未授权"),
  DATA_COLUMNS_DISACCORD(4120, "Data Columns Disaccord", "数据列名不一致"),
  RECORD_MAX_LIMIT(4121, "Max Record Limited", "数据最大件数超限"),
  RECORD_EXIST(4122, "The Record already exists", "记录已存在"),
  RECORD_NO_FOUND(4123, "The Record not found", "记录不存在"),
  CUST_CODE_NO_FOUND(4124, "Customer-Code not found", "MDM门店编码[{ext}]无效"),
  OUTER_CODE_NO_FOUND(4125, "Outer-Code not found", "箱码/外码[{ext}]无效"),

  // 5xxx - 自定义系统错误
  SERVER_ERROR(5000, "Internal Server Error", "发生系统异常"),
  KAFKA_ACCESS_FAILED(5002, "Kafka Send Failed", "Kafka服务异常:{ext}"),
  REDIS_ACCESS_FAILED(5005, "Redis Access Failed", "Redis服务异常:{ext}"),
  OSS_ACCESS_FAILED(5009, "OSS Access Failed", "OSS服务异常:{ext}"),
  MAXCOMPUTE_ACCESS_FAILED(5010, "MaxCompute Access Failed", "MaxCompute服务异常:{ext}"),
  HOLOGRES_ACCESS_FAILED(5011, "Hologres Access Failed", "Hologres服务异常:{ext}"),
  SFTP_ACCESS_FAILED(5012, "SFTP Access Failed", "SFTP服务异常:{ext}"),
  OKHTTP_ACCESS_FAILED(5013, "OkHttp Access Failed", "调用外部接口异常:{ext}");

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
  public static ApiMessageEnum of(final int code) {
    for (ApiMessageEnum messageEnum : ApiMessageEnum.values()) {
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
    return StringUtil.isNotBlank(desc) ? desc : message;
  }

}
