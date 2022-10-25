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
   * 判断是否为空
   *
   * @param map Map对象
   * @param <K> 键的类型
   * @param <V> 值的类型
   * @return true:null或数据为空 / false:不为空且有数据
   */
  public static <K, V> boolean isEmpty(final Map<K, V> map) {
    return map == null || map.isEmpty();
  }

  /**
   * 判断是否非空
   *
   * @param map Map对象
   * @param <K> 键的类型
   * @param <V> 值的类型
   * @return true:null或数据为空 / false:不为空且有数据
   */
  public static <K, V> boolean notEmpty(final Map<K, V> map) {
    return !isEmpty(map);
  }

  /**
   * 取得指定键的值
   *
   * @param map Map对象
   * @param key 键
   * @return 值
   */
  public static <K, V> V get(final Map<K, V> map, final K key) {
    return get(map, key, null);
  }

  /**
   * 取得指定键的值
   * <p>不存在/空,使用默认值替代</p>
   *
   * @param map          Map
   * @param key          键
   * @param defaultValue 默认值
   * @return 值
   */
  public static <K, V> V get(final Map<K, V> map, final K key, final V defaultValue) {
    return map == null || key == null || !map.containsKey(key) ? defaultValue : map.getOrDefault(key, defaultValue);
  }

  /**
   * Map对象复制
   *
   * @param src 原Map对象
   * @param <K> 键的类型
   * @param <V> 值的类型
   * @return 复制的Map对象
   */
  public static <K, V> Map<K, V> copy(final Map<K, V> src) {
    return isEmpty(src) ? src : new HashMap<>(src);
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
  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5,
                                    final K k6, final V v6, final K k7, final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10) {
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

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5,
                                    final K k6, final V v6, final K k7, final V v7, final K k8, final V v8, final K k9, final V v9) {
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

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5,
                                    final V v5, final K k6, final V v6, final K k7, final V v7, final K k8, final V v8) {
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

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5,
                                    final K k6, final V v6, final K k7, final V v7) {
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

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5,
                                    final K k6, final V v6) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
      put(k6, v6);
    }};
  }

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
      put(k5, v5);
    }};
  }

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
      put(k4, v4);
    }};
  }

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
      put(k3, v3);
    }};
  }

  public static <K, V> Map<K, V> of(final K k1, final V v1, final K k2, final V v2) {
    return new HashMap<K, V>() {{
      put(k1, v1);
      put(k2, v2);
    }};
  }

  public static <K, V> Map<K, V> of(final K k1, final V v1) {
    return new HashMap<K, V>() {{
      put(k1, v1);
    }};
  }

}
