package com.github.wanjune.yuu.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
class TimeUtilTest {

  @Test
  void getNowTimeStampMillis() {
    Assertions.assertTrue(TimeUtil.getNowTimeStampMillis() - System.currentTimeMillis() + 2000 > 0L);
    Assertions.assertTrue(TimeUtil.getNowTimeStampMillis() - System.currentTimeMillis() - 2000 < 0L);
  }

  @Test
  void getNowTimeStampSeconds() {
    Assertions.assertTrue(TimeUtil.getNowTimeStampSeconds() - System.currentTimeMillis() / 1000 + 2 > 0L);
    Assertions.assertTrue(TimeUtil.getNowTimeStampSeconds() - System.currentTimeMillis() / 1000 - 2 < 0L);
  }

  @Test
  void getNowDateTimeFormat() {
    log.info(TimeUtil.FMT_DT_FULL_STD + " -> " + TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_FULL_STD));
    Assertions.assertTrue(TimeUtil.isDateTime(TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_FULL_STD), TimeUtil.FMT_DT_FULL_STD));
    log.info(TimeUtil.FMT_DT_FULL_SIMPLE + " -> " + TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_FULL_SIMPLE));
    Assertions.assertTrue(TimeUtil.isDateTime(TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_FULL_SIMPLE), TimeUtil.FMT_DT_FULL_SIMPLE));
    log.info(TimeUtil.FMT_DT_STD + " -> " + TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_STD));
    Assertions.assertTrue(TimeUtil.isDateTime(TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_STD), TimeUtil.FMT_DT_STD));
    log.info(TimeUtil.FMT_DT_SIMPLE + " -> " + TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_SIMPLE));
    Assertions.assertTrue(TimeUtil.isDateTime(TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_SIMPLE), TimeUtil.FMT_DT_SIMPLE));
  }

  @Test
  void getNowDateTime() throws InterruptedException {
    LocalDateTime dtNow = TimeUtil.getNowDateTime();
    TimeUnit.SECONDS.sleep(1);
    Assertions.assertTrue(dtNow.isBefore(LocalDateTime.now()));
  }

  @Test
  void parseDateTime() {
    LocalDateTime dtNow = TimeUtil.getNowDateTime();
    Assertions.assertTrue(TimeUtil.parseDateTime("2022-10-12 16:17:18.222", TimeUtil.FMT_DT_FULL_STD).isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("2023-10-12 17:17:18.222", TimeUtil.FMT_DT_FULL_STD).isAfter(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20221012161718222", TimeUtil.FMT_DT_FULL_SIMPLE).isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20231012171718222", TimeUtil.FMT_DT_FULL_SIMPLE).isAfter(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("2022-10-12 16:17:18", TimeUtil.FMT_DT_STD).isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("2023-10-12 17:17:18", TimeUtil.FMT_DT_STD).isAfter(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20221012161718", TimeUtil.FMT_DT_SIMPLE).isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20231012171718", TimeUtil.FMT_DT_SIMPLE).isAfter(dtNow));
  }

  @Test
  void parseDateTimeGuess() {
    LocalDateTime dtNow = TimeUtil.getNowDateTime();
    Assertions.assertTrue(TimeUtil.parseDateTime("2022-10-12 16:17:18.222").isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("2023-10-12 17:17:18.222").isAfter(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20221012161718222").isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20231012171718222").isAfter(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("2022-10-12 16:17:18").isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("2023-10-12 17:17:18").isAfter(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20221012161718").isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime("20231012171718").isAfter(dtNow));
  }

  @Test
  void parseDateTimeTimeStampMillis() {
    LocalDateTime dtNow = TimeUtil.getNowDateTime();
    Assertions.assertTrue(TimeUtil.parseDateTime(1665562638222L).isBefore(dtNow));
    Assertions.assertTrue(TimeUtil.parseDateTime(1698129975000L).isAfter(dtNow));
  }

  @Test
  void formatDateTime() {
    LocalDateTime dt = TimeUtil.parseDateTime("2022-10-12 16:17:18.222");
    Assertions.assertEquals(TimeUtil.formatDateTime(dt, TimeUtil.FMT_DT_FULL_STD), "2022-10-12 16:17:18.222");
    Assertions.assertEquals(TimeUtil.formatDateTime(dt, TimeUtil.FMT_DT_FULL_SIMPLE), "20221012161718222");
    Assertions.assertEquals(TimeUtil.formatDateTime(dt, TimeUtil.FMT_DT_STD), "2022-10-12 16:17:18");
    Assertions.assertEquals(TimeUtil.formatDateTime(dt, TimeUtil.FMT_DT_SIMPLE), "20221012161718");
  }

  @Test
  void isDateTime() {
    Assertions.assertTrue(TimeUtil.isDateTime("2022-10-12 16:17:18.222", TimeUtil.FMT_DT_FULL_STD));
    Assertions.assertTrue(TimeUtil.isDateTime("20221012161718222", TimeUtil.FMT_DT_FULL_SIMPLE));
    Assertions.assertTrue(TimeUtil.isDateTime("2022-10-12 16:17:18", TimeUtil.FMT_DT_STD));
    Assertions.assertTrue(TimeUtil.isDateTime("20221012161718", TimeUtil.FMT_DT_SIMPLE));
  }

  @Test
  void getNowDateFormat() {
    log.info(TimeUtil.FMT_D_STD + " -> " + TimeUtil.getNowDateFormat(TimeUtil.FMT_D_STD));
    Assertions.assertTrue(TimeUtil.isDate(TimeUtil.getNowDateFormat(TimeUtil.FMT_D_STD), TimeUtil.FMT_D_STD));
    log.info(TimeUtil.FMT_D_SIMPLE + " -> " + TimeUtil.getNowDateFormat(TimeUtil.FMT_D_SIMPLE));
    Assertions.assertTrue(TimeUtil.isDate(TimeUtil.getNowDateFormat(TimeUtil.FMT_D_SIMPLE), TimeUtil.FMT_D_SIMPLE));
  }

  @Test
  void getNowDate() {
    Assertions.assertTrue(TimeUtil.getNowDate().isBefore(LocalDate.now().plusDays(1L)));
    Assertions.assertTrue(TimeUtil.getNowDate().isAfter(LocalDate.now().plusDays(-1L)));
  }

  @Test
  void parseDate() {
    LocalDate dateNow = LocalDate.now();
    Assertions.assertTrue(TimeUtil.parseDate("2022-10-11", TimeUtil.FMT_D_STD).compareTo(dateNow) < 0);
    Assertions.assertEquals(0, TimeUtil.parseDate("20221024", TimeUtil.FMT_D_SIMPLE).compareTo(dateNow));
    Assertions.assertTrue(TimeUtil.parseDate("2023/10/13", "yyyy/MM/dd").compareTo(dateNow) > 0);
  }

  @Test
  void parseDateGuess() {
    LocalDate dateNow = LocalDate.now();
    Assertions.assertTrue(TimeUtil.parseDate("2022-10-11").compareTo(dateNow) < 0);
    Assertions.assertEquals(0, TimeUtil.parseDate("20221024").compareTo(dateNow));
    Assertions.assertTrue(TimeUtil.parseDate("2023/10/13").compareTo(dateNow) > 0);
    Assertions.assertEquals(0, TimeUtil.parseDate("2022年10月24日").compareTo(dateNow));
    Assertions.assertEquals(0, TimeUtil.parseDate("24/10/2022").compareTo(dateNow));
    Assertions.assertEquals(0, TimeUtil.parseDate("10-24-2022").compareTo(dateNow));
  }

  @Test
  void formatDate() {
    LocalDate date = TimeUtil.parseDate("2022-10-12");
    Assertions.assertEquals(TimeUtil.formatDate(date, TimeUtil.FMT_D_STD), "2022-10-12");
    Assertions.assertEquals(TimeUtil.formatDate(date, TimeUtil.FMT_D_SIMPLE), "20221012");
    Assertions.assertEquals(TimeUtil.formatDate(date, "yyyy/MM/dd"), "2022/10/12");
  }

  @Test
  void isDate() {
    Assertions.assertTrue(TimeUtil.isDate("2022-10-11", TimeUtil.FMT_D_STD));
    Assertions.assertTrue(TimeUtil.isDate("20221012", TimeUtil.FMT_D_SIMPLE));
    Assertions.assertTrue(TimeUtil.isDate("2022/10/13", "yyyy/MM/dd"));
  }

  @Test
  void getQuarterFormat() {
    LocalDate date = TimeUtil.parseDate("2022-10-12");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, TimeUtil.FMT_QR_STD), "2022Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, TimeUtil.FMT_QR_SIMPLE), "2022-4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyy"), "2022");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "QR"), "Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "qr"), "4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "tt"), "2022Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, null), "2022Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, StringUtil.EMPTY), "2022Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyy-QR"), "2022-Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyy_QR"), "2022_Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyy/QR"), "2022/Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyyqr"), "20224");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyy_qr"), "2022_4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyy年QR"), "2022年Q4");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "yyyy年第qr季度"), "2022年第4季度");
    Assertions.assertEquals(TimeUtil.getQuarterFormat(date, "年:yyyy 季度:qr"), "年:2022 季度:4");
  }
}
