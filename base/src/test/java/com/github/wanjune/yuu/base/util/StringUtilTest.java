package com.github.wanjune.yuu.base.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@Slf4j
class StringUtilTest {

  @Test
  void length() {
    Assertions.assertEquals(StringUtil.length(null), 0);
    Assertions.assertEquals(StringUtil.length(""), 0);
    Assertions.assertEquals(StringUtil.length("abc"), 3);
  }

  @Test
  void force() {
    Assertions.assertEquals(StringUtil.force(null), "");
    Assertions.assertEquals(StringUtil.force(""), "");
    Assertions.assertEquals(StringUtil.force("abc"), "abc");
  }

  @Test
  void instance() {
    Assertions.assertEquals(StringUtil.instance("abc{test}efg", MapUtil.of("test", "测试1")), "abc测试1efg");
    Assertions.assertEquals(StringUtil.instance("abc{test1}e{test2}f{test1}g", MapUtil.of("test1", "测试1", "test2", "测试2")), "abc测试1e测试2f测试1g");
  }

  @Test
  void isContains() {
    String str = "A template for executing high-level operations. When used with a DefaultKafkaProducerFactory , the template is thread-safe. The producer factory and";
    Assertions.assertTrue(StringUtil.isContains(str, ListUtil.asList("DEFAULTKAFKAPRODUCERFACTORY"), true));
    Assertions.assertFalse(StringUtil.isContains(str, ListUtil.asList("DEFAULTKAFKAPRODUCERFACTORY"), false));
    Assertions.assertTrue(StringUtil.isContains(str, ListUtil.asList("aaa", "DEFAULTKAFKAPRODUCERFACTORY"), true));
    Assertions.assertFalse(StringUtil.isContains(str, ListUtil.asList("aaa", "DEFAULTKAFKAPRODUCERFACTORY"), false));
    Assertions.assertFalse(StringUtil.isContains(str, new ArrayList<>(), true));
    Assertions.assertFalse(StringUtil.isContains(str, new ArrayList<>(), false));
  }

  @Test
  void trimFirstAndLastChar() {
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("'测试数据'", "'"), "测试数据");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("\"测试数据\"", "\""), "测试数据");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("{测试数据}", "{}"), "测试数据");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("[测试数据]", "[]"), "测试数据");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("【测试数据】", "【】"), "测试数据");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("「测试数据」", "「」"), "测试数据");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("''测试数据''", "'"), "'测试数据'");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("\"\"测试数据\"\"", "\""), "\"测试数据\"");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("{{测试数据}}", "{}"), "{测试数据}");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("[[测试数据]]", "[]"), "[测试数据]");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("【【测试数据】】", "【】"), "【测试数据】");
    Assertions.assertEquals(StringUtil.trimFirstAndLastChar("「「测试数据」」", "「」"), "「测试数据」");
  }

  @Test
  void toFullwidth() {
    Assertions.assertNull(StringUtil.toFullwidth(null));
    Assertions.assertEquals(StringUtil.toFullwidth(""), "");
    Assertions.assertEquals(StringUtil.toFullwidth("13123456789"), "１３１２３４５６７８９");
    Assertions.assertEquals(StringUtil.toFullwidth("1３123４56789"), "１３１２３４５６７８９");
    Assertions.assertEquals(StringUtil.toFullwidth("1３1 23４ 5A　6 b7ｃ8　9"), "１３１　２３４　５Ａ　６　ｂ７ｃ８　９");
  }

  @Test
  void toHalfwidth() {
    Assertions.assertNull(StringUtil.toHalfwidth(null));
    Assertions.assertEquals(StringUtil.toHalfwidth(""), "");
    Assertions.assertEquals(StringUtil.toHalfwidth("１３１２３４５６７８９"), "13123456789");
    Assertions.assertEquals(StringUtil.toHalfwidth("１3１２３４５６7８9"), "13123456789");
    Assertions.assertEquals(StringUtil.toHalfwidth("１　3１A３ｂ５６ 7！８　9"), "1 31A3b56 7!8 9");
  }

  @Test
  void cleanUnicode() {
    Assertions.assertNull(StringUtil.cleanUnicode(null));
    Assertions.assertNull(StringUtil.cleanUnicode("null"));
    Assertions.assertNull(StringUtil.cleanUnicode("NULL"));
    Assertions.assertEquals(StringUtil.cleanUnicode(""), "");
    Assertions.assertEquals(StringUtil.cleanUnicode("测试1\uD83D\uDE09"), "测试1");
    Assertions.assertEquals(StringUtil.cleanUnicode("测试2\uD83D\uDE03"), "测试2");
  }

  @Test
  void cleanControl() {
    Assertions.assertNull(StringUtil.cleanControl(null));
    Assertions.assertNull(StringUtil.cleanControl("null"));
    Assertions.assertNull(StringUtil.cleanControl("NULL"));
    Assertions.assertEquals(StringUtil.cleanControl(""), "");
    Assertions.assertEquals(StringUtil.cleanControl("测试1\001"), "测试1");
    Assertions.assertEquals(StringUtil.cleanControl("测\002试\0032"), "测试2");
  }

  @Test
  void cleanText() {
    Assertions.assertNull(StringUtil.cleanText(null));
    Assertions.assertNull(StringUtil.cleanText("null"));
    Assertions.assertNull(StringUtil.cleanText("NULL"));
    Assertions.assertEquals(StringUtil.cleanText(""), "");
    Assertions.assertEquals(StringUtil.cleanText("测 试1- \001１　3１A３ｂ５６ 7！８　9"), "测试1-131A3b567!89");
    Assertions.assertEquals(StringUtil.cleanText("测 \002 试 \003 2-１３１　２３４　５Ａ　６"), "测试2-1312345A6");
  }

  @Test
  void random() {
    String random = StringUtil.random(12);
    log.info("random -> " + random);
    Assertions.assertEquals(StringUtil.length(random), 12);

    random = StringUtil.random(16);
    log.info("random -> " + random);
    Assertions.assertEquals(StringUtil.length(random), 16);

    random = StringUtil.random(24);
    log.info("randomrandom -> " + random);
    Assertions.assertEquals(StringUtil.length(random), 24);
  }

  @Test
  void splitList() {
    Assertions.assertEquals(StringUtil.splitList(null, "\t"), "");
    Assertions.assertEquals(StringUtil.splitList(new ArrayList<>(), "\t"), "");
    Assertions.assertEquals(StringUtil.splitList(ListUtil.asList("a"), "\t"), "a");
    Assertions.assertEquals(StringUtil.splitList(ListUtil.asList("1", "a", "2", "b"), "\t"), "1\ta\t2\tb");
  }

  @Test
  void isBlank() {
    Assertions.assertTrue(StringUtil.isBlank(null));
    Assertions.assertTrue(StringUtil.isBlank(""));
    Assertions.assertTrue(StringUtil.isBlank(" "));
    Assertions.assertTrue(StringUtil.isBlank("　"));
    Assertions.assertFalse(StringUtil.isBlank("1"));
  }

  @Test
  void notBlank() {
    Assertions.assertFalse(StringUtil.notBlank(null));
    Assertions.assertFalse(StringUtil.notBlank(""));
    Assertions.assertFalse(StringUtil.notBlank(" "));
    Assertions.assertFalse(StringUtil.notBlank("　"));
    Assertions.assertTrue(StringUtil.notBlank("1"));
  }

  @Test
  void isEmpty() {
    Assertions.assertTrue(StringUtil.isEmpty(null));
    Assertions.assertTrue(StringUtil.isEmpty(""));
    Assertions.assertFalse(StringUtil.isEmpty(" "));
  }

  @Test
  void notEmpty() {
    Assertions.assertFalse(StringUtil.notEmpty(null));
    Assertions.assertFalse(StringUtil.notEmpty(""));
    Assertions.assertTrue(StringUtil.notEmpty(" "));
  }
}
