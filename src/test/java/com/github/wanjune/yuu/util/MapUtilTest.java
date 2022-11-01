package com.github.wanjune.yuu.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class MapUtilTest {

  @Test
  void isEmpty() {
    Assertions.assertTrue(MapUtil.isEmpty(null));
    Assertions.assertTrue(MapUtil.isEmpty(new HashMap<>()));
    Assertions.assertFalse(MapUtil.isEmpty(new HashMap<String, String>() {{
      put("k1", "v1");
    }}));
  }

  @Test
  void notEmpty() {
    Assertions.assertFalse(MapUtil.notEmpty(null));
    Assertions.assertFalse(MapUtil.notEmpty(new HashMap<>()));
    Assertions.assertTrue(MapUtil.notEmpty(new HashMap<String, String>() {{
      put("k1", "v1");
    }}));
  }

  @Test
  void get() {
    Map<String, String> map = new HashMap<String, String>() {{
      put("k1", "v1");
    }};

    Assertions.assertEquals("v1", MapUtil.get(map, "k1"));
    Assertions.assertNull(MapUtil.get(map, "k2"));
  }

  @Test
  void copy() {
    Map<String, String> map1 = new HashMap<String, String>() {{
      put("k1", "v1");
    }};

    Map<String, String> map2 = MapUtil.copy(map1);
    map1.remove("k1");

    Assertions.assertTrue(map1.isEmpty());
    Assertions.assertEquals("v1", MapUtil.get(map2, "k1"));
    Assertions.assertNull(MapUtil.get(map2, "k2"));
  }

  @Test
  void getDef() {
    Map<String, String> map = new HashMap<String, String>() {{
      put("k1", "v1");
    }};

    Assertions.assertEquals("v1", MapUtil.get(map, "k1", "v2"));
    Assertions.assertEquals("v2", MapUtil.get(map, "k2", "v2"));
  }

  @Test
  void of() {
    Map<String, String> map = MapUtil.of("k1", "v1", "k2", "v2");

    Assertions.assertEquals(map.size(), 2);
    Assertions.assertEquals("v1", MapUtil.get(map, "k1"));
    Assertions.assertEquals("v2", MapUtil.get(map, "k2"));
  }
}
