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
   * File工具类构造函数 - 参数初始化
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
   * SFTP文件快速下载
   * <p>无需实例化SftpUtil类;其他复杂功能->实例化SftpUtil类并自行组装</p>
   * <p>通过下载的保存目录来判断->SFTP目录是否存在</p>
   *
   * @param host                SFTP主机
   * @param port                SFTP端口
   * @param username            SFTP用户
   * @param password            SFTP秘钥
   * @param sftpFilePath        SFTP文件路径
   * @param localFilePath       下载文件保存路径
   * @param excludeFileNameList 排除的文件名称列表(目录下载时有效,文件下载时设置为[null])
   * @param extList             排除的扩展名(目录下载时有效,文件下载时设置为[null])
   * @param isClearFirst        是否需要在下载前删除本地文件或目录
   * @throws Exception 异常
   */
  public static void immediateGet(final String host, final String port, final String username, final String password,
                                  final String sftpFilePath, final String localFilePath,
                                  final List<String> excludeFileNameList, final List<String> extList,
                                  final boolean isClearFirst) throws Exception {
    SftpUtil sftpUtil = null;
    try {
      // 清理本地文件或目录
      if (isClearFirst) FileUtil.delete(localFilePath);

      // SFTP工具类实例化 并 开启SFTP通道
      sftpUtil = new SftpUtil(host, port, username, password);
      sftpUtil.openChannel();
      // SFTP文件/目录上传至本地
      sftpUtil.get(sftpFilePath, localFilePath, excludeFileNameList, extList);
    } catch (SftpException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new SftpException(String.format("下载文件/目录[%s]失败", sftpFilePath), ex);
    } finally {
      if (sftpUtil != null) sftpUtil.closeChannel(); // 通道SFTP关闭
    }
  }

  /**
   * SFTP文件快速上传
   * <p>无需实例化SftpUtil类;其他复杂功能->实例化SftpUtil类并自行组装</p>
   * <p>通过上传文件目录来判断->是否可以有上传数据</p>
   *
   * @param host                SFTP主机
   * @param port                SFTP端口
   * @param username            SFTP用户
   * @param password            SFTP秘钥
   * @param localFilePath       上传文件路径
   * @param sftpFilePath        SFTP保存文件路径
   * @param excludeFileNameList 排除的文件名称列表(目录下载时有效,文件下载时设置为[null])
   * @param extList             排除的扩展名(目录下载时有效,文件下载时设置为[null])
   * @param isClearFirst        是否需要在上传前删除SFTP文件或目录
   * @throws Exception 异常
   */
  public static void immediatePut(final String host, final String port, final String username, final String password,
                                  final String localFilePath, final String sftpFilePath,
                                  final List<String> excludeFileNameList, final List<String> extList,
                                  final boolean isClearFirst) throws Exception {
    SftpUtil sftpUtil = null;
    try {
      // 清理SFTP文件/目录
      if (isClearFirst) sftpUtil.rm(sftpFilePath);

      // SFTP工具类实例化 并 开启SFTP通道
      sftpUtil = new SftpUtil(host, port, username, password);
      sftpUtil.openChannel();
      // 本地文件/目录上传至SFTP
      sftpUtil.put(localFilePath, sftpFilePath, excludeFileNameList, extList);
    } catch (SftpException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new SftpException(String.format("上传文件/目录[%s]失败", localFilePath), ex);
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
   * 开启SFTP通道</p>
   * 端口未设置(空) -> 默认端口[22]
   *
   * @throws SftpException SftpException(勇)
   */
  public void openChannel() throws Exception {

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
      throw new SftpException(String.format("SFTP连接失败[主机:%s,端口:%s,用户:%s,秘钥:%s]", host, StringUtil.isBlank(port) ? SFTP_DEFAULT_PORT : port, username, password), ex);
    }

  }

  /**
   * 关闭SFTP通道
   */
  public void closeChannel() {
    if (channelSftp != null) channelSftp.disconnect();
    if (session != null) session.disconnect();
  }

  /**
   * 文件/目录下载
   *
   * @param sftpFilePath        SFTP文件或目录全路径
   * @param localFilePath       本地文件或目录全路径
   * @param excludeFileNameList 排除的文件名称列表(目录下载时有效,文件下载时设置为[null])
   * @param extList             排除的扩展名(目录下载时有效,文件下载时设置为[null])
   * @throws Exception
   */
  public void get(final String sftpFilePath, final String localFilePath, final List<String> excludeFileNameList,
                  final List<String> extList) throws Exception {

    String sftpFileAbsolutePath = getAbsolutePath(sftpFilePath);

    // SFTP路径不存在 -> 退出
    if (!this.isExist(sftpFileAbsolutePath)) {
      log.info(String.format("[%s]文件/目录[%s]不存在 - 退出处理!", "get", sftpFileAbsolutePath));
      return;
    }

    OutputStream dfOutputStream = null;
    try {
      if (this.isDir(sftpFileAbsolutePath)) {
        /**
         * 目录下载(递归)
         */
        Vector<ChannelSftp.LsEntry> vLsEntry = channelSftp.ls(sftpFileAbsolutePath);
        String iFileName;
        for (ChannelSftp.LsEntry itemLsEntry : vLsEntry) {
          iFileName = itemLsEntry.getFilename();
          if (!iFileName.startsWith(FileUtil.NAME_EXCLUDE_PREFIX) && (null == excludeFileNameList || !excludeFileNameList.contains(iFileName)) &&
              (null == extList || !extList.contains(FileUtil.getExtension(iFileName).toLowerCase()))) {
            get(FileUtil.getChildPath(sftpFileAbsolutePath, iFileName), FileUtil.getChildPath(localFilePath, iFileName), excludeFileNameList, extList);
          }
        }
        log.info(String.format("[%s]SFTP下载(目录)完成: [%s] -> [%s]", "get", sftpFileAbsolutePath, localFilePath));
      } else {
        /**
         * 文件下载(覆盖模式)
         */
        dfOutputStream = new FileOutputStream(FileUtil.create(localFilePath), false);
        channelSftp.get(sftpFileAbsolutePath, dfOutputStream);
        dfOutputStream.close();
        log.info(String.format("[%s]SFTP下载(文件)完成: [%s] -> [%s]", "get", sftpFileAbsolutePath, localFilePath));
      }
    } catch (Exception ex) {
      throw new SftpException(String.format("下载文件/目录[%s]失败", sftpFileAbsolutePath), ex);
    } finally {
      if (dfOutputStream != null) {
        dfOutputStream.close();
      }
    }

  }

  /**
   * 文件/目录上传
   *
   * @param localFilePath       本地文件或目录全路径
   * @param sftpFilePath        SFTP文件或目录全路径
   * @param excludeFileNameList 排除的文件名称列表(目录上传时有效,文件下载时设置为[null])
   * @param extList             排除的扩展名(目录上传时有效,文件下载时设置为[null])
   * @throws Exception
   */
  public void put(final String localFilePath, final String sftpFilePath,
                  final List<String> excludeFileNameList, final List<String> extList) throws Exception {

    // 本路路径不存在 -> 退出
    if (!FileUtil.isExists(localFilePath)) {
      log.info(String.format("[%s]文件/目录[%s]不存在 - 退出处理!", "put", localFilePath));
      return;
    }

    File localFile = new File(localFilePath);
    InputStream dfInputStream = null;

    String sftpFileAbsolutePath = getAbsolutePath(sftpFilePath);

    try {
      if (localFile.isDirectory()) {
        /**
         * 目录上传(递归)
         */
        String iFilePath, iFileName;
        for (File iFile : localFile.listFiles()) {
          iFilePath = iFile.getAbsolutePath();
          iFileName = iFile.getName();
          if (!iFileName.startsWith(FileUtil.NAME_EXCLUDE_PREFIX) && (null == excludeFileNameList || !excludeFileNameList.contains(iFileName)) &&
              (null == extList || !extList.contains(FileUtil.getExtension(iFileName).toLowerCase()))) {
            put(iFilePath, FileUtil.getChildPath(sftpFileAbsolutePath, iFileName), excludeFileNameList, extList);
          }
        }
        log.info(String.format("[%s]SFTP上传(目录)完成: [%s] -> [%s]", "put", localFilePath, sftpFileAbsolutePath));
      } else {
        /**
         * 文件上传
         */
        // SFTP上传目录: 不存在 -> 创建
        String sftDirPath = FileUtil.getParentPath(sftpFileAbsolutePath);
        if (!isExist(sftDirPath)) channelSftp.mkdir(sftDirPath);
        // 切换至SFTP上传目录
        channelSftp.cd(sftDirPath);
        dfInputStream = new FileInputStream(localFile);
        // 文件上传(覆盖模式)
        channelSftp.put(dfInputStream, new String(localFile.getName().getBytes(), StandardCharsets.UTF_8));
        log.info(String.format("[%s]SFTP上传(文件)完成: [%s] -> [%s]", "put", localFilePath, sftpFileAbsolutePath));
      }
    } catch (Exception ex) {
      throw new SftpException(String.format("上传文件/目录[%s]失败", sftpFileAbsolutePath), ex);
    } finally {
      if (dfInputStream != null) {
        dfInputStream.close();
      }
    }

  }

  /**
   * 删除文件/目录
   *
   * @param sftpFilePath 文件或目录路径
   */
  public void rm(final String sftpFilePath) {
    try {
      String sftpFileAbsolutePath = getAbsolutePath(sftpFilePath);
      if (isExist(sftpFileAbsolutePath)) {
        if (isDir(sftpFileAbsolutePath)) {
          /**
           * 目录删除(递归)
           */
          Vector<ChannelSftp.LsEntry> vLsEntry = channelSftp.ls(sftpFileAbsolutePath);
          String iFileName;
          for (ChannelSftp.LsEntry itemLsEntry : vLsEntry) {
            iFileName = itemLsEntry.getFilename();
            if (!iFileName.startsWith(FileUtil.NAME_EXCLUDE_PREFIX)) {
              rm(FileUtil.getChildPath(sftpFileAbsolutePath, iFileName));
            }
          }
          channelSftp.rmdir(sftpFileAbsolutePath);
          log.info(String.format("[%s]SFTP删除(目录)完成: [%s]", "rm", sftpFileAbsolutePath));
        } else {
          /**
           * 文件删除
           */
          channelSftp.rm(sftpFileAbsolutePath);
          log.info(String.format("[%s]SFTP删除(文件)完成: [%s]", "rm", sftpFileAbsolutePath));
        }
      }
    } catch (Exception ex) {
      throw new SftpException(String.format("删除文件/目录[%s]失败", sftpFilePath), ex);
    }
  }

  /**
   * 文件或目录是否存在
   *
   * @param sftpFilePath 文件或目录路径
   * @return 判断结果
   */
  public boolean isExist(final String sftpFilePath) {
    try {
      channelSftp.lstat(getAbsolutePath(sftpFilePath));
      return true;
    } catch (Exception ex) {
      //throw new SftpException(String.format("判断路径[%s]是否存在失败", sftpFilePath), ex);
      return false;
    }
  }

  /**
   * 判断是否为目录
   *
   * @param sftpFilePath 文件或目录路径
   * @return 判断结果
   */
  public boolean isDir(final String sftpFilePath) {
    try {
      return channelSftp.lstat(this.getAbsolutePath(sftpFilePath)).isDir();
    } catch (Exception ex) {
      //throw new SftpException(String.format("判断路径[%s]是否为目录失败", sftpFilePath), ex);
      return false;
    }
  }

  /**
   * 列出目录下最新的文件名
   *
   * @param sftpDirPath 目录路径
   * @param fileExt     文件扩展名(不考虑设null)
   * @return 文件名
   */
  public String getLastFileByName(final String sftpDirPath, final String fileExt) {

    try {
      String sftpFileAbsolutePath = this.getAbsolutePath(sftpDirPath);

      channelSftp.cd(sftpFileAbsolutePath);
      Vector<ChannelSftp.LsEntry> sftpFileList = channelSftp.ls(sftpFileAbsolutePath);

      // SFTP目录下无文件
      if (ListUtil.isEmpty(sftpFileList)) return null;

      // 拼装排序列表
      List<String> fileNameList = new ArrayList<>();
      for (ChannelSftp.LsEntry entry : sftpFileList) {
        if (StringUtil.notEmpty(fileExt) && fileExt.equals(FileUtil.getExtension(entry.getFilename()))) {
          fileNameList.add(entry.getFilename());
        } else if (StringUtil.isEmpty(fileExt)) {
          fileNameList.add(entry.getFilename());
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
   * @param sftpPath SFTP文件/目录的相对/绝对路径
   * @return SFTP文件/目录的绝对路径
   */
  private String getAbsolutePath(final String sftpPath) {
    String strSftpPath = StringUtil.removeEnd(sftpPath, FileUtil.PATH_SEPARATOR);
    return new File(strSftpPath).isAbsolute() ? strSftpPath : rootPath.concat(FileUtil.PATH_SEPARATOR).concat(StringUtil.removeStart(strSftpPath, FileUtil.PATH_RELATIVE_CURRENT));
  }

}
