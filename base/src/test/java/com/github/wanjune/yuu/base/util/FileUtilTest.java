package com.github.wanjune.yuu.base.util;

import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class FileUtilTest {

  private static final String DIR_PATH = "/tmp";
  private static final String FILE_PATH_1 = DIR_PATH + FileUtil.SEPARATOR + "20221011/1156." + FileUtil.EXT_CSV;
  private static final String FILE_PATH_2 = DIR_PATH + FileUtil.SEPARATOR + "20221012/1157." + FileUtil.EXT_TXT;

  private static final String FILE_PATH_3 = DIR_PATH + FileUtil.SEPARATOR + "20221013/1410." + FileUtil.EXT_CSV;

  @AfterAll
  public static void tearDown() throws Exception {
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_1));
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_2));
    FileUtil.delete(FileUtil.getParentPath(FILE_PATH_3));
  }

  @BeforeEach
  private void setUp() {
    FileUtil.delete(FILE_PATH_1);
    FileUtil.delete(FILE_PATH_2);
    FileUtil.delete(FILE_PATH_3);

    fileWrite(FILE_PATH_2);
  }

  @Order(1)
  @Test
  void isExist() {
    Assertions.assertFalse(FileUtil.isExists(FILE_PATH_1));
    Assertions.assertTrue(FileUtil.isExists(FILE_PATH_2));
  }

  @Test
  @Order(2)
  void create() {
    Assertions.assertEquals(FileUtil.create(FILE_PATH_1).getAbsolutePath(), FILE_PATH_1);
  }

  @Test
  @Order(3)
  void delete() {
    Assertions.assertTrue(FileUtil.isExists(FILE_PATH_2));

    FileUtil.delete(FILE_PATH_2);
    Assertions.assertFalse(FileUtil.isExists(FILE_PATH_2));
  }

  @Test
  @Order(4)
  void testDelete() {
    fileWrite(FILE_PATH_1);
    Assertions.assertTrue(FileUtil.isExists(FILE_PATH_1));

    FileUtil.delete(FileUtil.create(FILE_PATH_1));
    Assertions.assertFalse(FileUtil.isExists(FILE_PATH_1));
  }

  @Test
  @Order(5)
  void getParentPath() {
    Assertions.assertEquals(FileUtil.getParentPath(FILE_PATH_1), DIR_PATH + FileUtil.SEPARATOR + "20221011");
    Assertions.assertEquals(FileUtil.getParentPath(FILE_PATH_2), DIR_PATH + FileUtil.SEPARATOR + "20221012");
  }

  @Test
  @Order(7)
  void getChildPath() {
    Assertions.assertEquals(FileUtil.getChildPath(DIR_PATH + FileUtil.SEPARATOR + "20221011", "1156." + FileUtil.EXT_CSV), FILE_PATH_1);
    Assertions.assertEquals(FileUtil.getChildPath(DIR_PATH + FileUtil.SEPARATOR + "20221012", "1157." + FileUtil.EXT_TXT), FILE_PATH_2);
  }

  @Test
  @Order(8)
  void getExtension() {
    Assertions.assertEquals(FileUtil.getExtension(FILE_PATH_1), FileUtil.EXT_CSV);
    Assertions.assertEquals(FileUtil.getExtension(FILE_PATH_2), FileUtil.EXT_TXT);

  }

  @Test
  @Order(9)
  void getChildFilePathList() {
    fileWrite(FILE_PATH_1);
    fileWrite(FILE_PATH_2);
    Assertions.assertEquals(FileUtil.listFiles(DIR_PATH + FileUtil.SEPARATOR + "20221011", ListUtil.asList(FileUtil.EXT_CSV)), ListUtil.asList(FILE_PATH_1));
    Assertions.assertEquals(FileUtil.listFiles(DIR_PATH + FileUtil.SEPARATOR + "20221012", ListUtil.asList(FileUtil.EXT_TXT)), ListUtil.asList(FILE_PATH_2));
  }

  @Test
  @Order(10)
  void combine() throws Exception {
    fileWrite(FILE_PATH_1);
    fileWrite(FILE_PATH_2);
    FileUtil.combine(ListUtil.asList(FILE_PATH_1, FILE_PATH_2), FILE_PATH_3, true);

    Assertions.assertTrue(FileUtil.isExists(FILE_PATH_3));
    Assertions.assertTrue(FileUtil.create(FILE_PATH_3).isFile());

    FileUtil.delete(FileUtil.create(FILE_PATH_3));
    Assertions.assertFalse(FileUtil.isExists(FILE_PATH_3));
  }

  private void fileWrite(String filePath) {
    try {
      FileUtil.create(filePath); // 创建上级目录
      BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
      out.write(TimeUtil.getDateTimeNowFormat(TimeUtil.FMT_DT_FULL_SIMPLE));
      out.close();
    } catch (IOException e) {
      // Nothing
    }
  }
}
