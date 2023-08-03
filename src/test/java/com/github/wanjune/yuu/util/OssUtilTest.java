package com.github.wanjune.yuu.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class OssUtilTest {
  private static final String ENDPOINT = "";
  private static final String ACCESSKEY_ID = "";
  private static final String ACCESSKEY_SECRET = "";
  private static final String BUCKET = "";

  private static OssUtil ossUtil;

  @BeforeAll
  static void before() throws Exception {
    ossUtil = new OssUtil(ENDPOINT, ACCESSKEY_ID, ACCESSKEY_SECRET, BUCKET);
  }

  @AfterAll
  static void after() {
    ossUtil.close();
  }

  @Test
  void upload() {
    System.out.println(System.nanoTime());
  }

  @Test
  void download() {
  }

  @Test
  void deleteFile() {
  }

  @Test
  void deleteDir() {
    //ossUtil.deleteDir("API/TC/DEV/ego/outercode");
  }

  //@Test
  //void listFiles() {
  //}


  @Test
  void isExists() {

  }

  @Test
  void getParentPath() {
  }
}
