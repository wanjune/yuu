package com.github.wanjune.yuu.base.util;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;

/**
 * Date和DateTime工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
public class DateTimeUtil {

  /**
   * 日期格式-标准
   */
  public static final String DF_STANDARD = "yyyy-MM-dd";

  /**
   * 日期简格式-简单
   */
  public static final String DF_SIMPLE = "yyyyMMdd";

  /**
   * 时间格式-标准
   */
  public static final String DTF_STANDARD = "yyyy-MM-dd HH:mm:ss";

  /**
   * 时间格式-简单
   */
  public static final String DTF_SIMPLE = "yyyyMMddHHmmss";

  /**
   * 时间格式-完整-标准
   */
  public static final String DTF_FULL_STANDARD = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * 时间格式-完整-简单
   */
  public static final String DTF_FULL_SIMPLE = "yyyyMMddHHmmssSSS";

  /**
   * 获取当前时间的UnixTimeStamp(毫秒)
   *
   * @return 当前时间的UnixTimeStamp
   */
  public static Long getNowTimeStampMillis() {
    return System.currentTimeMillis();
  }

  /**
   * 获取当前时间的UnixTimeStamp(秒)
   *
   * @return 当前时间的UnixTimeStamp
   */
  public static Long getNowTimeStampSeconds() {
    return getNowTimeStampMillis() / 1000;
  }

  /**
   * 转换为日期对象
   *
   * @param strDate  日期字符串
   * @param dtFormat 日期格式
   * @return 日期
   */
  public static LocalDate parseDate(String strDate, String dtFormat) {
    try {
      return LocalDate.parse(strDate, DateTimeFormatter.ofPattern(dtFormat));
    } catch (Exception ex) {
      // Nothing
    }
    return null;
  }

  /**
   * 获取系统当前日期的格式化字符串
   *
   * @return 当前日期字符串
   */
  public static String getDateTimeNowFormat(String dtFormat) {
    return formatDateTime(getDateTimeNow(), dtFormat);
  }

  /**
   * 获取系统当前日期
   *
   * @return 系统当前日期
   */
  public static LocalDateTime getDateTimeNow() {
    return LocalDateTime.now();
  }

  /**
   * 转换为是时间对象
   *
   * @param strDateTime 时间字符串
   * @param dtFormat    时间格式
   * @return 日期
   */
  public static LocalDateTime parseDateTime(String strDateTime, String dtFormat) {
    try {
      DateTimeFormatter dateTimeFormatter;
      if (dtFormat.endsWith(".SSS")) {
        dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace(".SSS", StringUtil.EMPTY)).appendLiteral(".").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else if (dtFormat.endsWith("SSS")) {
        dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace("SSS", StringUtil.EMPTY)).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else {
        dateTimeFormatter = DateTimeFormatter.ofPattern(dtFormat);
      }
      return LocalDateTime.parse(strDateTime, dateTimeFormatter);
    } catch (Exception ex) {
      // Nothing
    }
    return null;
  }

  /**
   * 获取系统当前日期
   *
   * @return 系统当前日期
   */
  public static LocalDate getDateNow() {
    return LocalDate.now();
  }

  /**
   * 根据日期输出指定格式的字符串
   *
   * @param inLocalDate 指定日期
   * @param dtFormat    指定格式
   * @return 指定格式的日期字符串
   */
  public static String formatDate(LocalDate inLocalDate, String dtFormat) {
    return inLocalDate.format(DateTimeFormatter.ofPattern(dtFormat));
  }

  /**
   * 根据时间输出指定格式的字符串
   *
   * @param inLocalDateTime 指定时间
   * @param dtFormat        指定格式
   * @return 指定格式的日期字符串
   */
  public static String formatDateTime(LocalDateTime inLocalDateTime, String dtFormat) {
    DateTimeFormatter dateTimeFormatter;
    if (dtFormat.endsWith(".SSS")) {
      dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace(".SSS", StringUtil.EMPTY)).appendLiteral(".").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
    } else if (dtFormat.endsWith("SSS")) {
      dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace("SSS", StringUtil.EMPTY)).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
    } else {
      dateTimeFormatter = DateTimeFormatter.ofPattern(dtFormat);
    }
    return inLocalDateTime.format(dateTimeFormatter);
  }

  /**
   * 是合法的日期
   *
   * @param strDate  日期字符串
   * @param dtFormat 日期格式
   * @return 验证结果
   */
  public static boolean isDate(String strDate, String dtFormat) {
    return Objects.nonNull(parseDate(strDate, dtFormat));
  }

  /**
   * 是合法的时间
   *
   * @param strDateTime 时间字符串
   * @param dtFormat    时间格式
   * @return 验证结果
   */
  public static boolean isDateTime(String strDateTime, String dtFormat) {
    return Objects.nonNull(parseDateTime(strDateTime, dtFormat));
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
   * 获取系统当前日时
   *
   * @return 系统当前日时
   */
  public static LocalDateTime getNowDateTime() {
    return LocalDateTime.now();
  }

  /**
   * 获取系统当前日期的格式化字符串
   *
   * @return 当前日期的字符串
   */
  public static String getNowDateFormat(String dtFormat) {
    return formatDate(getNowDate(), dtFormat);
  }

  /**
   * 获取系统当前日期的格式化字符串
   *
   * @return 当前日期字符串
   */
  public static String getNowDateTimeFormat(String dtFormat) {
    return formatDateTime(getNowDateTime(), dtFormat);
  }


  /**
   * 将Timestamp转换为时间对象
   *
   * @param longTimestamp Timestamp毫秒
   * @return 时间对象
   */
  public static LocalDateTime parseDateTime(Long longTimestamp) {
    try {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(longTimestamp), ZoneId.systemDefault());
    } catch (Exception ex) {
      return null;
    }
  }

}
