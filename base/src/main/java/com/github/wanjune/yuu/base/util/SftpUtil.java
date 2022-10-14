package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.exception.SftpException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * SFTP工具类
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
  // 排除文件名称前缀为"."的文件(系统文件或隐藏文件)
  private static final String EXCLUDE_PREFIX = ".";

  /*
   * FTP配置属性
   */
  private final String host;
  private final String port;
  private final String username;
  private final String password;

  private Session session = null;
  private ChannelSftp channelSftp = null;

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
      if (StringUtil.isNotBlank(password)) this.session.setPassword(password);
      this.session.setConfig("StrictHostKeyChecking", "no");
      this.session.connect();

      // 开启SFTP通道
      Channel channel = this.session.openChannel(CHANNEL_TYPE_SFTP);
      channel.connect();
      this.channelSftp = (ChannelSftp) channel; // 转换为ChannelSftp对象
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
  public void download(final String sftpFilePath, final String localFilePath, final List<String> excludeFileNameList,
                       final List<String> extList) throws Exception {

    // SFTP路径不存在 -> 退出
    if (!this.isExist(sftpFilePath)) {
      log.info(String.format("[%s]文件/目录[%s]不存在 - 退出处理!", "download", sftpFilePath));
      return;
    }

    OutputStream dfOutputStream = null;
    try {
      if (this.isDir(sftpFilePath)) {
        /**
         * 目录下载(递归)
         */
        Vector<ChannelSftp.LsEntry> vLsEntry = channelSftp.ls(sftpFilePath);
        String iFileName;
        for (ChannelSftp.LsEntry itemLsEntry : vLsEntry) {
          iFileName = itemLsEntry.getFilename();
          if ((null == excludeFileNameList || !excludeFileNameList.contains(iFileName)) &&
              (null == extList || extList.contains(FileUtil.getExtension(iFileName).toLowerCase())) &&
              !iFileName.startsWith(EXCLUDE_PREFIX)) {
            download(FileUtil.getChildPath(sftpFilePath, iFileName), FileUtil.getChildPath(localFilePath, iFileName), excludeFileNameList, extList);
          }
        }
        log.info(String.format("[%s]SFTP下载(目录)完成: [%s] -> [%s]", "download", sftpFilePath, localFilePath));
      } else {
        /**
         * 文件下载
         */
        dfOutputStream = new FileOutputStream(FileUtil.create(localFilePath), false);
        channelSftp.get(sftpFilePath, dfOutputStream);
        dfOutputStream.close();
        log.info(String.format("[%s]SFTP下载(文件)完成: [%s] -> [%s]", "download", sftpFilePath, localFilePath));
      }
    } catch (Exception ex) {
      throw new SftpException(String.format("下载文件/目录[%s]失败", sftpFilePath), ex);
    } finally {
      if (dfOutputStream != null) {
        dfOutputStream.close();
      }
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
      channelSftp.lstat(sftpFilePath);
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
      return channelSftp.lstat(sftpFilePath).isDir();
    } catch (Exception ex) {
      //throw new SftpException(String.format("判断路径[%s]是否为目录失败", sftpFilePath), ex);
      return false;
    }
  }

  /**
   * 列出当前目录下最新的文件名
   *
   * @param sftpFilePath 目录路径
   * @param fileExt      文件扩展名
   * @return 文件名
   */
  public String getLastFileByName(final String sftpFilePath, final String fileExt) {

    try {
      channelSftp.cd(sftpFilePath);
      Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(sftpFilePath);

      // 获取文件列表为空 -> 直接返回NULL
      if (fileList == null) return null;

      // 拼装排序列表
      List<String> fileNameList = new ArrayList<>();
      for (ChannelSftp.LsEntry entry : fileList) {
        if (fileExt.equals(FileUtil.getExtension(entry.getFilename()))) fileNameList.add(entry.getFilename());
      }
      // 按照文件名称逆序排序
      fileNameList.sort(
          new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
              return o1.compareTo(o2) > 0 ? -1 : 1;
            }
          }
      );
      // 逆序排序的第一个
      return fileNameList.get(0);
    } catch (Exception ex) {
      throw new SftpException(String.format("获取目录[%s]下最新文件名失败", sftpFilePath), ex);
    }
  }

}
