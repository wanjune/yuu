package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.exception.SftpException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * SFTP工具类
 * <p>支持绝对路径和登录用户根目录相对路径</p>
 *
 * @author wanjune
 * @since 2020-10-27
 */
@Component
@Slf4j
@SuppressWarnings("ALL")
public class SftpUtil {

  // FTP默认端口
  private static final int SFTP_DEFAULT_PORT = 22;
  // 通道类型 - SFTP
  private static final String CHANNEL_TYPE_SFTP = "sftp";

  /*
   * FTP配置属性
   */
  private final String host;
  private final String port;
  private final String username;
  private final String password;

  private Session session = null;
  private ChannelSftp channelSftp = null;
  private String rootPath = null;

  /**
   * SFTP工具类构造函数 - 参数初始化
   *
   * @param host     SFTP服务器
   * @param port     SFTP端口
   * @param username SFTP登录用户
   * @param password SFTP用户密码
   */
  public SftpUtil(final String host, final String port, final String username, final String password) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
  }

  /**
   * SFTP快速下载(文件/目录)
   * <p>无需实例化SftpUtil类;其他复杂功能->实例化SftpUtil类并自行组装</p>
   * <p>通过下载的保存目录来判断->SFTP目录是否存在</p>
   *
   * @param host                SFTP主机
   * @param port                SFTP端口
   * @param username            SFTP用户
   * @param password            SFTP秘钥
   * @param sftpPath            SFTP文件/目录路径
   * @param localPath           下载文件/目录保存路径
   * @param excludeFileNameList 排除的文件名称列表(目录下载时有效,文件下载时设置为[null])
   * @param excludeExtList      排除的扩展名(目录下载时有效,文件下载时设置为[null])
   * @param isClearFirst        是否需要在下载前删除本地文件或目录
   */
  public static void quickGet(final String host, final String port, final String username, final String password,
                              final String sftpPath, final String localPath,
                              final List<String> excludeFileNameList, final List<String> excludeExtList,
                              final boolean isClearFirst) {
    SftpUtil sftpUtil = null;
    try {
      // 清理本地文件或目录
      if (isClearFirst) FileUtil.delete(localPath);

      // SFTP工具类实例化 并 开启SFTP通道
      sftpUtil = new SftpUtil(host, port, username, password);
      sftpUtil.openChannel();
      // SFTP文件/目录下载至本地
      sftpUtil.get(sftpPath, localPath, excludeFileNameList, excludeExtList);
    } catch (SftpException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new SftpException(String.format("下载文件/目录[%s]失败", sftpPath), ex);
    } finally {
      if (sftpUtil != null) sftpUtil.closeChannel(); // 通道SFTP关闭
    }
  }

  /**
   * SFTP快速上传(文件/目录)
   * <p>无需实例化SftpUtil类;其他复杂功能->实例化SftpUtil类并自行组装</p>
   * <p>通过上传文件目录来判断->是否可以有上传数据</p>
   *
   * @param host                SFTP主机
   * @param port                SFTP端口
   * @param username            SFTP用户
   * @param password            SFTP秘钥
   * @param localPath           上传文件/目录路径
   * @param sftpPath            SFTP保存/目录文件路径
   * @param excludeFileNameList 排除的文件名称列表(目录下载时有效,文件下载时设置为[null])
   * @param excludeExtList      排除的扩展名(目录下载时有效,文件下载时设置为[null])
   * @param isClearFirst        是否需要在上传前删除SFTP文件或目录
   */
  public static void quickPut(final String host, final String port, final String username, final String password,
                              final String localPath, final String sftpPath,
                              final List<String> excludeFileNameList, final List<String> excludeExtList,
                              final boolean isClearFirst) {
    SftpUtil sftpUtil = null;
    try {
      // SFTP工具类实例化 并 开启SFTP通道
      sftpUtil = new SftpUtil(host, port, username, password);
      sftpUtil.openChannel();
      // 清理SFTP文件/目录
      if (isClearFirst) sftpUtil.rm(sftpPath);
      // 本地文件/目录上传至SFTP
      sftpUtil.put(localPath, sftpPath, excludeFileNameList, excludeExtList);
    } catch (SftpException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new SftpException(String.format("上传文件/目录[%s]失败", localPath), ex);
    } finally {
      if (sftpUtil != null) sftpUtil.closeChannel(); // 通道SFTP关闭
    }
  }

  // 防止SFTP资源未被释放
  @PreDestroy
  public void destroy() {
    this.closeChannel();
  }

  /**
   * 开启SFTP通道
   */
  public void openChannel() {
    try {
      // 获取Session
      this.session = new JSch().getSession(username, host, StringUtil.isBlank(port) ? SFTP_DEFAULT_PORT : Integer.parseInt(port));
      if (StringUtil.notBlank(password)) this.session.setPassword(password);
      this.session.setConfig("StrictHostKeyChecking", "no");
      this.session.connect();

      // 开启SFTP通道
      Channel channel = this.session.openChannel(CHANNEL_TYPE_SFTP);
      channel.connect();
      this.channelSftp = (ChannelSftp) channel; // 转换为ChannelSftp对象
      this.rootPath = StringUtil.removeEnd(channelSftp.pwd(), FileUtil.PATH_SEPARATOR); // 用户根路径
    } catch (Exception ex) {
      throw new SftpException(String.format("SFTP服务器连接失败[主机:%s,端口:%s,用户:%s,秘钥:%s]", host, StringUtil.isBlank(port) ? SFTP_DEFAULT_PORT : port, username, password), ex);
    }
  }

  /**
   * 关闭SFTP通道
   */
  public void closeChannel() {
    if (channelSftp != null) channelSftp.disconnect();
    if (session != null) session.disconnect();
    if (rootPath != null) rootPath = null;
  }

  /**
   * 下载(文件/目录)
   *
   * @param sftpPath            SFTP文件/目录路径
   * @param localPath           本地文件/目录路径
   * @param excludeFileNameList 排除的文件名称列表(目录下载时有效,文件下载时设置为[null])
   * @param excludeExtList      排除的扩展名(目录下载时有效,文件下载时设置为[null])
   * @throws Exception
   */
  public void get(final String sftpPath, final String localPath,
                  final List<String> excludeFileNameList, final List<String> excludeExtList) throws Exception {

    String sftpAbsPath = getAbsolutePath(sftpPath);

    // SFTP路径不存在 -> 退出
    if (!this.isExists(sftpAbsPath)) {
      log.info(String.format("[%s]文件/目录[%s]不存在!", "get", sftpAbsPath));
      return;
    }

    OutputStream dfOutputStream = null;
    try {
      if (this.isDir(sftpAbsPath)) {
        /**
         * 目录下载(递归)
         */
        Vector<ChannelSftp.LsEntry> vLsEntry = channelSftp.ls(sftpAbsPath);
        String iFileName;
        for (ChannelSftp.LsEntry itemLsEntry : vLsEntry) {
          iFileName = itemLsEntry.getFilename();
          if (!iFileName.startsWith(FileUtil.NAME_EXCLUDE_PREFIX) && (null == excludeFileNameList || !excludeFileNameList.contains(iFileName)) &&
              (null == excludeExtList || !excludeExtList.contains(FileUtil.getExtension(iFileName).toLowerCase()))) {
            get(FileUtil.getChildPath(sftpAbsPath, iFileName), FileUtil.getChildPath(localPath, iFileName), excludeFileNameList, excludeExtList);
          }
        }
        log.info(String.format("[%s]SFTP下载(目录)完成: [%s] -> [%s]", "get", sftpAbsPath, localPath));
      } else {
        /**
         * 文件下载(覆盖模式)
         */
        dfOutputStream = new FileOutputStream(FileUtil.create(localPath), false);
        channelSftp.get(sftpAbsPath, dfOutputStream);
        dfOutputStream.close();
        log.info(String.format("[%s]SFTP下载(文件)完成: [%s] -> [%s]", "get", sftpAbsPath, localPath));
      }
    } catch (Exception ex) {
      throw new SftpException(String.format("下载文件/目录[%s]失败", sftpAbsPath), ex);
    } finally {
      if (dfOutputStream != null) {
        dfOutputStream.close();
      }
    }

  }

  /**
   * 上传(文件/目录)
   *
   * @param localPath           本地文件/目录路径
   * @param sftpPath            SFTP文件/目录路径
   * @param excludeFileNameList 排除的文件名称列表(目录上传时有效,文件下载时设置为[null])
   * @param excludeExtList      排除的扩展名(目录上传时有效,文件下载时设置为[null])
   * @throws Exception
   */
  public void put(final String localPath, final String sftpPath,
                  final List<String> excludeFileNameList, final List<String> excludeExtList) throws Exception {

    // 本路路径不存在 -> 退出
    if (!FileUtil.isExists(localPath)) {
      log.info(String.format("[%s]本地文件/目录[%s]不存在", "put", localPath));
      return;
    }

    File localFile = new File(localPath);
    InputStream dfInputStream = null;

    String sftpAbsPath = getAbsolutePath(sftpPath);

    try {
      if (localFile.isDirectory()) {
        /**
         * 目录上传(递归)
         */
        String iFileName;
        for (File iFile : localFile.listFiles()) {
          iFileName = iFile.getName();
          if (!iFileName.startsWith(FileUtil.NAME_EXCLUDE_PREFIX) && (null == excludeFileNameList || !excludeFileNameList.contains(iFileName)) &&
              (null == excludeExtList || !excludeExtList.contains(FileUtil.getExtension(iFileName).toLowerCase()))) {
            put(iFile.getAbsolutePath(), FileUtil.getChildPath(sftpAbsPath, iFileName), excludeFileNameList, excludeExtList);
          }
        }
        log.info(String.format("[%s]SFTP上传(目录)完成: [%s] -> [%s]", "put", localPath, sftpAbsPath));
      } else {
        /**
         * 文件上传
         */
        // SFTP上传目录: 不存在 -> 创建
        String sftDirPath = FileUtil.getParentPath(sftpAbsPath);
        if (!isExists(sftDirPath)) channelSftp.mkdir(sftDirPath);
        // 切换至SFTP上传目录
        channelSftp.cd(sftDirPath);
        dfInputStream = new FileInputStream(localFile);
        // 文件上传(覆盖模式)
        channelSftp.put(dfInputStream, new String(localFile.getName().getBytes(), StandardCharsets.UTF_8));
        log.info(String.format("[%s]SFTP上传(文件)完成: [%s] -> [%s]", "put", localPath, sftpAbsPath));
      }
    } catch (Exception ex) {
      throw new SftpException(String.format("上传文件/目录[%s]失败", sftpAbsPath), ex);
    } finally {
      if (dfInputStream != null) {
        dfInputStream.close();
      }
    }

  }

  /**
   * 删除(文件/目录)
   *
   * @param sftpPath 文件或目录路径
   */
  public void rm(final String sftpPath) {
    try {
      String sftpAbsPath = getAbsolutePath(sftpPath);
      if (isExists(sftpAbsPath)) {
        if (isDir(sftpAbsPath)) {
          /**
           * 目录删除(递归)
           */
          Vector<ChannelSftp.LsEntry> vLsEntry = channelSftp.ls(sftpAbsPath);
          String iFileName;
          for (ChannelSftp.LsEntry itemLsEntry : vLsEntry) {
            iFileName = itemLsEntry.getFilename();
            if (!iFileName.startsWith(FileUtil.NAME_EXCLUDE_PREFIX)) {
              rm(FileUtil.getChildPath(sftpAbsPath, iFileName));
            }
          }
          channelSftp.rmdir(sftpAbsPath);
          log.info(String.format("SFTP删除(目录)[%s]完成", sftpAbsPath));
        } else {
          /**
           * 文件删除
           */
          channelSftp.rm(sftpAbsPath);
          log.info(String.format("SFTP删除(文件)[%s]完成", sftpAbsPath));
        }
      }
    } catch (Exception ex) {
      throw new SftpException(String.format("删除文件/目录[%s]失败", sftpPath), ex);
    }
  }

  /**
   * 路径是否存在
   *
   * @param sftPath 文件或目录路径
   * @return 判断结果
   */
  public boolean isExists(final String sftPath) {
    try {
      channelSftp.lstat(getAbsolutePath(sftPath));
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * 是否为目录
   *
   * @param sftPath 文件或目录路径
   * @return 判断结果
   */
  public boolean isDir(final String sftPath) {
    try {
      return channelSftp.lstat(this.getAbsolutePath(sftPath)).isDir();
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * 列出目录下最新的文件名
   * <p>文件名逆序排序的第1件</p>
   *
   * @param sftpDirPath 目录路径
   * @param sftpFileExt 扩展名(null->全部)
   * @return 最新文件的名称
   */
  public String getLastFileByName(final String sftpDirPath, final String sftpFileExt) {

    try {
      String sftDirAbsPath = this.getAbsolutePath(sftpDirPath);

      channelSftp.cd(sftDirAbsPath);
      Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(sftDirAbsPath);

      // SFTP目录下无文件
      if (ListUtil.isEmpty(fileList)) return null;

      // 拼装排序列表
      List<String> fileNameList = new ArrayList<>();
      for (ChannelSftp.LsEntry entry : fileList) {
        if (!entry.getFilename().startsWith(FileUtil.NAME_EXCLUDE_PREFIX)) {
          if (StringUtil.notEmpty(sftpFileExt) && sftpFileExt.equals(FileUtil.getExtension(entry.getFilename()))) {
            fileNameList.add(entry.getFilename());
          } else if (StringUtil.isEmpty(sftpFileExt)) {
            fileNameList.add(entry.getFilename());
          }
        }
      }

      // 未匹配到任何文件
      if (ListUtil.isEmpty(fileNameList)) return null;

      // 按照文件名称逆序排序
      fileNameList.sort(
          new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
              return o1.compareTo(o2) > 0 ? -1 : 1;
            }
          }
      );

      return fileNameList.get(0);
    } catch (Exception ex) {
      throw new SftpException(String.format("获取目录[%s]下最新文件名失败", sftpDirPath), ex);
    }
  }

  /**
   * 获取绝对路径
   *
   * @param sftpPath SFTP路径(相对/绝对)
   * @return SFTP绝对路径
   */
  private String getAbsolutePath(final String sftpPath) {
    String stdSftpPath = StringUtil.removeEnd(sftpPath, FileUtil.PATH_SEPARATOR);
    return new File(stdSftpPath).isAbsolute() ? stdSftpPath :
        rootPath.concat(FileUtil.PATH_SEPARATOR).concat(StringUtil.removeStart(stdSftpPath, FileUtil.PATH_RELATIVE_CURRENT));
  }

}
