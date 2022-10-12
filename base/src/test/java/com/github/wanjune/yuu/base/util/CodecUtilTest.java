package com.github.wanjune.yuu.base.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class CodecUtilTest {

  @Test
  void encodeBase64() {
    Assertions.assertEquals(CodecUtil.encodeBase64("BASE64编码测试"), "QkFTRTY057yW56CB5rWL6K+V");
  }

  @Test
  void decodeBase64() {
    Assertions.assertEquals(CodecUtil.decodeBase64("QkFTRTY06Kej56CB5rWL6K+V"), "BASE64解码测试");
  }

  @Test
  void md5() {
    Assertions.assertEquals(CodecUtil.md5("MD5摘要测试"), "704b1c67f28b2fffb3b7b2d11b9479d7");
  }

  @Test
  void sha1() {
    Assertions.assertEquals(CodecUtil.sha1("SHA-1摘要测试"), "360b6346671c59e11a4cdfb5f94a84510bde3763");
  }

  @Test
  void sha256() {
    Assertions.assertEquals(CodecUtil.sha256("SHA-256摘要测试"), "4f3db5ba76c597f71b0b1663a376728723bcf16e00a11d11e7cd5eff085868a4");
  }

  @Test
  void sha512() {
    Assertions.assertEquals(CodecUtil.sha512("SHA-512摘要测试"), "6aadafff882a454404c88d7bfb3c2c3788f77feb7aec245197ad774a3c75970d2656dac9127a8672b485bc472f799c866847df7220c6d5d77dff313b28abebd0");
  }
}
