package com.github.wanjune.yuu.base.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SftpUtilTest {

  private static final String HOST = "100.102.103.199";
  private static final String PORT = "22";
  private static final String USERNAME = "testuser";
  private static final String PASSWORD = "testuserpassword";

  private static final String LOCAL_DIR = "/Users/demo/Downloads/test";
  private static final String SFTP_DIR = "./test";

  private static SftpUtil sftpUtil;

  @BeforeAll
  static void before() throws Exception {
    sftpUtil = new SftpUtil(HOST, PORT, USERNAME, PASSWORD);
    sftpUtil.openChannel();
  }

  @AfterAll
  static void after() {
    sftpUtil.closeChannel();
  }

  //@Test
  //void quickGet() {
  //  // 目录下载
  //  SftpUtil.quickGet(HOST, PORT, USERNAME, PASSWORD, "./dev", LOCAL_DIR, ListUtil.asList("TEST_FILE-0927.xlsx"), ListUtil.asList("pptx"), true);
  //  // 文件下载
  //  SftpUtil.quickGet(HOST, PORT, USERNAME, PASSWORD, "/var/ftpfiles/testuser/dev/TEST_FILE-0927.xlsx", LOCAL_DIR + "/TEST_FILE-0927.xlsx", null, null, true);
  //}
  //
  //@Test
  //void quickPut() {
  //  // 目录上传
  //  SftpUtil.quickPut(HOST, PORT, USERNAME, PASSWORD, LOCAL_DIR, SFTP_DIR, ListUtil.asList("TEST_FILE-0916.xlsx"), ListUtil.asList("pptx"), true);
  //  // 文件上传
  //  SftpUtil.quickPut(HOST, PORT, USERNAME, PASSWORD, LOCAL_DIR + "/TEST_FILE-0916.xlsx", SFTP_DIR + "/TEST_FILE-0916.xlsx", null, null, false);
  //  SftpUtil.quickPut(HOST, PORT, USERNAME, PASSWORD, LOCAL_DIR, SFTP_DIR, null, ListUtil.asList("xlsx"), false);
  //}

  @Test
  void get() throws Exception {
    // 目录下载
    sftpUtil.get("./dev", LOCAL_DIR, ListUtil.asList("TEST_FILE-0927.xlsx"), ListUtil.asList("pptx"));
    // 文件下载
    sftpUtil.get("/var/ftpfiles/testuser/dev/TEST_FILE-0927.xlsx", LOCAL_DIR + "/TEST_FILE-0927.xlsx", null, null);
  }

  @Test
  void put() throws Exception {
    // 目录上传
    sftpUtil.put(LOCAL_DIR, "/var/ftpfiles/testuser/test", ListUtil.asList("TEST_FILE-0916.xlsx"), ListUtil.asList("pptx"));
    // 文件上传
    sftpUtil.put(LOCAL_DIR + "/TEST_FILE-0916.xlsx", SFTP_DIR + "/TEST_FILE-0916.xlsx", null, null);
    sftpUtil.put(LOCAL_DIR, SFTP_DIR, null, ListUtil.asList("xlsx"));
  }

  @Test
  void rm() {
    Assertions.assertTrue(sftpUtil.isExists("/var/ftpfiles/testuser/test/TEST_FILE-0923.xlsx"));
    sftpUtil.rm("/var/ftpfiles/testuser/test/TEST_FILE-0923.xlsx");
    Assertions.assertFalse(sftpUtil.isExists("/var/ftpfiles/testuser/test/TEST_FILE-0923.xlsx"));

    Assertions.assertTrue(sftpUtil.isExists("./test"));
    sftpUtil.rm("./test");
    Assertions.assertFalse(sftpUtil.isExists("./test"));
  }

  @Test
  void isExists() {
    Assertions.assertTrue(sftpUtil.isExists("/var/ftpfiles/testuser/dev"));
    Assertions.assertFalse(sftpUtil.isExists("/var/ftpfiles/testuser/test"));
    Assertions.assertTrue(sftpUtil.isExists("/var/ftpfiles/testuser/dev/TEST_FILE-0927.xlsx"));
    Assertions.assertFalse(sftpUtil.isExists("/var/ftpfiles/testuser/test/TEST_FILE-0927.xlsx"));

    Assertions.assertTrue(sftpUtil.isExists("./dev"));
    Assertions.assertFalse(sftpUtil.isExists("./test"));
    Assertions.assertTrue(sftpUtil.isExists("./dev/TEST_FILE-0927.xlsx"));
    Assertions.assertFalse(sftpUtil.isExists("./test/TEST_FILE-0927.xlsx"));
  }

  @Test
  void isDir() {
    Assertions.assertTrue(sftpUtil.isDir("/var/ftpfiles/testuser/dev"));
    Assertions.assertFalse(sftpUtil.isDir("/var/ftpfiles/testuser/test"));
    Assertions.assertFalse(sftpUtil.isDir("/var/ftpfiles/testuser/dev/TEST_FILE-0927.xlsx"));
    Assertions.assertFalse(sftpUtil.isDir("/var/ftpfiles/testuser/test/TEST_FILE-0927.xlsx"));

    Assertions.assertTrue(sftpUtil.isDir("./dev"));
    Assertions.assertFalse(sftpUtil.isDir("./test"));
    Assertions.assertFalse(sftpUtil.isDir("./dev/TEST_FILE-0927.xlsx"));
    Assertions.assertFalse(sftpUtil.isDir("./test/TEST_FILE-0927.xlsx"));
  }

  @Test
  void getLastFileByName() {
    Assertions.assertEquals("TEST_FILE.xlsx", sftpUtil.getLastFileByName("/var/ftpfiles/testuser/dev", null));
    Assertions.assertEquals("TEST_FILE.xlsx", sftpUtil.getLastFileByName("./dev", "xlsx"));
  }
}
