package com.github.wanjune.yuu.util;

import com.github.wanjune.yuu.exception.OkHttpException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Map;

/**
 * OkHttp工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
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

  public static final String NOT_RESPONDING = "";

  private final OkHttpClient okHttpClient;
  private boolean isPrintLog;
  private String resBody;

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
    StringBuilder apiUrl = new StringBuilder(url);
    // 接口响应信息-Body内容
    String resBody = NOT_RESPONDING;
    // 接口响应信息-HTTP status code(99是不存在的,如果响应CODE=99->发生了异常)
    int resCode = 99;

    try {
      /**
       * 请求参数构建
       */
      // Request构建器
      Request.Builder reqBuilder = new Request.Builder();
      // URL拼接参数
      if (MapUtil.notEmpty(params)) {
        boolean isFirst = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
          apiUrl.append(isFirst ? "?" : "&").append(entry.getKey()).append("=").append(entry.getValue());
          isFirst = false;
        }
      }
      // Header创建
      if (MapUtil.notEmpty(headers)) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }
      }
      // Body创建
      RequestBody reqBody = null;
      if (StringUtil.notBlank(body) && StringUtil.notBlank(mediaType))
        reqBody = RequestBody.create(body, MediaType.parse(mediaType));

      /**
       * 执行请求
       */
      Response response = okHttpClient.newCall(reqBuilder.url(apiUrl.toString()).method(method, reqBody).build()).execute();
      if (response.body() != null) {
        resCode = response.code();
        resBody = response.body().string();
      }

      /**
       * 详细日志
       */
      if (isPrintLog)
        log.info(String.format("[%s]详细日志\n[请求url]:\t%s\n[method]:\t%s\n[headers]:\t%s\n[body]:\t%s\n[响应CODE]:\t%s\n[body]:\t%s", "execute", apiUrl.toString(), method, headers, body, resCode, resBody));

      return resBody;
    } catch (Exception ex) {
      if (isPrintLog)
        log.error(String.format("[%s]发生异常!\n[请求url]:\t%s\n[method]:\t%s\n[headers]:\t%s\n[body]:\t%s\n[响应CODE]:\t%s\n[body]:\t%s", "execute", apiUrl.toString(), method, headers, body, resCode, resBody), ex);

      throw new OkHttpException(String.format("请求失败[请求url:%s,method:%s,headers:%s,body:%s,响应CODE:%s,body:%s]", apiUrl.toString(), method, headers, body, resCode, resBody), ex);
    }

  }

}
