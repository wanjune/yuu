package com.github.wanjune.yuu.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * File工具类
 *
 * @author wanjune
 * @since 2020-10-27
 */
@Slf4j
public class FileUtil {

  public static final String FILE_SEPARATOR = "/";
  public static final String FILE_EXT_TXT = "txt";
  public static final String FILE_EXT_GZ = "gz";
  public static final String FILE_EXT_CSV = "csv";
  public static final int BUFFER_SIZE = 4096;

  /**
   * 文件或目录是否存在
   *
   * @param filePath 文件或目录路径
   * @return 判断结果
   */
  public static boolean isExist(String filePath) {
    return new File(filePath).exists();
  }

  /**
   * 创建文件</p>
   * 如果上级目录不存在,创建上级目录</br>
   * 与New File(),多一个创建上级目录,避免出现异常
   *
   * @param filePath 文件路径
   */
  public static File create(String filePath) {
    try {
      File file = new File(filePath);

      // 创建文件目录
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }

      return file;
    } catch (Exception ex) {
      throw new RuntimeException(String.format("创建文件[%s]失败", filePath), ex);
    }
  }

  /**
   * 删除文件或目录
   *
   * @param filePath 文件或目录路径
   */
  public static void delete(String filePath) {
    try {
      delete(new File(filePath));
    } catch (Exception ex) {
      throw new RuntimeException(String.format("删除文件/目录[%s]失败", filePath), ex);
    }
  }

  /**
   * 删除文件或目录
   *
   * @param file 要删除的文件或目录
   */
  public static void delete(File file) {
    try {
      if (file.exists()) {
        if (file.isDirectory()) {
          String[] childFiles = file.list();
          if (childFiles != null && childFiles.length > 0) {
            // 递归删除目录中的子目录下
            for (String childFile : childFiles) {
              delete(new File(file, childFile));
            }
          }
        }

        file.delete();
      }
    } catch (Exception ex) {
      throw new RuntimeException(String.format("删除文件/目录[%s]失败", file.getAbsoluteFile()), ex);
    }
  }

  /**
   * 获取文件的上级目录路径
   *
   * @param filePath 文件路径
   * @return 上级目录
   */
  public static String getParentDirPath(String filePath) {
    try {
      String strFilePath = filePath.replaceAll("\\\\", FILE_SEPARATOR);
      return strFilePath.substring(0, strFilePath.lastIndexOf(FILE_SEPARATOR));
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * 获取目录下文件路径
   *
   * @param dirPath  目录路径
   * @param fileName 文件全名(包括扩展名)
   * @return 文件路径
   */
  public static String getChildFilePath(String dirPath, String fileName) {
    return dirPath + FILE_SEPARATOR + fileName;
  }

  /**
   * 取得文件扩展名
   *
   * @param fileName 文件名(包含扩展名)
   * @return 文件扩展名
   */
  public static String getExtension(String fileName) {
    int i = fileName.lastIndexOf('.');
    if (i > 0 && i < fileName.length() - 1) {
      return fileName.substring(i + 1);
    } else {
      return null;
    }
  }

  /**
   * 获取目录下指定扩展名的文件路径列表
   *
   * @param dirPath 指定的目录路径
   * @param ext     扩展名
   * @return 目录下的文件列表
   */
  public static List<String> getChildFilePathList(String dirPath, String ext) {

    List<String> filePathList = new ArrayList<String>();

    File dirFile = new File(dirPath);

    if (dirFile.exists() && dirFile.isDirectory()) {
      File[] fileArrays = dirFile.listFiles();
      if (fileArrays != null && fileArrays.length > 0) {
        for (File iFile : fileArrays) {
          if (ext.equalsIgnoreCase(getExtension(iFile.getName()))) {
            filePathList.add(iFile.getAbsolutePath());
          }
        }
      }
    }

    if (ListUtil.nonEmpty(filePathList)) {
      // 排序
      ListUtil.sort(filePathList);
      return filePathList;
    }

    return null;
  }

  /**
   * 文件合并
   *
   * @param filePathList    待合并的文件路径列表
   * @param combineFilePath 合并后的文件路径
   * @throws Exception Exception
   */
  public static void combine(List<String> filePathList, String combineFilePath) throws Exception {

    BufferedOutputStream outputStream = null;
    BufferedInputStream inputStream = null;

    log.info(String.format("[%s]文件合并,处理开始!", "combine"));

    try {
      outputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(combineFilePath)));

      byte[] buffer = new byte[BUFFER_SIZE];

      for (String iFilePath : filePathList) {

        // 读取文件流
        inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(iFilePath)));

        // 合并文件
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }

        // 关闭读取的文件流
        log.info(String.format("[%s]已合并文件,源文件:[%s] -> 目标文件[%s]!", "combine", iFilePath, combineFilePath));
        inputStream.close();
      }
    } catch (Exception ex) {
      log.error(String.format("[%s]文件合并,处理异常!", "combine"), ex);
      throw ex;
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      if (outputStream != null) {
        outputStream.close();
      }
    }

    log.info(String.format("[%s]文件合并,处理结束!", "combine"));
  }

}
