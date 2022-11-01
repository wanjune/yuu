package com.github.wanjune.yuu.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wanjune.yuu.exception.YuuException;

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

  // 类型引用 - Map
  public static final TypeReference<Map<String, Object>> TYPE_REF_MAP = new TypeReference<Map<String, Object>>() {
  };

  // 类型引用 - List<Map>
  public static final TypeReference<ArrayList<Map<String, Object>>> TYPE_REF_LIST_MAP = new TypeReference<ArrayList<Map<String, Object>>>() {
  };

  // Jackson对象
  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * 获取对象类型的引用
   * <p>目的:避免引用JsonUtil代码的import中,出现Jackson的TypeReference</p>
   * <p>参数:[new HashMap<String, Object>()] -> 返回等同:[new TypeReference<Map<String, Object>>() {}]</p>
   * <p>参数:[new ArrayList<Map<String, Object>>()] -> 返回等同:[new TypeReference<ArrayList<Map<String, Object>>>() {}]</p>
   *
   * @param type 对象类型
   * @param <T>  对象类型
   * @return 类型引用
   */
  public static <T> TypeReference<T> getTypeRef(T type) {
    return new TypeReference<T>() {
    };
  }

  /**
   * JSON字符串转换为对象
   *
   * @param jsonString JSON字符串
   * @param typeRef    数据类型引用
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
      return getType(jsonString, TYPE_REF_MAP);
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
      return getType(jsonString, TYPE_REF_LIST_MAP);
    } catch (Exception ex) {
      throw new YuuException(String.format("字符串[%S]转换Map列表失败", jsonString), ex);
    }
  }

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
   * 判断是否为Json字符串
   *
   * @param jsonString Json格式字符串
   * @return 判断结果
   */
  public static boolean isJsonString(final String jsonString) {
    try {
      MAPPER.readValue(jsonString, TYPE_REF_MAP);
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
      MAPPER.readValue(jsonString, TYPE_REF_LIST_MAP);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

}
