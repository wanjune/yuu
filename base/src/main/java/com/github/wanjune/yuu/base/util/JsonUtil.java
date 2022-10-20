package com.github.wanjune.yuu.base.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
   * 将数据对象转换为Json格式的字符串
   *
   * @param data 数据对象
   * @return Json格式的字符串
   * @throws Exception Exception
   */
  public static String writeValueAsString(final Object data) throws Exception {
    return StringUtil.trimFirstAndLastChar(MAPPER.writeValueAsString(data), CstUtil.DOUBLE_QUOTE);
  }

  /**
   * 获取指定类型数据对象
   *
   * @param jsonString   Json格式字符串
   * @param valueTypeRef Json格式字符串
   * @return 数据对象
   * @throws Exception Exception
   */
  public static <T> T getType(final String jsonString, final TypeReference<T> valueTypeRef) throws Exception {
    return StringUtil.isBlank(jsonString) ? null : MAPPER.readValue(jsonString, valueTypeRef);
  }

  /**
   * 获取Map对象
   *
   * @param jsonString Json标准格式字符串
   * @return Map对象
   * @throws Exception Exception
   */
  public static Map<String, Object> getMap(final String jsonString) throws Exception {
    return getType(jsonString, new TypeReference<Map<String, Object>>() {
    });
  }

  /**
   * 获取Map对象
   *
   * @param jsonString Json格式字符串
   * @return Map对象
   * @throws Exception Exception
   */
  public static List<Map<String, Object>> getMapList(final String jsonString) throws Exception {
    return getType(jsonString, new TypeReference<ArrayList<Map<String, Object>>>() {
    });
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
    } catch (Exception ex) {
      return false;
    }
    return true;
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
    } catch (Exception ex) {
      return false;
    }
    return true;
  }

}
