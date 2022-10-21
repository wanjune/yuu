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

  private final StringRedisTemplate redisTemplate;

  @Autowired
  public RedisUtil(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 获取Redis值
   *
   * @param key Redis键
   * @return Redis值
   */
  public String get(String key) {
    try {
      return redisTemplate.opsForValue().get(key);
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
  public void set(String key, Object value) {
    try {
      if (value instanceof String) {
        redisTemplate.opsForValue().set(key, String.valueOf(value));
      } else {
        redisTemplate.opsForValue().set(key, JsonUtil.writeValueAsString(value));
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
      return redisTemplate.delete(key);
    } catch (Exception ex) {
      throw new RedisException(String.format("删除[key=%s]失败", key), ex);
    }
  }

  /**
   * 获取指定匹配模式的Key列表
   *
   * @param pattern Key表达式
   * @return Key列表
   */
  public List<String> scan(String pattern) {
    try {
      Set<String> keySet = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
        Set<String> keySetTmp = new HashSet<>();
        try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(SCAN_OPTION_COUNT).build())) {
          while (cursor.hasNext()) {
            keySetTmp.add(new String(cursor.next(), StandardCharsets.UTF_8));
          }
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
        return keySetTmp;
      });
      return CollectionUtils.isEmpty(keySet) ? null : new ArrayList<>(keySet);
    } catch (Exception ex) {
      throw new RedisException(String.format("扫描[%s]失败", pattern), ex);
    }
  }

}
