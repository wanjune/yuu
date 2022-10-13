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

  public static final String NULL = "null";
  public static final String EMPTY = "";
  public static final String SPACE = " ";

  // 随机字符串 - 变量
  private static final String CANDIDATE_RANDOM_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
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
  private static final int ASCII_HALF_TO_FULL_OFFSET = 65248;

  /**
   * 获取实际字符串(带参数的字符串以[{}]变量形式的参数，变为实际字符）
   *
   * @param strVar   带参数变量的字符串
   * @param objParam 参数对象(Map类型)
   * @return 真实字符串
   */
  public static String instance(final String strVar, final Map<String, Object> objParam) {
    String strResult = strVar;
    for (String itemKey : objParam.keySet()) {
      strResult = strResult.replaceAll(CstUtil.CURLY_BRACKET_PREFIX + itemKey + CstUtil.CURLY_BRACKET_SUFFIX, objParam.get(itemKey).toString());
    }
    return strResult;
  }

  /**
   * 去除字符串首尾成对的符号
   * <p>头尾字符相同(双引号、单引号等)</p>
   *
   * @param strValue 待处理的字符串
   * @param strChar  头尾符号
   * @return 处理后的字符串
   */
  public static String trimFirstAndLastChar(final String strValue, final String strChar) {
    if (isEmpty(strValue) || length(strValue) < 2 || length(strChar) == 0 || length(strChar) > 2) {
      return strValue;
    }

    String strCharFirst = EMPTY;
    String strCharLast = EMPTY;
    if (length(strChar) == 1) {
      strCharFirst = strChar;
      strCharLast = strChar;
    } else if (length(strChar) == 2) {
      strCharFirst = strChar.substring(0, 1);
      strCharLast = strChar.substring(1);
    }

    if (isBlank(strCharFirst) || isBlank(strCharLast) || !(strValue.startsWith(strCharFirst) && strValue.endsWith(strCharLast))) {
      return strValue;
    } else {
      return strValue.substring(0, strValue.lastIndexOf(strChar)).substring(strValue.indexOf(strChar) + 1);
    }
  }

  /**
   * 字符串是否包含特定的字符串
   *
   * @param strValue   待判断的字符串
   * @param searchList 检索包含的对象列表
   * @param isIgnore   是否忽略大小写
   * @return 判断结果
   */
  public static boolean isContains(final String strValue, final List<String> searchList, boolean isIgnore) {
    boolean isContained = false;
    if (isNotBlank(strValue) && ListUtil.nonEmpty(searchList)) {
      for (String searchItem : searchList) {
        if ((isIgnore && strValue.toLowerCase().contains(searchItem.toLowerCase())) || strValue.contains(searchItem)) {
          isContained = true;
          break;
        }
      }
    }
    return isContained;
  }


  /**
   * 将字符串中所有半角字符转换为全角字符
   *
   * @param characters: 包含半角字符的字符串
   * @return 全角字符串
   */
  public static String toFullwidth(String characters) {
    if (isEmpty(characters)) {
      return characters;
    }
    StringBuilder sbBuffer = new StringBuilder(characters.length());
    char[] charItemArray = characters.toCharArray();
    for (char charItem : charItemArray) {
      if (ASCII_HALFWIDTH_SPACE == charItem) {
        // 半角[Space](ASCII): 32
        sbBuffer.append(ASCII_FULL_WIDTH_SPACE);
      } else if ((charItem >= ASCII_HALF_WIDTH_START) && (charItem <= ASCII_HALF_WIDTH_END)) {
        // 半角(ASCII): [!](33) ~ [~](126)
        sbBuffer.append((char) (charItem + ASCII_HALF_TO_FULL_OFFSET));
      } else {
        // Others
        sbBuffer.append(charItem);
      }
    }
    return sbBuffer.toString();
  }

  /**
   * 将字符串中所有全角字符转换为半角字符
   *
   * @param characters: 包含全角字符的字符串
   * @return 半角字符串
   */
  public static String toHalfwidth(String characters) {
    if (isEmpty(characters)) {
      return characters;
    }
    StringBuilder sbBuffer = new StringBuilder(characters.length());
    char[] charItemArray = characters.toCharArray();
    for (char charItem : charItemArray) {
      if (ASCII_FULL_WIDTH_SPACE == charItem) {
        // 全角[Space](ASCII): 12288
        sbBuffer.append(ASCII_HALFWIDTH_SPACE);
      } else if (charItem >= ASCII_FULL_WIDTH_START && charItem <= ASCII_FULL_WIDTH_END) {
        // 全角(ASCII): [！](65281) ~ [～](65374)
        sbBuffer.append((char) (charItem - ASCII_HALF_TO_FULL_OFFSET));
      } else {
        sbBuffer.append(charItem);
      }
    }
    return sbBuffer.toString();
  }

  /**
   * 删除Unicode字符
   * <p>正则表达式[^\u0000-\uFFFF]</p>
   *
   * @param strValue 待处理字符串
   * @return 返回剔除Unicode字符后的字符串
   */
  public static String cleanUnicode(final String strValue) {
    if (isNotBlank(strValue) && !NULL.equalsIgnoreCase(strValue.trim())) {
      return strValue.replaceAll(REGEX_UNICODE, EMPTY).replaceAll(REGEX_UNICODE, EMPTY).trim();
    } else {
      return NULL.equalsIgnoreCase(strValue) ? null : strValue;
    }
  }

  /**
   * 删除控制字符
   * <p>删除控制字符(ASCII)[0 ~ 31]和DEL(ASCII):[127]</p>
   *
   * @param strValue 待处理字符串
   * @return 返回剔除控制字符后的字符串
   */
  public static String cleanControl(final String strValue) {
    if (isNotBlank(strValue) && !NULL.equalsIgnoreCase(strValue.trim())) {
      StringBuilder sbResult = new StringBuilder(EMPTY);
      for (int i = 0; i < strValue.length(); i++) {
        if (strValue.charAt(i) > ASCII_CTRL_END && strValue.charAt(i) != ASCII_DEL) {
          sbResult.append(strValue.charAt(i));
        }
      }
      return NULL.equalsIgnoreCase(sbResult.toString()) ? null : sbResult.toString();
    }
    return strValue;
  }

  /**
   * 清理文本
   * <p>1.全角转半角;2.删除控制字符(包含DEL);3.删除空格</p>
   *
   * @param strValue: 待处理字符串
   * @return 清理后的字符串
   */
  @SuppressWarnings("ALL")
  public static String cleanText(final String strValue) {
    if (isNotEmpty(strValue)) {
      String strText = cleanControl(toHalfwidth(strValue));
      if (isNotEmpty(strText)) {
        strText = cleanControl(toHalfwidth(strValue)).replaceAll(String.valueOf(ASCII_HALFWIDTH_SPACE), EMPTY);
      }
      return NULL.equalsIgnoreCase(strText) ? null : strText;
    }
    return strValue;
  }


  /**
   * 强制取出String类型
   * </p>解决字符串为NULL时,返回EMPTY
   *
   * @param strValue 字符串(可能为NULL)
   * @return 字符串
   */
  public static String force(final String strValue) {
    return isBlank(strValue) ? EMPTY : strValue;
  }

  /**
   * 获取指定长度的随机字符串
   *
   * @param len 随机字符长度
   * @return 随机字符串
   */
  public static String getRandomString(final int len) {
    StringBuilder strBud = new StringBuilder();
    for (int i = 0; i < len; i++) {
      strBud.append(CANDIDATE_RANDOM_STR.charAt(RANDOM.nextInt(BOUND)));
    }
    return strBud.toString();
  }

  /**
   * 获取字符串元素(默认逗号分割)
   * <p>如果对象列表只有1个元素返回单个元素</p>
   * <p>如果对象列表有多个元素返回按照分隔符号分割的字符串</p>
   *
   * @param listObj 数据列表对象
   * @param <T>     列表中元素对象类型
   * @return 字符串
   */
  public static <T> String getSplitString(List<T> listObj) {
    return getSplitString(listObj, CstUtil.COMMA);
  }

  /**
   * 获取字符串元素
   * <p>如果对象列表只有1个元素返回单个元素</p>
   * <p>如果对象列表有多个元素返回按照分隔符号分割的字符串</p>
   *
   * @param listObj 数据列表对象
   * @param split   分割符号
   * @param <T>     列表中元素对象类型
   * @return 字符串
   */
  public static <T> String getSplitString(List<T> listObj, String split) {
    String strResult = EMPTY;
    if (ListUtil.nonEmpty(listObj)) {
      if (listObj.size() == 1) {
        return String.valueOf(listObj.get(0));
      }
      for (int i = 0; i < listObj.size(); i++) {
        if (i > 0) {
          strResult = strResult.concat(split);
        }
        strResult = strResult.concat(String.valueOf(listObj.get(i)));
      }
    }
    return strResult;
  }

  /**
   * 检测字符串是否为 null/空/空白
   *
   * @param cs 被检测字符串
   * @return true:null/空/空白
   */
  public static boolean isBlank(final CharSequence cs) {
    final int strLen = length(cs);
    if (strLen == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * 检测字符串是否不是null/空/空白
   *
   * @param cs 被检测字符串
   * @return false:字符串为null/空/空白
   */
  public static boolean isNotBlank(final CharSequence cs) {
    return !isBlank(cs);
  }

  /**
   * 检测字符串是否为 null/空
   *
   * @param cs 被检测字符串
   * @return true: 字符串为null/空
   */
  public static boolean isEmpty(final CharSequence cs) {
    return length(cs) == 0;
  }

  /**
   * 检测字符串是否不为 null/空
   *
   * @param cs 被检测字符串
   * @return false: 字符串为null/空
   */
  public static boolean isNotEmpty(final CharSequence cs) {
    return !isEmpty(cs);
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

}
