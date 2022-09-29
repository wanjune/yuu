package com.github.wanjune.yuu.base.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Map工具类
 *
 * @author wanjune
 * @since 2022-05-09
 */
public class MapUtil {

  /**
   * 判断Map是否为空
   *
   * @param map Map对象
   * @param <K> 键的类型
   * @param <V> 值的类型
   * @return true:null或数据为空 / false:不为空且有数据
   */
  public static <K, V> boolean isEmpty(Map<K, V> map) {
    return map == null || map.isEmpty();
  }

  /**
   * 判断Map是否不为空
   *
   * @param map Map对象
   * @param <K> 键的类型
   * @param <V> 值的类型
   * @return true:null或数据为空 / false:不为空且有数据
   */
  public static <K, V> boolean nonEmpty(Map<K, V> map) {
    return !isEmpty(map);
  }

  /**
   * 取得Map中的指定键的值(Object)
   *
   * @param map Map
   * @param key 键
   * @return 值(Object)
   */
  public static <K, V> V get(Map<K, V> map, K key) {
    return get(map, key, null);
  }

  /**
   * 取得Map中的指定键的值(Object)
   *
   * @param map Map
   * @param key 键
   * @return 值(Object)
   */
  public static <K, V> V get(Map<K, V> map, K key, V defValue) {
    return map == null || key == null || !map.containsKey(key) ? defValue : map.getOrDefault(key, defValue);
  }


  /**
   * 快速生成Map对象
   *
   * @param k1  键1
   * @param v1  值1
   * @param k2  键2
   * @param v2  值3
   * @param k3  键3
   * @param v3  值3
   * @param k4  键4
   * @param v4  值4
   * @param k5  键5
   * @param v5  值5
   * @param k6  键6
   * @param v6  值6
   * @param k7  键7
   * @param v7  值7
   * @param k8  键8
   * @param v8  值8
   * @param k9  键9
   * @param v9  值9
   * @param k10 键10
   * @param v10 值10
   * @param <K> 键的类型
   * @param <V> 值的类型
   * @return Map对象
   */
  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
      put(k6, v6);
      put(k7, v7);
      put(k8, v8);
      put(k9, v9);
      put(k10, v10);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
      put(k6, v6);
      put(k7, v7);
      put(k8, v8);
      put(k9, v9);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
      put(k6, v6);
      put(k7, v7);
      put(k8, v8);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
      put(k6, v6);
      put(k7, v7);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
      put(k6, v6);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
    }};
  }

  public static <K, V> Map<K, V> of(K k1, V v1) {
    return new HashMap<K, V>() {{
      put(k1, v1);
    }};
  }

}
