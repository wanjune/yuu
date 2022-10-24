package com.github.wanjune.yuu.base.util;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * String工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class StringUtil {

  // 常用字符串
  public static final String EMPTY = "";
  public static final String SPACE = " ";

  // 随机字符串 - 变量
  private static final String CANDIDATE_RANDOM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final Random RANDOM = new Random();
  private static final int BOUND = 62;

  // Unicode正则表达式
  private static final String REGEX_UNICODE = "[^\\u0000-\\uFFFF]";

  // 控制字符(ASCII): 0 ~ 31
  private static final char ASCII_CTRL_END = 31;
  // 半角[Space](ASCII): 32
  private static final char ASCII_HALFWIDTH_SPACE = ' ';
  // 半角(ASCII): [!](33) ~ [~](126)
  private static final char ASCII_HALF_WIDTH_START = 33;
  private static final char ASCII_HALF_WIDTH_END = 126;
  // DEL(ASCII): 127
  private static final char ASCII_DEL = 127;
  // 全角[Space](ASCII): 12288
  private static final char ASCII_FULL_WIDTH_SPACE = 12288;
  // 全角(ASCII): [！](65281) ~ [～](65374)
  private static final char ASCII_FULL_WIDTH_START = 65281;
  private static final char ASCII_FULL_WIDTH_END = 65374;
  // 全角和半角的偏移量
  private static final int ASCII_HALF_WITH_FULL_OFFSET = 65248;

  /**
   * 获取实际字符串(带参数的字符串以[{}]变量形式的参数，变为实际字符）
   *
   * @param string 带参数变量的字符串
   * @param params 参数对象(Map类型)
   * @return 真实字符串
   */
  public static String instance(final String string, final Map<String, Object> params) {
    String strResult = string;
    if (notEmpty(string) && MapUtil.notEmpty(params)) {
      for (String itemKey : params.keySet()) {
        strResult = strResult.replaceAll(CstUtil.CURLY_BRACKET_PREFIX + itemKey + CstUtil.CURLY_BRACKET_SUFFIX, params.get(itemKey).toString());
      }
    }
    return strResult;
  }

  /**
   * 字符串是否包含指定的字符串
   *
   * @param string     待判断的字符串
   * @param searchList 检索包含的对象列表
   * @param isIgnore   是否忽略大小写
   * @return 判断结果
   */
  public static boolean isContains(final String string, final List<String> searchList, final boolean isIgnore) {
    boolean isContained = false;
    if (notBlank(string) && ListUtil.notEmpty(searchList)) {
      for (String searchItem : searchList) {
        if ((isIgnore && string.toLowerCase().contains(searchItem.toLowerCase())) || string.contains(searchItem)) {
          isContained = true;
          break;
        }
      }
    }
    return isContained;
  }

  /**
   * 去除字符串首尾成对的符号
   * <p>头尾字符相同(单引号等,双引号[半角/全角],大括号[半角/全角],中括号[半角/全角]等)</p>
   *
   * @param string 待处理的字符串
   * @param chars  头尾符号
   * @return 处理后的字符串
   */
  public static String trimFirstAndLastChar(final String string, final String chars) {
    // 首尾符号
    String first = EMPTY;
    String last = EMPTY;
    if (length(chars) == 1) {
      first = chars;
      last = chars;
    } else if (length(chars) == 2) {
      first = chars.substring(0, 1);
      last = chars.substring(1);
    }

    // 字符处理
    if (length(string) < 2 || isBlank(first) || isBlank(last) || !(string.startsWith(first) && string.endsWith(last))) {
      return string;
    } else {
      return string.substring(0, string.lastIndexOf(last)).substring(string.indexOf(first) + 1);
    }
  }

  /**
   * 去除字符串头部字符串
   * <p>匹配 -> 移除;不匹配 -> 原字符串</p>
   *
   * @param string 待处理的字符串
   * @param start  要移除的头部字符串
   * @return 处理后的字符串
   */
  public static String removeStart(final String string, final String start) {
    return isEmpty(string) || isEmpty(start) || !string.startsWith(start) ? string : string.substring(string.indexOf(start) + length(start));
  }

  /**
   * 去除字符串尾部字符串
   * <p>匹配 -> 移除;不匹配 -> 原字符串</p>
   *
   * @param string 待处理的字符串
   * @param end    要移除的尾部字符串
   * @return 处理后的字符串
   */
  public static String removeEnd(final String string, final String end) {
    return isEmpty(string) || isEmpty(end) || !string.endsWith(end) ? string : string.substring(0, string.lastIndexOf(end));
  }

  /**
   * 将字符串中所有半角字符转换为全角字符
   *
   * @param string: 可能包含半角字符的字符串
   * @return 全角字符串
   */
  public static String toFullwidth(final String string) {
    if (isEmpty(string)) return string;

    StringBuilder sb = new StringBuilder(string.length());
    char[] arrays = string.toCharArray();
    for (char item : arrays) {
      if (ASCII_HALFWIDTH_SPACE == item) { // 半角[Space](ASCII): 32
        sb.append(ASCII_FULL_WIDTH_SPACE);
      } else if ((item >= ASCII_HALF_WIDTH_START) && (item <= ASCII_HALF_WIDTH_END)) { // 半角(ASCII): [!](33) ~ [~](126)
        sb.append((char) (item + ASCII_HALF_WITH_FULL_OFFSET));
      } else {
        sb.append(item);
      }
    }
    return sb.toString();
  }

  /**
   * 将字符串中所有全角字符转换为半角字符
   *
   * @param string: 包含全角字符的字符串
   * @return 半角字符串
   */
  public static String toHalfwidth(final String string) {
    if (isEmpty(string)) return string;

    StringBuilder sb = new StringBuilder(string.length());
    char[] arrays = string.toCharArray();
    for (char item : arrays) {
      if (ASCII_FULL_WIDTH_SPACE == item) {
        sb.append(ASCII_HALFWIDTH_SPACE); // 全角[Space](ASCII): 12288
      } else if (item >= ASCII_FULL_WIDTH_START && item <= ASCII_FULL_WIDTH_END) {
        sb.append((char) (item - ASCII_HALF_WITH_FULL_OFFSET)); // 全角(ASCII): [！](65281) ~ [～](65374)
      } else {
        sb.append(item);
      }
    }
    return sb.toString();
  }

  /**
   * 删除Unicode字符
   * <p>[^\u0000-\uFFFF]</p>
   * <p>字符串[NULL(不区分大小写)] -> 空对象</p>
   *
   * @param string 待处理字符串
   * @return 删除Unicode字符后的字符串
   */
  public static String cleanUnicode(final String string) {
    if (notBlank(string) && !CstUtil.DATA_NULL.equalsIgnoreCase(string.trim())) {
      String reString = string.replaceAll(REGEX_UNICODE, EMPTY).replaceAll(REGEX_UNICODE, EMPTY).trim();
      return CstUtil.DATA_NULL.equalsIgnoreCase(reString) ? null : reString;
    } else {
      return CstUtil.DATA_NULL.equalsIgnoreCase(string) ? null : string;
    }
  }

  /**
   * 删除控制字符
   * <p>删除控制字符(ASCII)[0 ~ 31]和DEL(ASCII):[127]</p>
   * <p>字符串[NULL(不区分大小写)] -> 空对象</p>
   *
   * @param string 待处理字符串
   * @return 返回剔除控制字符后的字符串
   */
  public static String cleanControl(final String string) {
    if (notBlank(string) && !CstUtil.DATA_NULL.equalsIgnoreCase(string.trim())) {
      StringBuilder sb = new StringBuilder(EMPTY);
      for (int i = 0; i < string.length(); i++) {
        if (string.charAt(i) > ASCII_CTRL_END && string.charAt(i) != ASCII_DEL) {
          sb.append(string.charAt(i));
        }
      }
      return CstUtil.DATA_NULL.equalsIgnoreCase(sb.toString()) ? null : sb.toString();
    } else {
      return CstUtil.DATA_NULL.equalsIgnoreCase(string) ? null : string;
    }
  }

  /**
   * 清理文本
   * <p>按照以下顺序依次处理</p>
   * <p>1.全角转半角</p>
   * <p>2.删除控制字符(包含DEL)</p>
   * <p>3.删除空格</p>
   * <p>同时,字符串[NULL(不区分大小写)] -> 空对象</p>
   *
   * @param string: 待处理字符串
   * @return 清理后的字符串
   */
  @SuppressWarnings("ALL")
  public static String cleanText(final String string) {
    if (notEmpty(string) && !CstUtil.DATA_NULL.equalsIgnoreCase(string.trim())) {
      String reString = cleanControl(toHalfwidth(string));
      if (notEmpty(reString)) {
        reString = reString.replaceAll(String.valueOf(ASCII_HALFWIDTH_SPACE), EMPTY);
      }
      return CstUtil.DATA_NULL.equalsIgnoreCase(reString) ? null : reString;
    } else {
      return CstUtil.DATA_NULL.equalsIgnoreCase(string) ? null : string;
    }
  }

  /**
   * 获取指定长度的随机字符串
   * <p>abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789</p>
   *
   * @param len 随机字符长度
   * @return 随机字符串
   */
  public static String random(final int len) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < len; i++) {
      sb.append(CANDIDATE_RANDOM.charAt(RANDOM.nextInt(BOUND)));
    }
    return sb.toString();
  }

  /**
   * 根据分割符将列表转换为字符串
   * <p>如果对象列表只有1个元素返回单个元素</p>
   * <p>如果对象列表有多个元素返回按照分隔符号分割的字符串</p>
   *
   * @param list  数据列表对象
   * @param split 分割符号(空对象/空字符串 -> 逗号[,])
   * @param <T>   列表中元素对象类型
   * @return 字符串
   */
  public static <T> String splitList(final List<T> list, final String split) {
    String result = EMPTY;
    String reSplit = isEmpty(split) ? CstUtil.COMMA : split;
    int size = ListUtil.size(list);

    if (size == 1) {
      result = String.valueOf(list.get(0));
    } else if (size > 1) {
      for (int i = 0; i < size; i++) {
        if (i > 0) result = result.concat(reSplit);
        result = result.concat(String.valueOf(list.get(i)));
      }
    }

    return result;
  }

  /**
   * 字符串长度
   *
   * @param cs 被检测字符串
   * @return 字符串长度
   */
  public static int length(final CharSequence cs) {
    return cs == null ? 0 : cs.length();
  }

  /**
   * 强制取出String类型
   * <p>字符串为NULL/空白时,返回EMPTY</p>
   *
   * @param cs 字符串(可能为NULL)
   * @return 字符串
   */
  public static CharSequence force(final CharSequence cs) {
    return isBlank(cs) ? EMPTY : cs;
  }

  /**
   * 检测字符串是否为 空对象/空字符串/空白
   *
   * @param cs 被检测字符串
   * @return 检测结果
   */
  public static boolean isBlank(final CharSequence cs) {
    int strLen = length(cs);
    if (strLen > 0) {
      for (int i = 0; i < strLen; i++) {
        if (!Character.isWhitespace(cs.charAt(i))) return false;
      }
    }
    return true;
  }

  /**
   * 检测字符串是否非 空对象/空字符串/空白
   *
   * @param cs 被检测字符串
   * @return 检测结果
   */
  public static boolean notBlank(final CharSequence cs) {
    return !isBlank(cs);
  }

  /**
   * 检测字符串是否为 空对象/空字符串
   *
   * @param cs 被检测字符串
   * @return 检测结果
   */
  public static boolean isEmpty(final CharSequence cs) {
    return length(cs) == 0;
  }

  /**
   * 检测字符串是否非 空对象/空字符串
   *
   * @param cs 被检测字符串
   * @return 检测结果
   */
  public static boolean notEmpty(final CharSequence cs) {
    return !isEmpty(cs);
  }

}
