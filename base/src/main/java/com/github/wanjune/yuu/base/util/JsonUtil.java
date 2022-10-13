package com.github.wanjune.yuu.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class JsonUtil {

  // Jackson数据对象
  public static final ObjectMapper OBJ_MAPPER = new ObjectMapper();

  /**
   * 将数据对象转换为Json格式的字符串
   *
   * @param dataObj 数据对象
   * @return Json格式的字符串
   * @throws JsonProcessingException JsonProcessingException
   */
  public static String writeValueAsString(Object dataObj) throws JsonProcessingException {
    return StringUtil.trimFirstAndLastChar(OBJ_MAPPER.writeValueAsString(dataObj), CstUtil.DOUBLE_QUOTE);
  }

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
   * 获取CSV对象的数据列表
   *
   * @param csvDataList   Json对象数据列表
   * @param csvHeaderList 转换的CSV表头列表
   * @return CSV文件输出对象的数据列表
   * @throws JsonProcessingException JsonProcessingException
   */
  public static List<Map<String, String>> getCsvDataList(List<Map<String, Object>> csvDataList,
                                                         List<String> csvHeaderList) throws JsonProcessingException {
    return getCsvDataList(csvDataList, csvHeaderList, null);
  }

  /**
   * 获取CSV对象的数据列表
   *
   * @param csvDataList    Json对象数据列表
   * @param csvHeaderList  转换的CSV表头列表
   * @param unicodeColumns 需要清理Unicode的列(Informatica的Unicode解析BUG,常常出现nikename中[moji文字])
   * @return CSV文件输出对象的数据列表
   * @throws JsonProcessingException JsonProcessingException
   */
  public static List<Map<String, String>> getCsvDataList(List<Map<String, Object>> csvDataList,
                                                         List<String> csvHeaderList,
                                                         List<String> unicodeColumns) throws JsonProcessingException {
    List<Map<String, String>> reCsvDataList = null;
    if (ListUtil.nonEmpty(csvDataList) && ListUtil.nonEmpty(csvHeaderList)) {
      reCsvDataList = new ArrayList<>(csvDataList.size());
      for (Map<String, Object> csvDataItem : csvDataList) {
        reCsvDataList.add(getCsvData(csvDataItem, csvHeaderList, unicodeColumns));
      }
    }
    return reCsvDataList;
  }

  /**
   * 获取CSV对象的数据
   *
   * @param csvData        Json对象数据
   * @param csvHeaderList  转换的CSV表头
   * @param unicodeColumns 需要清理Unicode的列(Informatica的Unicode解析BUG,常常出现nikename中[moji文字])
   * @return CSV文件输出对象的数据
   * @throws JsonProcessingException JsonProcessingException
   */
  private static Map<String, String> getCsvData(Map<String, Object> csvData,
                                                List<String> csvHeaderList,
                                                List<String> unicodeColumns) throws JsonProcessingException {
    Map<String, String> reCsvData = new HashMap<>(csvHeaderList.size());
    for (String key : csvHeaderList) {
      if (StringUtil.isContains(key, unicodeColumns, true) && csvData.get(key) != null) {
        reCsvData.put(key, writeValueAsString(StringUtil.cleanUnicode(csvData.get(key).toString())));
      } else if (csvData.get(key) != null) {
        reCsvData.put(key, writeValueAsString(csvData.get(key)));
      } else {
        reCsvData.put(key, StringUtil.EMPTY);
      }
    }
    return reCsvData;
  }

}
