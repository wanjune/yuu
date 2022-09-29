package com.github.wanjune.yuu.base.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * List工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class ListUtil {

  /**
   * 验证2个列表中内容是否一致
   *
   * @param list1st 列表1
   * @param list2nd 列表2
   * @return true - 内容一致 / false - 内容不一致
   */
  public static <T> boolean equal(List<T> list1st, List<T> list2nd) {
    return (list1st == null && list2nd == null)
        || ((list1st != null && list2nd != null) && list1st.size() == list2nd.size()
        && new HashSet<>(list1st).containsAll(list2nd) && new HashSet<>(list2nd).containsAll(list1st));
  }

  /**
   * 字符串形式的数组转换为字符串列表
   *
   * @param strArrays 字符串形式的数组
   * @return 字符串列表
   */
  public static List<String> asList(String strArrays) {
    return StringUtil.isBlank(strArrays) ? null :
        new ArrayList<String>((Arrays.asList(StringUtil.trimFirstAndLastChar(strArrays.replaceAll(StringUtil.SPACE, StringUtil.EMPTY), CstUtil.BRACKET).replaceAll(CstUtil.DOUBLE_QUOTE, StringUtil.EMPTY).split(CstUtil.COMMA))));
  }

  /**
   * 字符串数组转换为字符串列表
   *
   * @param a 字符数组
   * @return 字符串列表
   */
  @SafeVarargs
  public static <T> List<T> asList(T... a) {
    return a != null ? new ArrayList<T>(Arrays.asList(a)) : null;
  }

  /**
   * 列表对象是否为空
   *
   * @param objList 验证列表
   * @return 验证结果
   */
  public static boolean isEmpty(List<?> objList) {
    return objList == null || objList.isEmpty();
  }

  /**
   * 列表对象是否为非空
   *
   * @param objList 验证列表
   * @return 验证结果
   */
  public static boolean nonEmpty(List<?> objList) {
    return !isEmpty(objList);
  }

  /**
   * 列表默认排序(字符顺序)
   *
   * @param objList 对象列表
   */
  public static void sort(List<?> objList) {
    if (nonEmpty(objList)) {
      objList.sort(null);
    }
  }

  /**
   * 列表对象复制
   *
   * @param dest 目标列表对象
   * @param src  原列表对象
   * @param <T>  类型
   */
  public static <T> void copy(List<T> dest, List<T> src) {
    if (isEmpty(src)) {
      return;
    }
    for (int i = 0; i < src.size(); i++) {
      dest.add(null);
    }
    Collections.copy(dest, src);
  }

  /**
   * 按指定大小，分隔集合，将集合按规定个数分为N个部分
   *
   * @param list 被分割的列表对象
   * @param len  每个部分长度
   * @return 按照指定大小切割的List数组
   */
  public static <T> List<List<T>> partition(List<T> list, int len) {
    int limit = (list.size() + len - 1) / len;
    return Stream.iterate(0, n -> n + 1).limit(limit).parallel().map(a -> list.stream().skip((long) a * len).limit(len).parallel().collect(Collectors.toList())).collect(Collectors.toList());
  }

  /**
   * 获取对象List的大小
   *
   * @param list 对象List
   * @param <T>  对象类型
   * @return List大小(null返回0)
   */
  public static <T> int size(List<T> list) {
    return isEmpty(list) ? 0 : list.size();
  }

}
