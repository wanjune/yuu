package com.github.wanjune.yuu.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * HTTP工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class HttpUtil {

  /**
   * 用户授权信息字段
   */
  public static final String APP_ID = "appId";
  public static final String APP_SECKEY = "appSecKey";

  /**
   * Key: Code,Message,Data
   */
  public static final String CODE = "code";
  public static final String MESSAGE = "message";
  public static final String DATA = "data";

  /**
   * 获取HttpRequest
   *
   * @return HttpRequest
   */
  @SuppressWarnings("ConstantConditions")
  public static HttpServletRequest getHttpRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
  }

  /**
   * 获取HttpResponse
   *
   * @return HttpResponse
   */
  @SuppressWarnings("ConstantConditions")
  public static HttpServletResponse getHttpResponse() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
  }

  /**
   * 获取客户端信息</br>
   * 客户端ID(appId) + 客户端安全KEY(appSecKey)
   *
   * @param request HttpRequest
   * @return 获取客户端信息(appId + appSecKey)
   */
  public static Map<String, String> getAppInfo(final HttpServletRequest request) {
    String appId = StringUtil.EMPTY;
    String appSecKey = StringUtil.EMPTY;
    if (StringUtil.notEmpty(request.getHeader(APP_ID))) {
      appId = request.getHeader(APP_ID);
    }
    if (StringUtil.notEmpty(request.getHeader(APP_SECKEY))) {
      appSecKey = request.getHeader(APP_SECKEY);
    }
    return MapUtil.of(APP_ID, appId, APP_SECKEY, appSecKey);
  }

  /**
   * 清理URI
   * <p>其他规则,自行添加<p/>
   *
   * @param uri 请求URI
   * @return 标准URI
   */
  public static String cleanUri(final String uri) {
    return StringUtil.removeEnd(uri, CstUtil.SLASH);
  }

}
