package com.github.wanjune.yuu.base.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 通用加密、解密工具类
 *
 * @author wanjune
 * @since 2020-09-01
 */
public class CodecUtil {

  /**
   * base64Encode
   *
   * @param dataContextStr 待加密数据字符串
   * @return 加密后的数据(字符串)
   */
  public static String base64Encode(final String dataContextStr) {
    return Base64.encodeBase64String(dataContextStr.getBytes());
  }

  /**
   * base64Decode
   *
   * @param dataEncContextStr 已加密类容字符串
   * @return 解密后数据(字符串)
   */
  public static String base64Decode(final String dataEncContextStr) {
    return new String(Base64.decodeBase64(dataEncContextStr.getBytes()));
  }

  /**
   * md5
   *
   * @param dataContextStr 待加密数据字符串
   * @return 加密后的数据(字符串)
   */
  public static String md5Hex(final String dataContextStr) {
    return DigestUtils.md5Hex(dataContextStr);
  }

  /**
   * sha1Hex
   *
   * @param dataContextStr 待加密数据字符串
   * @return 加密后的数据(字符串)
   */
  public static String sha1Hex(final String dataContextStr) {
    return DigestUtils.sha1Hex(dataContextStr);
  }

  /**
   * sha256Hex
   *
   * @param dataContextStr 待加密数据字符串
   * @return 加密后的数据(字符串)
   */
  public static String sha256Hex(final String dataContextStr) {
    return DigestUtils.sha256Hex(dataContextStr);
  }

}
