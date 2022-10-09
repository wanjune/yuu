package com.github.wanjune.yuu.base.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
      "dd/MM/yyyy", "dd MMMM yyyy", "MM-dd-yyyy");
  /**
   * 日时格式-列表
   */
  private static final List<String> FMT_DT_LIST = Arrays.asList("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyyMMddHHmmss",
      "yyyy-MM-dd HH:mm:ss.SSS", "yyyyMMddHHmmssSSS", "dd-M-yyyy hh:mm:ss");

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
   * @param dtFormat 日期格式
   * @return 当前日时字符串
   */
  public static String getDateTimeNowFormat(String dtFormat) {
    return formatDateTime(getDateTimeNow(), dtFormat);
  }

  /**
   * 获取系统当前日时
   *
   * @return 系统当前日时
   */
  public static LocalDateTime getDateTimeNow() {
    return LocalDateTime.now();
  }

  /**
   * 转换为是日时对象
   *
   * @param strDateTime 日时字符串
   * @param dtFormat    日时格式
   * @return 指定格式的日时字符串
   */
  public static LocalDateTime parseDateTime(String strDateTime, String dtFormat) {
    try {
      DateTimeFormatter dtFormatter;
      if (dtFormat.endsWith(".SSS")) {
        dtFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace(".SSS", StringUtil.EMPTY)).appendLiteral(".").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else if (dtFormat.endsWith("SSS")) {
        dtFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace("SSS", StringUtil.EMPTY)).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
      } else {
        dtFormatter = DateTimeFormatter.ofPattern(dtFormat);
      }
      return LocalDateTime.parse(strDateTime, dtFormatter);
    } catch (Exception ex) {
      // Nothing
    }
    return null;
  }

  /**
   * 转换为是日时对象
   * <p>尝试使用FMT_DT_LIST列表中格式转换</p>
   *
   * @param strDateTime 日时字符串
   * @return 指定格式的日时字符串
   */
  public static LocalDateTime parseDateTime(String strDateTime) {
    LocalDateTime dateTime = null;
    for (String dtFormat : FMT_DT_LIST) {
      try {
        dateTime = parseDateTime(strDateTime, dtFormat);
        if (dateTime != null) {
          break;
        }
      } catch (Exception ex) {
        // NOTHING
      }
    }
    return dateTime;
  }

  /**
   * 将Timestamp转换为日时对象
   *
   * @param longTimestamp Timestamp毫秒
   * @return 日时对象
   */
  public static LocalDateTime parseDateTime(Long longTimestamp) {
    try {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(longTimestamp), ZoneId.systemDefault());
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * 根据日时输出指定格式的字符串
   *
   * @param inLocalDateTime 指定日时
   * @param dtFormat        日时格式
   * @return 指定格式的日时字符串
   */
  public static String formatDateTime(LocalDateTime inLocalDateTime, String dtFormat) {
    DateTimeFormatter dtFormatter;
    if (dtFormat.endsWith(".SSS")) {
      dtFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace(".SSS", StringUtil.EMPTY)).appendLiteral(".").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
    } else if (dtFormat.endsWith("SSS")) {
      dtFormatter = new DateTimeFormatterBuilder().appendPattern(dtFormat.replace("SSS", StringUtil.EMPTY)).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
    } else {
      dtFormatter = DateTimeFormatter.ofPattern(dtFormat);
    }
    return inLocalDateTime.format(dtFormatter);
  }

  /**
   * 是合法的日时
   *
   * @param strDateTime 日时字符串
   * @param dtFormat    日时格式
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
   * @param dtFormat 日时格式
   * @return 当前日期的字符串
   */
  public static String getNowDateFormat(String dtFormat) {
    return formatDate(getNowDate(), dtFormat);
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
   * 获取系统当前日时的格式化字符串
   *
   * @param dtFormat 日时格式
   * @return 当前日期字符串
   */
  public static String getNowDateTimeFormat(String dtFormat) {
    return formatDateTime(getNowDateTime(), dtFormat);
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
   * 转换为日期对象
   * <p>尝试使用FMT_D_LIST列表中格式转换</p>
   *
   * @param strDate 日期字符串
   * @return 日期
   */
  public static LocalDate parseDate(String strDate) {
    LocalDate date = null;
    for (String dtFormat : FMT_D_LIST) {
      try {
        date = parseDate(strDate, dtFormat);
        if (date != null) {
          break;
        }
      } catch (Exception ex) {
        // NOTHING
      }
    }
    return date;
  }

  /**
   * 根据日期输出指定格式的字符串
   *
   * @param inLocalDate 指定日期
   * @param dtFormat    日期格式
   * @return 指定格式的日期字符串
   */
  public static String formatDate(LocalDate inLocalDate, String dtFormat) {
    return inLocalDate.format(DateTimeFormatter.ofPattern(dtFormat));
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
   * 获取季度格式化字符串
   * <p>
   * "tt" -- 2019Q1
   * "" -- 2019Q1
   * null -- 2019Q1
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
   * @param inLocalDate 指定日期
   * @param qrFormat    季度格式
   * @return 指定格式的季度字符串
   */
  @SuppressWarnings("ALL")
  public static String getQuarterFormat(LocalDate inLocalDate, String qrFormat) {

    if (inLocalDate == null) {
      return StringUtil.EMPTY;
    }

    String strQrFormat = qrFormat;
    if (StringUtil.isEmpty(qrFormat) || !StringUtil.isContains(strQrFormat, ListUtil.asList("yyyy", "qr"), true)) {
      strQrFormat = FMT_QR_STD;
    }

    String year = String.valueOf(inLocalDate.getYear());
    int month = inLocalDate.getMonthValue();

    String quarter;
    if (month < 4) {
      quarter = "1";
    } else if (month < 7) {
      quarter = "2";
    } else if (month < 10) {
      quarter = "3";
    } else {
      quarter = "4";
    }

    return strQrFormat.replace("QR", "Q".concat(quarter)).replace("qr", quarter).toLowerCase().replace("yyyy", year);
  }

}
