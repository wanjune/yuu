package com.github.wanjune.yuu.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 编码解码+消息摘要工具类
 *
 * @author wanjune
 * @since 2020-09-01
 */
public class CodecUtil {

  /**
   * 将字符串进行base64编码
   *
   * @param string 待base64编码的字符串
   * @return base64编码后的字符串
   */
  public static String encodeBase64(final String string) {
    return Base64.encodeBase64String(string.getBytes());
  }

  /**
   * base64数据字符串解码
   *
   * @param base64DataString base64数据字符串
   * @return base64解码后的字符串
   */
  public static String decodeBase64(final String base64DataString) {
    return new String(Base64.decodeBase64(base64DataString.getBytes()));
  }

  /**
   * 计算MD5摘要
   *
   * @param string 待处理字符串
   * @return MD5摘要的2进制字符串
   */
  public static String md5(final String string) {
    return DigestUtils.md5Hex(string);
  }

  /**
   * 计算SHA-1摘要
   *
   * @param string 待处理字符串
   * @return SHA-1摘要的2进制字符串
   */
  public static String sha1(final String string) {
    return DigestUtils.sha1Hex(string);
  }

  /**
   * 计算SHA-256摘要
   *
   * @param string 待处理字符串
   * @return SHA-256摘要的2进制字符串
   */
  public static String sha256(final String string) {
    return DigestUtils.sha256Hex(string);
  }

  /**
   * 计算SHA-512摘要
   *
   * @param string 待处理字符串
   * @return SHA-512摘要的2进制字符串
   */
  public static String sha512(final String string) {
    return DigestUtils.sha512Hex(string);
  }

}
