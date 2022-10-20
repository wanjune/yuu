package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.exception.OkHttpException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OkHttp工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class OkHttpUtil {

  public static final String JSON = "application/json; charset=utf-8";
  public static final String XML = "application/xml; charset=utf-8";

  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String DELETE = "DELETE";
  public static final String PATCH = "PATCH";

  //public static final String NOT_RESPONDING = "Not Responding";
  public static final String NOT_RESPONDING = "";

  private final OkHttpClient okHttpClient;
  private boolean isPrintLog;

  public OkHttpUtil(OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
    this.isPrintLog = false;
  }

  public OkHttpUtil(OkHttpClient okHttpClient, boolean isPrintLog) {
    this.okHttpClient = okHttpClient;
    this.isPrintLog = isPrintLog;
  }

  /**
   * 请求处理
   * <p>普通字符串对象(上传文件等需要重构)</p>
   *
   * @param method    请求方法(GET/HEAD/POST/DELETE/PUT/PATCH)
   * @param url       请求URL
   * @param params    请求参数(URL地址部)
   * @param headers   请求Header
   * @param mediaType 请求媒体类型(rfc_2045标准)
   * @param body      请求Body字符串(依照mediaType格式)
   * @return Response.Body内容(字符串)
   * @throws Exception 异常
   */
  public String execute(String method,
                        String url,
                        Map<String, Object> params,
                        Map<String, String> headers,
                        String mediaType,
                        String body) throws Exception {

    // 完整请求地址(包含URL部参数)
    StringBuilder sbUrlFull = new StringBuilder(url);
    // 接口响应信息Body内容
    String strResBody = NOT_RESPONDING;

    try {
      // Request构建器
      Request.Builder reqBuilder = new Request.Builder();

      // URL处理
      if (MapUtil.nonEmpty(params)) {
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
          sbUrlFull.append(isFirst ? "?" : "&").append(entry.getKey()).append("=").append(entry.getValue());
          isFirst = false;
        }
      }

      // Header
      if (MapUtil.nonEmpty(headers)) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }
      }

      // Body
      RequestBody reqBody = null;
      if (StringUtil.notBlank(body) && StringUtil.notBlank(mediaType)) {
        reqBody = RequestBody.create(body, MediaType.parse(mediaType));
      }

      // 执行请求
      try (Response response = okHttpClient.newCall(reqBuilder.url(sbUrlFull.toString()).method(method, reqBody).build()).execute()) {
        if (response.body() != null) {
          strResBody = response.body().string();
        }
      }

      // 请求数据详情日志
      if (isPrintLog) {
        log.info(String.format("[%s]外部接口请求详情\n[url]:\t%s\n[method]:\t%s\n[headers]:\t%s\n[body]:\t%s\n[响应body]:\t%s",
            "execute", sbUrlFull.toString(), method, headers, body, strResBody));
      }

      return strResBody;
    } catch (Exception ex) {
      log.error(String.format("[%s]外部接口请求发生异常!\n[url]:\t%s\n[method]:\t%s\n[headers]:\t%s\n[body]:\t%s\n[响应body]:\t%s",
          "execute", sbUrlFull.toString(), method, headers, body, strResBody), ex);
      throw new OkHttpException(String.format("外部接口请求失败[url:%s,method:%s,headers:%s,body:%s,响应body:%s]",
          sbUrlFull.toString(), method, headers, body, strResBody), ex);
    }

  }

}
