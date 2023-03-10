package com.github.wanjune.yuu.util;

import com.github.wanjune.yuu.exception.YuuException;
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

  // 行分隔符
  public static final int LINE_SEPARATOR = '\n';
  // 需排除文件名前缀文件(当前目录,上级目录,系统文件,隐藏文件)
  public static final String NAME_EXCLUDE_PREFIX = ".";
  // 文件名中扩展名分隔符
  public static final int NAME_EXT_SEPARATOR = '.';
  // 路径中分隔符(非Windows系统)
  public static final String PATH_SEPARATOR = File.separator;
  // 路径中分隔符(Windows系统)
  public static final String PATH_SEPARATOR_WIN = "\\\\";
  // 相对路径中的当前路径
  public static final String PATH_RELATIVE_CURRENT = "./";
  // 文件扩展名
  public static final String EXT_XLXS = "xlsx";
  public static final String EXT_TXT = "txt";
  public static final String EXT_GZ = "gz";
  public static final String EXT_CSV = "csv";

  // 文件读写缓存大小
  private static final int BUF_SIZE = 4096;

  /**
   * 文件/目录是否存在
   *
   * @param path 文件/目录路径
   * @return 判断结果
   */
  public static boolean isExists(final String path) {
    try {
      return new File(path).exists();
    } catch (Exception ex) {
      throw new YuuException(String.format("判断文件/目录[%s]是否存在失败", path), ex);
    }
  }

  /**
   * 是否是目录
   *
   * @param path 文件/目录路径
   * @return 判断结果
   */
  public static boolean isDir(final String path) {
    try {
      if (isExists(path)) return new File(path).isDirectory();
      return false;
    } catch (Exception ex) {
      throw new YuuException(String.format("判断路径[%s]是否是目录失败", path), ex);
    }
  }

  /**
   * 创建文件/目录
   * <p>如果上级目录不存在,创建上级目录</p>
   * <p>相对[new File()],多一个创建上级目录 -> 避免出现异常</p>
   *
   * @param path 文件路径
   */
  @SuppressWarnings("ALL")
  public static File create(final String path) {
    try {
      File file = new File(path);
      file.getParentFile().mkdirs();
      return file;
    } catch (Exception ex) {
      throw new YuuException(String.format("创建文件[%s]失败", path), ex);
    }
  }

  /**
   * 删除文件/目录
   *
   * @param path 文件/目录路径
   */
  public static void delete(final String path) {
    delete(new File(path));
  }

  /**
   * 删除文件/目录
   *
   * @param file 要删除的文件/目录
   */
  @SuppressWarnings("ALL")
  public static void delete(final File file) {
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
      throw new YuuException(String.format("删除[%s][%s]失败", file.isDirectory() ? "目录" : "文件", file.getAbsolutePath()), ex);
    }
  }

  /**
   * 获取目录标准路径
   * <p>去除目录路径中的尾部的反斜杠</p>
   *
   * @param dirPath 目录路径
   * @return 目录标准路径
   */
  public static String getDirPath(String dirPath) {
    return StringUtil.isEmpty(dirPath) ? StringUtil.EMPTY : StringUtil.removeEnd(dirPath, FileUtil.PATH_SEPARATOR);
  }

  /**
   * 获取文件/目录上级目录路径
   *
   * @param path 文件路径
   * @return 上级目录
   */
  public static String getParentPath(final String path) {
    try {
      String stdPath = StringUtil.removeEnd(path.replaceAll(PATH_SEPARATOR_WIN, PATH_SEPARATOR), PATH_SEPARATOR); // 替换Windows系统分割符 并 去除尾部分隔符
      return stdPath.substring(0, stdPath.lastIndexOf(PATH_SEPARATOR));
    } catch (Exception ex) {
      throw new YuuException(String.format("获取[%s]的上级路径失败", path), ex);
    }
  }

  /**
   * 获取目录下文件路径
   *
   * @param dirPath  目录路径
   * @param fileName 文件全名(包括扩展名)
   * @return 文件路径
   */
  public static String getChildPath(final String dirPath, final String fileName) {
    try {
      String stdDirPath = StringUtil.removeEnd(dirPath.replaceAll(PATH_SEPARATOR_WIN, PATH_SEPARATOR), PATH_SEPARATOR); // 替换Windows系统分割符 并 去除尾部分隔符
      return stdDirPath.concat(PATH_SEPARATOR).concat(fileName);
    } catch (Exception ex) {
      throw new YuuException(String.format("获取[%s]的下级[%s]路径失败", dirPath, fileName), ex);
    }
  }

  /**
   * 取得文件扩展名
   *
   * @param fileName 文件名(包含扩展名)
   * @return 文件扩展名
   */
  public static String getExtension(final String fileName) {
    try {
      int i = fileName.lastIndexOf(NAME_EXT_SEPARATOR);
      if (i > 0 && i < fileName.length() - 1) {
        return fileName.substring(i + 1);
      } else {
        return StringUtil.EMPTY;
      }
    } catch (Exception ex) {
      throw new YuuException(String.format("获取文件[%s]扩展名失败", fileName), ex);
    }
  }

  /**
   * 获取目录下文件路径列表
   *
   * @param dirPath     目录路径
   * @param fileExtList 扩展名列表(null->所有文件)
   * @return 目录下的文件列表
   */
  public static List<String> listFiles(final String dirPath, final List<String> fileExtList) {
    try {
      List<String> filePathList = new ArrayList<>();
      File dir = new File(dirPath);

      if (dir.exists() && dir.isDirectory()) {
        File[] fileArrays = dir.listFiles();
        if (fileArrays != null) {
          for (File iFile : fileArrays) {
            if (!iFile.getName().startsWith(NAME_EXCLUDE_PREFIX)) {
              if (ListUtil.isEmpty(fileExtList)) {
                filePathList.add(iFile.getAbsolutePath());
              } else if (ListUtil.notEmpty(fileExtList) && StringUtil.isContains(getExtension(iFile.getName()), fileExtList, true)) {
                filePathList.add(iFile.getAbsolutePath());
              }
            }
          }
        }
      }

      // 排序
      ListUtil.sort(filePathList);

      return ListUtil.notEmpty(filePathList) ? filePathList : null;
    } catch (Exception ex) {
      throw new YuuException(String.format("获取目录[%s]下文件路径列表", dirPath), ex);
    }
  }

  /**
   * 文件合并
   *
   * @param filePathList    待合并的文件路径列表
   * @param combineFilePath 合并后的文件路径
   * @param isNewLine       是否换新行拼接文件
   * @throws Exception Exception
   */
  public static void combine(final List<String> filePathList, final String combineFilePath, final boolean isNewLine) throws Exception {

    BufferedOutputStream outputStream = null;
    BufferedInputStream inputStream = null;

    try {
      // 合并后文件:已存在 -> 删除(保留原目录); 不存在 -> 创建合并后文件的目录
      FileUtil.delete(FileUtil.create(combineFilePath));

      outputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(combineFilePath)));
      byte[] bytes = new byte[BUF_SIZE];
      int len;

      for (int i = 0; i < filePathList.size(); i++) {
        // 读取文件
        inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePathList.get(i))));
        // 合并文件
        while ((len = inputStream.read(bytes)) > 0) outputStream.write(bytes, 0, len);
        // 换新行
        if (isNewLine && i != filePathList.size() - 1) outputStream.write(LINE_SEPARATOR);
        // 关闭输入的文件流
        inputStream.close();

        log.info(String.format("已合并源文件:[%s] -> 目标文件[%s]中!", filePathList.get(i), combineFilePath));
      }
    } catch (Exception ex) {
      throw new YuuException(String.format("文件合并至[%s]失败", combineFilePath), ex);
    } finally {
      if (inputStream != null) inputStream.close();
      if (outputStream != null) outputStream.close();
    }
  }

}
