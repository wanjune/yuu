package com.github.wanjune.yuu.base.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SftpUtilTest {

  private static final String HOST = "100.101.102.103";
  private static final String PORT = "22";
  private static final String USERNAME = "sftpuser";
  private static final String PASSWORD = "sftppassword";

  private static final String LOCAL_DIR = "/Users/tester/test/test";

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

  @Test
  void get() throws Exception {
    FileUtil.delete(LOCAL_DIR);
    sftpUtil.get("/var/sftpfilestore/sftpuser/dev", LOCAL_DIR, null, null);
    Assertions.assertTrue(FileUtil.isExists(LOCAL_DIR));
    Assertions.assertTrue(FileUtil.getChildFilePathList(LOCAL_DIR, "xlsx").size() > 0);

    FileUtil.delete(LOCAL_DIR);
    sftpUtil.get("/var/sftpfilestore/sftpuser/dev/test_file-0913.xlsx", FileUtil.getChildPath(LOCAL_DIR, "test_file-0913.xlsx"), null, null);
    Assertions.assertTrue(FileUtil.isExists(FileUtil.getChildPath(LOCAL_DIR, "test_file-0913.xlsx")));

    FileUtil.delete(LOCAL_DIR);
    sftpUtil.get("./dev", LOCAL_DIR, null, null);
    Assertions.assertTrue(FileUtil.isExists(LOCAL_DIR));
    Assertions.assertTrue(FileUtil.getChildFilePathList(LOCAL_DIR, "xlsx").size() > 0);

    FileUtil.delete(LOCAL_DIR);
    sftpUtil.get("./dev/test_file-0913.xlsx", FileUtil.getChildPath(LOCAL_DIR, "test_file-0913.xlsx"), null, null);
    Assertions.assertTrue(FileUtil.isExists(FileUtil.getChildPath(LOCAL_DIR, "test_file-0913.xlsx")));
  }

  @Test
  void put() throws Exception {
    sftpUtil.rm("/var/sftpfilestore/sftpuser/test");
    Assertions.assertFalse(sftpUtil.isExist("/var/sftpfilestore/sftpuser/test"));
    sftpUtil.put(FileUtil.getChildPath(LOCAL_DIR, "test_file-0913.xlsx"), "/var/sftpfilestore/sftpuser/test/test_file-0913.xlsx", null, null);
    Assertions.assertTrue(sftpUtil.isExist("/var/sftpfilestore/sftpuser/test/test_file-0913.xlsx"));

    sftpUtil.rm("./test");
    Assertions.assertFalse(sftpUtil.isExist("./test"));
    sftpUtil.put(FileUtil.getChildPath(LOCAL_DIR, "test_file-0913.xlsx"), "./test/test_file-0913.xlsx", null, null);
    Assertions.assertTrue(sftpUtil.isExist("./test/test_file-0913.xlsx"));

    sftpUtil.rm("/var/sftpfilestore/sftpuser/test");
    Assertions.assertFalse(sftpUtil.isExist("/var/sftpfilestore/sftpuser/test"));
    sftpUtil.put(LOCAL_DIR, "/var/sftpfilestore/sftpuser/test", null, null);
    Assertions.assertTrue(sftpUtil.isExist("/var/sftpfilestore/sftpuser/test"));

    sftpUtil.rm("./test");
    Assertions.assertFalse(sftpUtil.isExist("./test"));
    sftpUtil.put(LOCAL_DIR, "./test", null, null);
    Assertions.assertTrue(sftpUtil.isExist("./test"));
  }

  @Test
  void isExist() {
    Assertions.assertTrue(sftpUtil.isExist("/var/sftpfilestore/sftpuser/dev"));
    Assertions.assertFalse(sftpUtil.isExist("/var/sftpfilestore/sftpuser/test"));
    Assertions.assertTrue(sftpUtil.isExist("/var/sftpfilestore/sftpuser/dev/test_file-0913.xlsx"));
    Assertions.assertFalse(sftpUtil.isExist("/var/sftpfilestore/sftpuser/dev/test_file-0913.pptx"));

    Assertions.assertTrue(sftpUtil.isExist("./dev"));
    Assertions.assertFalse(sftpUtil.isExist("./test"));
    Assertions.assertTrue(sftpUtil.isExist("./dev/test_file-0913.xlsx"));
    Assertions.assertFalse(sftpUtil.isExist("./dev/test_file-0913.pptx"));
  }

  @Test
  void isDir() {
    Assertions.assertTrue(sftpUtil.isDir("/var/sftpfilestore/sftpuser/dev"));
    Assertions.assertFalse(sftpUtil.isDir("/var/sftpfilestore/sftpuser/test"));
    Assertions.assertFalse(sftpUtil.isDir("/var/sftpfilestore/sftpuser/dev/test_file-0913.xlsx"));
    Assertions.assertFalse(sftpUtil.isDir("/var/sftpfilestore/sftpuser/dev/test_file-0913.pptx"));

    Assertions.assertTrue(sftpUtil.isDir("./dev"));
    Assertions.assertFalse(sftpUtil.isDir("./test"));
    Assertions.assertFalse(sftpUtil.isDir("./dev/test_file-0913.xlsx"));
    Assertions.assertFalse(sftpUtil.isDir("./dev/test_file-0913.pptx"));
  }

  @Test
  void immediateGet() throws Exception {
    // 目录下载
    SftpUtil.immediateGet(HOST, PORT, USERNAME, PASSWORD, "./test", LOCAL_DIR, ListUtil.asList("test_file-0913.xlsx"), ListUtil.asList("pptx"), true);
    // 文件下载
    SftpUtil.immediateGet(HOST, PORT, USERNAME, PASSWORD, "/var/sftpfilestore/sftpuser/test/test_file-0913.xlsx", LOCAL_DIR + "/test_file-0913.xlsx", null, null, true);
  }

  @Test
  void immediatePut() throws Exception {
    // 目录上传
    SftpUtil.immediatePut(HOST, PORT, USERNAME, PASSWORD, LOCAL_DIR, "./dev", ListUtil.asList("test_file-0913.xlsx"), ListUtil.asList("xls"), true);
    // 文件上传
    SftpUtil.immediatePut(HOST, PORT, USERNAME, PASSWORD, LOCAL_DIR + "/test_file-0913.xlsx", "/var/sftpfilestore/sftpuser/dev/test_file-0913.xlsx", null, null, true);
    SftpUtil.immediatePut(HOST, PORT, USERNAME, PASSWORD, LOCAL_DIR + "/AUDIT_APP.xls", "/var/sftpfilestore/sftpuser/dev/AUDIT_APP.xls", null, null, true);
  }
}
