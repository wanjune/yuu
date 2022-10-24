package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.exception.YuuException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

/**
 * 日期和时间工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class TimeUtil {

  /**
   * 日期格式-标准
   */
  public static final String FMT_D_STD = "yyyy-MM-dd";

  /**
   * 日期简格式-简单
   */
  public static final String FMT_D_SIMPLE = "yyyyMMdd";

  /**
   * 日时格式-标准
   */
  public static final String FMT_DT_STD = "yyyy-MM-dd HH:mm:ss";

  /**
   * 日时格式-简单
   */
  public static final String FMT_DT_SIMPLE = "yyyyMMddHHmmss";

  /**
   * 日时格式-完整-标准
   */
  public static final String FMT_DT_FULL_STD = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * 日时格式-完整-简单
   */
  public static final String FMT_DT_FULL_SIMPLE = "yyyyMMddHHmmssSSS";
  /**
   * 季度格式-标准
   */
  public static final String FMT_QR_STD = "yyyyQR";
  /**
   * 季度格式-简单
   */
  public static final String FMT_QR_SIMPLE = "yyyy-qr";
  /**
   * 日期格式-列表
   */
  private static final List<String> FMT_D_LIST = Arrays.asList("yyyy-MM-dd", "yyyyMMdd", "yyyy年MM月dd日", "yyyy/MM/dd",
      "dd/MM/yyyy", "MM-dd-yyyy");
  /**
   * 日时格式-列表
   */
  private static final List<String> FMT_DT_LIST = Arrays.asList("yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss",
      "yyyy-MM-dd HH:mm:ss.SSS", "yyyyMMddHHmmssSSS", "dd-M-yyyy hh:mm:ss", "yyyy-MM-dd HH:mm");

  /**
   * 获取当前日时的UnixTimeStamp(毫秒)
   *
   * @return 当前日时的UnixTimeStamp
   */
  public static Long getNowTimeStampMillis() {
    return System.currentTimeMillis();
  }

  /**
   * 获取当前日时的UnixTimeStamp(秒)
   *
   * @return 当前日时的UnixTimeStamp
   */
  public static Long getNowTimeStampSeconds() {
    return getNowTimeStampMillis() / 1000;
  }

  /**
   * 获取系统当前日时的格式化字符串
   *
   * @param format 日期格式
   * @return 当前日时字符串
   */
  public static String getNowDateTimeFormat(final String format) {
    return formatDateTime(getNowDateTime(), format);
  }

  /**
   * 获取系统当前日时
   *
   * @return 系统当前日时
   */
  public static LocalDateTime getNowDateTime() {
    return LocalDateTime.now();
  }

  /**
   * 转换为日时对象
   *
   * @param dateTimeString 日时字符串
   * @param format         日时格式
   * @return 指定格式的日时字符串
   */
  public static LocalDateTime parseDateTime(final String dateTimeString, final String format) {
    try {
      DateTimeFormatter formatter;
      if (format.endsWith(".SSS")) {
        formatter = new DateTimeFormatterBuilder().appendPattern(format.replace(".SSS", StringUtil.EMPTY)).appendLiteral(".").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else if (format.endsWith("SSS")) {
        formatter = new DateTimeFormatterBuilder().appendPattern(format.replace("SSS", StringUtil.EMPTY)).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else {
        formatter = DateTimeFormatter.ofPattern(format);
      }
      return LocalDateTime.parse(dateTimeString, formatter);
    } catch (Exception ex) {
      throw new YuuException(String.format("[%s]转换为日时对象失败", dateTimeString), ex);
    }
  }

  /**
   * 转换是日时对象
   * <p>尝试使用FMT_DT_LIST列表中格式转换</p>
   *
   * @param dateTimeString 日时字符串
   * @return 指定格式的日时字符串
   */
  public static LocalDateTime parseDateTime(final String dateTimeString) {
    LocalDateTime dateTime = null;
    for (String format : FMT_DT_LIST) {
      try {
        dateTime = parseDateTime(dateTimeString, format);
        break;
      } catch (Exception ex) {
        // NOTHING
      }
    }

    if (dateTime != null) return dateTime;
    throw new YuuException(String.format("[%s]转换为日时对象失败", dateTimeString));
  }

  /**
   * 转换为日时对象
   *
   * @param timeStampMillis Timestamp毫秒
   * @return 日时对象
   */
  public static LocalDateTime parseDateTime(final Long timeStampMillis) {
    try {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStampMillis), ZoneId.systemDefault());
    } catch (Exception ex) {
      throw new YuuException(String.format("[%s]转换为日时对象失败", timeStampMillis));
    }
  }

  /**
   * 取得指定日时格式的字符串
   *
   * @param dateTime 指定日时
   * @param format   日时格式
   * @return 指定日时格式的字符串
   */
  public static String formatDateTime(final LocalDateTime dateTime, final String format) {
    try {
      DateTimeFormatter formatter;
      if (format.endsWith(".SSS")) {
        formatter = new DateTimeFormatterBuilder().appendPattern(format.replace(".SSS", StringUtil.EMPTY)).appendLiteral(".").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else if (format.endsWith("SSS")) {
        formatter = new DateTimeFormatterBuilder().appendPattern(format.replace("SSS", StringUtil.EMPTY)).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else {
        formatter = DateTimeFormatter.ofPattern(format);
      }
      return dateTime.format(formatter);
    } catch (Exception ex) {
      throw new YuuException(String.format("取得[%s]指定日时格式[%s]的字符串失败", dateTime, format));
    }
  }

  /**
   * 是否是合法的日时
   *
   * @param dateTimeString 日时字符串
   * @param format         日时格式
   * @return 验证结果
   */
  public static boolean isDateTime(final String dateTimeString, final String format) {
    try {
      parseDateTime(dateTimeString, format);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * 获取系统当前日期的格式化字符串
   *
   * @param format 日时格式
   * @return 当前日期的字符串
   */
  public static String getNowDateFormat(final String format) {
    return formatDate(getNowDate(), format);
  }

  /**
   * 获取系统当前日期
   *
   * @return 系统当前日期
   */
  public static LocalDate getNowDate() {
    return LocalDate.now();
  }

  /**
   * 转换为日期对象
   *
   * @param dateString 日期字符串
   * @param format     日期格式
   * @return 日期
   */
  public static LocalDate parseDate(final String dateString, final String format) {
    try {
      return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
    } catch (Exception ex) {
      throw new YuuException(String.format("[%s]转换为日期对象", dateString));
    }
  }

  /**
   * 转换为日期对象
   * <p>尝试使用FMT_D_LIST列表中格式转换</p>
   *
   * @param dateString 日期字符串
   * @return 日期
   */
  public static LocalDate parseDate(final String dateString) {
    LocalDate date = null;
    for (String format : FMT_D_LIST) {
      try {
        date = parseDate(dateString, format);
        break;
      } catch (Exception ex) {
        // NOTHING
      }
    }

    if (date != null) return date;
    throw new YuuException(String.format("[%s]转换为日期对象失败", dateString));
  }

  /**
   * 取得指定日期格式的字符串
   *
   * @param date   指定日期
   * @param format 日期格式
   * @return 指定格式的日期字符串
   */
  public static String formatDate(final LocalDate date, final String format) {
    try {
      return date.format(DateTimeFormatter.ofPattern(format));
    } catch (Exception ex) {
      throw new YuuException(String.format("取得[%s]指定日期格式[%s]的字符串失败", date, format));
    }
  }

  /**
   * 是合法的日期
   *
   * @param dateString 日期字符串
   * @param format     日期格式
   * @return 验证结果
   */
  public static boolean isDate(final String dateString, final String format) {
    try {
      parseDate(dateString, format);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * 获取季度格式化字符串
   * <p>
   * "tt" (非法) -- 2019Q1
   * "" (空字符串) -- 2019Q1
   * null (空) -- 2019Q1
   * "yyyyQR" -- 2019Q1
   * "yyyy-QR" -- 2019-Q1
   * "yyyy_QR" -- 2019_Q2
   * "yyyy/QR" -- 2019/Q2
   * "yyyyqr" -- 20193
   * "yyyy-qr" -- 2019-4
   * "yyyy_qr" -- 2019_4
   * "yyyy" -- 2019
   * "qr" -- 2
   * "yyyy年QR" -- 2018年Q2
   * "yyyy年第qr季度" -- 2018年第2季度
   * "年:yyyy 季度:qr" -- 年:2018 季度:2
   * </p>
   *
   * @param date     指定日期
   * @param qrFormat 季度格式
   * @return 指定格式的季度字符串
   */
  @SuppressWarnings("ALL")
  public static String getQuarterFormat(final LocalDate date, final String qrFormat) {
    // 日期为空 -> null
    if (date == null) return StringUtil.EMPTY;
    // 非法季度格式 -> 标准格式
    String reQrFormat = StringUtil.notEmpty(qrFormat) && StringUtil.isContains(qrFormat, ListUtil.asList("yyyy", "qr"), true) ? qrFormat : FMT_QR_STD;

    // 计算季度
    int month = date.getMonthValue();
    String quarter = month < 4 ? "1" : (month < 7 ? "2" : (month < 10 ? "3" : "4"));

    return reQrFormat.replace("yyyy", String.valueOf(date.getYear())).replace("QR", "Q".concat(quarter)).replace("qr", quarter);
  }

}
