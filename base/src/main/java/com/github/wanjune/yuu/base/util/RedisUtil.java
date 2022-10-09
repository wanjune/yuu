package com.github.wanjune.yuu.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
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
 * Redis的String工具类
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
   * @param strKey Redis键
   * @return Redis值
   */
  public String getString(String strKey) {
    try {
      return stringRedisTemplate.opsForValue().get(strKey);
    } catch (Exception ex) {
      throw new RedisException(String.format("读取[key=%s]失败", strKey), ex);
    }
  }

  /**
   * 设置Redis值
   *
   * @param strKey   Redis键
   * @param strValue Redis值
   */
  public void setString(String strKey, String strValue) {
    try {
      stringRedisTemplate.opsForValue().set(strKey, strValue);
    } catch (Exception ex) {
      throw new RedisException(String.format("写入[key=%s]失败", strKey), ex);
    }
  }

  /**
   * 删除Redis的Key
   *
   * @param strKey Redis键
   * @return 删除结果
   */
  @SuppressWarnings("all")
  public boolean deleteKey(String strKey) {
    try {
      return stringRedisTemplate.delete(strKey);
    } catch (Exception ex) {
      throw new RedisException(String.format("删除[key=%s]失败", strKey), ex);
    }
  }

  /**
   * 获取指定匹配模式的Key列表
   *
   * @param keyScanPattern Key扫描表达式
   * @return Key列表
   */
  public List<String> getKeys(String keyScanPattern) throws Exception {
    try {
      Set<String> setKeys = stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
        Set<String> keysTmp = new HashSet<>();
        try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(keyScanPattern).count(SCAN_OPTION_COUNT).build())) {
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
      throw new RedisException(String.format("扫描[key=%s]失败", keyScanPattern), ex);
    }
  }

  /**
   * 设置Redis值
   *
   * @param strKey   Redis键
   * @param objValue Redis的对象的值
   */
  public void setObject(String strKey, Object objValue) throws JsonProcessingException {
    try {
      setString(strKey, JsonUtil.writeValueAsString(objValue));
    } catch (Exception ex) {
      throw new RedisException(String.format("写入[key=%s]失败", strKey), ex);
    }
  }
}
