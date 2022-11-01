package com.github.wanjune.yuu.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;

class FileUtilTest {

  private static final String DIR_PATH = "/tmp";
  private static final String FILE_PATH_1 = DIR_PATH + FileUtil.PATH_SEPARATOR + "20221011/1156." + FileUtil.EXT_CSV;
  private static final String FILE_PATH_2 = DIR_PATH + FileUtil.PATH_SEPARATOR + "20221012/1157." + FileUtil.EXT_TXT;
  private static final String FILE_PATH_3 = DIR_PATH + FileUtil.PATH_SEPARATOR + "20221013/1410." + FileUtil.EXT_CSV;

  @AfterAll
  public static void tearDown() throws Exception {
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_1));
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_2));
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_3));
  }

  @BeforeEach
  private void setUp() {
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_1));
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_2));
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_3));
  }

  private void fileWrite(String filePath) {
    try {
      FileUtil.create(filePath); // 创建上级目录
      BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
      out.write(TimeUtil.getNowDateTimeFormat(TimeUtil.FMT_DT_FULL_SIMPLE));
      out.close();
    } catch (Exception e) {
      // Nothing
    }
  }

  @Test
  void isExists() {
    fileWrite(FILE_PATH_2);
    Assertions.assertFalse(FileUtil.isExists(FILE_PATH_1));
    Assertions.assertTrue(FileUtil.isExists(FILE_PATH_2));
  }

  @Test
  void create() {
    Assertions.assertEquals(FILE_PATH_1, FileUtil.create(FILE_PATH_1).getAbsolutePath());
  }

  @Test
  void delete() {
    fileWrite(FILE_PATH_2);
    Assertions.assertTrue(FileUtil.isExists(FILE_PATH_2));

    FileUtil.delete(FILE_PATH_2);
    Assertions.assertFalse(FileUtil.isExists(FILE_PATH_2));
  }

  @Test
  void getParentPath() {
    Assertions.assertEquals(DIR_PATH + FileUtil.PATH_SEPARATOR + "20221011", FileUtil.getParentPath(FILE_PATH_1));
    Assertions.assertEquals(DIR_PATH + FileUtil.PATH_SEPARATOR + "20221012", FileUtil.getParentPath(FILE_PATH_2));
  }

  @Test
  void getChildPath() {
    Assertions.assertEquals(FILE_PATH_1, FileUtil.getChildPath(DIR_PATH + FileUtil.PATH_SEPARATOR + "20221011", "1156." + FileUtil.EXT_CSV));
    Assertions.assertEquals(FILE_PATH_2, FileUtil.getChildPath(DIR_PATH + FileUtil.PATH_SEPARATOR + "20221012", "1157." + FileUtil.EXT_TXT));
  }

  @Test
  void getExtension() {
    Assertions.assertEquals(FileUtil.EXT_CSV, FileUtil.getExtension(FILE_PATH_1));
    Assertions.assertEquals(FileUtil.EXT_TXT, FileUtil.getExtension(FILE_PATH_2));
  }

  @Test
  void listFiles() {
    fileWrite(FILE_PATH_1);
    fileWrite(FILE_PATH_2);
    Assertions.assertEquals(ListUtil.asList(FILE_PATH_1), FileUtil.listFiles(DIR_PATH + FileUtil.PATH_SEPARATOR + "20221011", ListUtil.asList(FileUtil.EXT_CSV)));
    Assertions.assertEquals(ListUtil.asList(FILE_PATH_2), FileUtil.listFiles(DIR_PATH + FileUtil.PATH_SEPARATOR + "20221012", ListUtil.asList(FileUtil.EXT_TXT)));
  }

  @Test
  void combine() throws Exception {
    fileWrite(FILE_PATH_1);
    fileWrite(FILE_PATH_2);
    FileUtil.combine(ListUtil.asList(FILE_PATH_1, FILE_PATH_2), FILE_PATH_3, true);

    Assertions.assertTrue(FileUtil.isExists(FILE_PATH_3));
    Assertions.assertTrue(FileUtil.create(FILE_PATH_3).isFile());

    FileUtil.delete(FileUtil.create(FILE_PATH_3));
    Assertions.assertFalse(FileUtil.isExists(FILE_PATH_3));
  }
}
