package com.github.wanjune.yuu.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json字符串工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class JsonUtil {

  // Jackson数据对象
  public static final ObjectMapper OBJ_MAPPER = new ObjectMapper();
  // CSV需要去除Unicode的数据表头的列名
  public static final List<String> CSV_COLUMN_UNICODE_SPECIAL = ListUtil.asList();


  /**
   * 获取指定类型数据对象
   *
   * @param strJson      Json格式字符串
   * @param valueTypeRef Json格式字符串
   * @return 数据对象
   * @throws JsonProcessingException JsonProcessingException
   */
  public static <T> T getType(String strJson, TypeReference<T> valueTypeRef) throws JsonProcessingException {
    return StringUtil.isBlank(strJson) ? null : OBJ_MAPPER.readValue(strJson, valueTypeRef);
  }

  /**
   * 获取Map对象
   *
   * @param strJson Json标准格式字符串
   * @return Map对象
   * @throws JsonProcessingException JsonProcessingException
   */
  public static Map<String, Object> getMap(String strJson) throws JsonProcessingException {
    return getType(strJson, new TypeReference<Map<String, Object>>() {
    });
  }

  /**
   * 获取Map对象
   *
   * @param strJson Json格式字符串
   * @return Map对象
   * @throws JsonProcessingException JsonProcessingException
   */
  public static List<Map<String, Object>> getMapList(String strJson) throws JsonProcessingException {
    return getType(strJson, new TypeReference<ArrayList<Map<String, Object>>>() {
    });
  }

  /**
   * 获取单态元素
   * <p>如果对象列表只有1个元素返回单个元素</p>
   * <p>避免转换JSON时单个元素使用JSON格式数组</p>
   *
   * @param listObj 数据列表对象
   * @param <T>     列表中元素对象类型
   * @return 单个元素 或 元素列表
   */
  public static <T> Object getSingleton(List<T> listObj) {
    return ListUtil.isEmpty(listObj) ? null : (listObj.size() == 1 ? listObj.get(0) : listObj);
  }

  /**
   * 判断是否为Json字符串
   *
   * @param strJson Json格式字符串
   * @return 判断结果
   */
  public static boolean isJsonString(String strJson) {
    try {
      OBJ_MAPPER.readValue(strJson, new TypeReference<Map<String, Object>>() {
      });
    } catch (Exception ex) {
      return false;
    }
    return true;
  }

  /**
   * 判断是否为Json数组字符串
   *
   * @param strJson Json格式字符串
   * @return 判断结果
   */
  public static boolean isJsonArray(String strJson) {
    try {
      OBJ_MAPPER.readValue(strJson, new TypeReference<ArrayList<Map<String, Object>>>() {
      });
    } catch (Exception ex) {
      return false;
    }
    return true;
  }

  /**
   * 将数据对象转换为Json格式的字符串
   *
   * @param dataObj 数据对象
   * @return Json格式的字符串
   * @throws JsonProcessingException JsonProcessingException
   */
  public static String writeValueAsString(Object dataObj) throws JsonProcessingException {
    return StringUtil.trimFirstAndLastChar(OBJ_MAPPER.writeValueAsString(dataObj), CstUtil.COMMA);
  }

  /**
   * 获取CSV对象的数据列表
   *
   * @param csvDataList   Json对象数据列表
   * @param csvHeaderList 转换的CSV表头列表
   * @return 待CSV转换的对象数据列表
   * @throws JsonProcessingException JsonProcessingException
   */
  public static List<Map<String, String>> getCsvDataList(List<Map<String, Object>> csvDataList,
                                                         List<String> csvHeaderList) throws JsonProcessingException {

    if (ListUtil.isEmpty(csvDataList) || ListUtil.isEmpty(csvHeaderList)) {
      return null;
    }

    List<Map<String, String>> dataCsvList = new ArrayList<>(csvDataList.size());
    for (Map<String, Object> dataItem : csvDataList) {
      dataCsvList.add(getCsvData(dataItem, csvHeaderList));
    }
    return dataCsvList;
  }

  /**
   * 获取CSV对象的数据
   *
   * @param csvData       Json对象数据
   * @param csvHeaderList 转换的CSV表头
   * @return 待CSV转换的对象数据
   * @throws JsonProcessingException JsonProcessingException
   */
  private static Map<String, String> getCsvData(Map<String, Object> csvData,
                                                List<String> csvHeaderList) throws JsonProcessingException {

    Map<String, String> dataCsv = new HashMap<>(csvHeaderList.size());
    for (String key : csvHeaderList) {
      if (StringUtil.isContainsIgnore(key, CSV_COLUMN_UNICODE_SPECIAL) && csvData.get(key) != null) {
        csvData.put(key, writeValueAsString(StringUtil.cleanUnicode(csvData.get(key).toString())));
      } else if (csvData.get(key) != null) {
        csvData.put(key, writeValueAsString(csvData.get(key)));
      } else {
        csvData.put(key, StringUtil.EMPTY);
      }
    }
    return dataCsv;
  }

}
