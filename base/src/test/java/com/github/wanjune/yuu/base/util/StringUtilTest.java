package com.github.wanjune.yuu.base.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@Slf4j
class StringUtilTest {

  @Test
  void instance() {
    Assertions.assertEquals("abc测试1efg", StringUtil.instance("abc{test}efg", MapUtil.of("test", "测试1")));
    Assertions.assertEquals("abc测试1e测试2f测试1g", StringUtil.instance("abc{test1}e{test2}f{test1}g", MapUtil.of("test1", "测试1", "test2", "测试2")));
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
    Assertions.assertEquals("测试数据", StringUtil.trimFirstAndLastChar("'测试数据'", "'"));
    Assertions.assertEquals("测试数据", StringUtil.trimFirstAndLastChar("\"测试数据\"", "\""));
    Assertions.assertEquals("测试数据", StringUtil.trimFirstAndLastChar("{测试数据}", "{}"));
    Assertions.assertEquals("测试数据", StringUtil.trimFirstAndLastChar("[测试数据]", "[]"));
    Assertions.assertEquals("测试数据", StringUtil.trimFirstAndLastChar("【测试数据】", "【】"));
    Assertions.assertEquals("测试数据", StringUtil.trimFirstAndLastChar("「测试数据」", "「」"));
    Assertions.assertEquals("'测试数据'", StringUtil.trimFirstAndLastChar("''测试数据''", "'"));
    Assertions.assertEquals("\"测试数据\"", StringUtil.trimFirstAndLastChar("\"\"测试数据\"\"", "\""));
    Assertions.assertEquals("{测试数据}", StringUtil.trimFirstAndLastChar("{{测试数据}}", "{}"));
    Assertions.assertEquals("[测试数据]", StringUtil.trimFirstAndLastChar("[[测试数据]]", "[]"));
    Assertions.assertEquals("【测试数据】", StringUtil.trimFirstAndLastChar("【【测试数据】】", "【】"));
    Assertions.assertEquals("「测试数据」", StringUtil.trimFirstAndLastChar("「「测试数据」」", "「」"));
  }

  @Test
  void removeStart() {
    String str = "A template for executing high-level operations. When used with a";
    Assertions.assertEquals(str, StringUtil.removeStart(str, null));
    Assertions.assertEquals(" template for executing high-level operations. When used with a", StringUtil.removeStart(str, "A"));
    Assertions.assertEquals("for executing high-level operations. When used with a", StringUtil.removeStart(str, "A template "));
  }

  @Test
  void removeEnd() {
    String str = "A template for executing high-level operations. When used with a";
    Assertions.assertEquals(str, StringUtil.removeEnd(str, null));
    Assertions.assertEquals("A template for executing high-level operations. When used with ", StringUtil.removeEnd(str, "a"));
    Assertions.assertEquals("A template for executing high-level operations. W", StringUtil.removeEnd(str, "hen used with a"));
  }

  @Test
  void toFullwidth() {
    Assertions.assertNull(StringUtil.toFullwidth(null));
    Assertions.assertEquals(StringUtil.toFullwidth(""), "");
    Assertions.assertEquals("１３１２３４５６７８９", StringUtil.toFullwidth("13123456789"));
    Assertions.assertEquals("１３１２３４５６７８９", StringUtil.toFullwidth("1３123４56789"));
    Assertions.assertEquals("１３１　２３４　５Ａ　６　ｂ７ｃ８　９", StringUtil.toFullwidth("1３1 23４ 5A　6 b7ｃ8　9"));
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
    Assertions.assertEquals("", StringUtil.cleanUnicode(""));
    Assertions.assertEquals("测试1", StringUtil.cleanUnicode("\uD83D\uDE00测\uD83D\uDE02试\uD83E\uDEF31"));
    Assertions.assertEquals("测试2", StringUtil.cleanUnicode("测\uD83D\uDE00试\uD83D\uDE022\uD83E\uDEF3"));
  }

  @Test
  void cleanControl() {
    Assertions.assertNull(StringUtil.cleanControl(null));
    Assertions.assertNull(StringUtil.cleanControl("null"));
    Assertions.assertNull(StringUtil.cleanControl("NULL"));
    Assertions.assertEquals("", StringUtil.cleanControl(""));
    Assertions.assertEquals("测试1", StringUtil.cleanControl("测试1\001"));
    Assertions.assertEquals("测试2", StringUtil.cleanControl("测\002试\0032"));
  }

  @Test
  void cleanText() {
    Assertions.assertNull(StringUtil.cleanText(null));
    Assertions.assertNull(StringUtil.cleanText("nULl"));
    Assertions.assertNull(StringUtil.cleanText("null"));
    Assertions.assertNull(StringUtil.cleanText("NULL"));
    Assertions.assertEquals("", StringUtil.cleanText(""));
    Assertions.assertEquals("测试1-131A3b567!89", StringUtil.cleanText("测 试1- \001１　3１A３ｂ５６ 7！８　9"));
    Assertions.assertEquals("测试2-1312345A6", StringUtil.cleanText("测 \002 试 \003 2-１３１　２３４　５Ａ　６"));
  }

  @Test
  void random() {
    String random = StringUtil.random(0);
    log.info("random -> " + random);
    Assertions.assertEquals(0, StringUtil.length(random));

    random = StringUtil.random(12);
    log.info("random -> " + random);
    Assertions.assertEquals(12, StringUtil.length(random));

    random = StringUtil.random(16);
    log.info("random -> " + random);
    Assertions.assertEquals(16, StringUtil.length(random));

    random = StringUtil.random(24);
    log.info("randomrandom -> " + random);
    Assertions.assertEquals(24, StringUtil.length(random));
  }

  @Test
  void splitList() {
    Assertions.assertEquals("", StringUtil.splitList(null, "\t"));
    Assertions.assertEquals("", StringUtil.splitList(new ArrayList<>(), "\t"));
    Assertions.assertEquals("a", StringUtil.splitList(ListUtil.asList("a"), "\t"));
    Assertions.assertEquals("1\ta\t2\tb", StringUtil.splitList(ListUtil.asList("1", "a", "2", "b"), "\t"));
  }

  @Test
  void length() {
    Assertions.assertEquals(0, StringUtil.length(null));
    Assertions.assertEquals(0, StringUtil.length(""));
    Assertions.assertEquals(3, StringUtil.length("abc"));
  }

  @Test
  void force() {
    Assertions.assertEquals("", StringUtil.force(null));
    Assertions.assertEquals("", StringUtil.force(""));
    Assertions.assertEquals("abc", StringUtil.force("abc"));
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
