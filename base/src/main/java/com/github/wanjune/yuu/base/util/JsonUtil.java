package com.github.wanjune.yuu.base.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wanjune.yuu.base.exception.YuuException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class JsonUtil {

  // Jackson对象
  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * 对象转换为JSON字符串
   *
   * @param object 对象
   * @return JSON字符串
   */
  public static String writeValueAsString(final Object object) {
    try {
      return StringUtil.trimFirstAndLastChar(MAPPER.writeValueAsString(object), CstUtil.DOUBLE_QUOTE);
    } catch (Exception ex) {
      throw new YuuException(String.format("[%S]转换JSON字符串失败", object), ex);
    }
  }

  /**
   * JSON字符串转换为对象
   *
   * @param jsonString JSON字符串
   * @param typeRef    对象类型引用
   * @return 数据对象
   */
  public static <T> T getType(final String jsonString, final TypeReference<T> typeRef) {
    try {
      return StringUtil.isBlank(jsonString) ? null : MAPPER.readValue(jsonString, typeRef);
    } catch (Exception ex) {
      throw new YuuException(String.format("字符串[%S]转换类型失败", jsonString), ex);
    }
  }

  /**
   * JSON字符串转换为Map
   *
   * @param jsonString JSON字符串
   * @return Map对象
   */
  public static Map<String, Object> getMap(final String jsonString) {
    try {
      return getType(jsonString, new TypeReference<Map<String, Object>>() {
      });
    } catch (Exception ex) {
      throw new YuuException(String.format("字符串[%S]转换Map失败", jsonString), ex);
    }
  }

  /**
   * JSON字符串转换为Map列表
   *
   * @param jsonString JSON字符串
   * @return Map列表
   */
  public static List<Map<String, Object>> getMapList(final String jsonString) {
    try {
      return getType(jsonString, new TypeReference<ArrayList<Map<String, Object>>>() {
      });
    } catch (Exception ex) {
      throw new YuuException(String.format("字符串[%S]转换Map列表失败", jsonString), ex);
    }
  }

  /**
   * 判断是否为Json字符串
   *
   * @param jsonString Json格式字符串
   * @return 判断结果
   */
  public static boolean isJsonString(final String jsonString) {
    try {
      MAPPER.readValue(jsonString, new TypeReference<Map<String, Object>>() {
      });
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * 判断是否为Json数组字符串
   *
   * @param jsonString Json格式字符串
   * @return 判断结果
   */
  public static boolean isJsonArray(final String jsonString) {
    try {
      MAPPER.readValue(jsonString, new TypeReference<ArrayList<Map<String, Object>>>() {
      });
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

}
