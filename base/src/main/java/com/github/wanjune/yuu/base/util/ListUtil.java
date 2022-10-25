package com.github.wanjune.yuu.base.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
   * 获取对象List的大小
   *
   * @param list 对象List
   * @param <T>  对象类型
   * @return List大小
   */
  public static <T> int size(final List<T> list) {
    return isEmpty(list) ? 0 : list.size();
  }

  /**
   * 验证2个列表中内容是否一致
   *
   * @param list1 列表1
   * @param list2 列表2
   * @return 验证结果
   */
  public static <T> boolean equal(final List<T> list1, final List<T> list2) {
    return (list1 == null && list2 == null)
        || ((list1 != null && list2 != null) && list1.size() == list2.size() && new HashSet<>(list1).containsAll(list2) && new HashSet<>(list2).containsAll(list1));
  }

  /**
   * 对象数组转换为对象列表
   *
   * @param array 对象数组
   * @return 对象列表
   */
  @SafeVarargs
  public static <T> List<T> asList(final T... array) {
    return array == null ? null : new ArrayList<>(Arrays.asList(array));
  }

  /**
   * 字符串形式的数组转换为列表
   *
   * @param arraysString 字符串形式的数组
   * @return 字符串列表
   */
  public static List<String> asList(final String arraysString) {
    if (StringUtil.isBlank(arraysString)) return null;
    String stdArraysString = StringUtil.trimFirstAndLastChar(arraysString, CstUtil.BRACKET)
        .replaceAll(CstUtil.COMMA + StringUtil.SPACE, CstUtil.COMMA)
        .replaceAll(CstUtil.DOUBLE_QUOTE, StringUtil.EMPTY);
    return StringUtil.isBlank(stdArraysString) ? null : new ArrayList<>((Arrays.asList(stdArraysString.split(CstUtil.COMMA))));
  }


  /**
   * 列表对象是否为空
   *
   * @param list 验证列表
   * @return 验证结果
   */
  public static boolean isEmpty(final List<?> list) {
    return list == null || list.isEmpty();
  }

  /**
   * 列表对象是否为非空
   *
   * @param list 验证列表
   * @return 验证结果
   */
  public static boolean notEmpty(final List<?> list) {
    return !isEmpty(list);
  }

  /**
   * 获取单态元素
   * <p>如果对象列表只有1个元素返回单个元素</p>
   * <p>避免转换JSON时单个元素使用JSON格式数组</p>
   *
   * @param list 数据列表对象
   * @param <T>  列表中元素对象类型
   * @return 单个元素 或 元素列表
   */
  public static <T> Object getSingleton(final List<T> list) {
    return isEmpty(list) ? null : (list.size() == 1 ? list.get(0) : list);
  }

  /**
   * 列表默认排序(字符顺序)
   * <p>只支持单元素类型(String,Integer等),不支持复制类型(Map,Class等)</p>
   *
   * @param list 对象列表
   */
  public static void sort(final List<?> list) {
    if (notEmpty(list)) list.sort(null);
  }

  /**
   * 列表对象复制
   *
   * @param src 原列表对象
   * @param <T> 类型
   * @return 复制的列表对象
   */
  public static <T> List<T> copy(final List<T> src) {
    return isEmpty(src) ? src : new ArrayList<>(src);
  }

  /**
   * 将列表按照每部分大小进行分割
   *
   * @param list 被分割的列表对象
   * @param len  每部分列表长度
   * @return 分割的列表
   */
  public static <T> List<List<T>> partition(final List<T> list, int len) {
    if (ListUtil.notEmpty(list) && len < 1) return null;
    int limit = (list.size() + len - 1) / len;
    return Stream.iterate(0, n -> n + 1).limit(limit).parallel().map(a -> list.stream().skip((long) a * len).limit(len).parallel().collect(Collectors.toList())).collect(Collectors.toList());
  }

}
