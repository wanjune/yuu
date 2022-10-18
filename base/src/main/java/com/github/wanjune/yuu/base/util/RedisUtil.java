package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.exception.RedisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Redis工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
@Component
public class RedisUtil {

  private static final Long SCAN_OPTION_COUNT = 10000L;

  private final StringRedisTemplate stringRedisTemplate;

  @Autowired
  public RedisUtil(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  /**
   * 获取Redis值
   *
   * @param key Redis键
   * @return Redis值
   */
  public String get(String key) {
    try {
      return stringRedisTemplate.opsForValue().get(key);
    } catch (Exception ex) {
      throw new RedisException(String.format("读取[key=%s]失败", key), ex);
    }
  }

  /**
   * 设置Redis值
   *
   * @param key   Redis键
   * @param value Redis值
   */
  public <T> void set(String key, T value) {
    try {
      if (value instanceof String) {
        stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
      } else {
        stringRedisTemplate.opsForValue().set(key, JsonUtil.writeValueAsString(value));
      }
    } catch (Exception ex) {
      throw new RedisException(String.format("写入[key=%s]失败", key), ex);
    }
  }

  /**
   * 删除Redis的Key
   *
   * @param key Redis键
   * @return 删除结果
   */
  @SuppressWarnings("all")
  public boolean delete(String key) {
    try {
      return stringRedisTemplate.delete(key);
    } catch (Exception ex) {
      throw new RedisException(String.format("删除[key=%s]失败", key), ex);
    }
  }

  /**
   * 获取指定匹配模式的Key列表
   *
   * @param keyPattern Key表达式
   * @return Key列表
   */
  public List<String> scan(String keyPattern) throws Exception {
    try {
      Set<String> setKeys = stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
        Set<String> keysTmp = new HashSet<>();
        try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(keyPattern).count(SCAN_OPTION_COUNT).build())) {
          while (cursor.hasNext()) {
            keysTmp.add(new String(cursor.next(), StandardCharsets.UTF_8));
          }
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
        return keysTmp;
      });
      return CollectionUtils.isEmpty(setKeys) ? null : new ArrayList<String>(setKeys);
    } catch (Exception ex) {
      throw new RedisException(String.format("扫描[key=%s]失败", keyPattern), ex);
    }
  }

}
