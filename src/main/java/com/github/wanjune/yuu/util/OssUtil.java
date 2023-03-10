package com.github.wanjune.yuu.util;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.github.wanjune.yuu.exception.OssException;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云OSS工具类
 *
 * @author wanjune
 * @since 2020-09-01
 */
@SuppressWarnings("all")
public class OssUtil {
  // 允许打开的最大HTTP连接数(默认为1024个)
  private static final int MAX_CONNECTIONS = 200;
  // Socket层传输数据的超时时间(默认50000毫秒)
  private static final int SOCKET_TIMEOUT = 300000;
  // 建立连接的超时时间(默认50000毫秒)
  private static final int CONNECTION_TIMEOUT = 120000;
  // 从连接池中获取连接的超时时间[单位:毫秒](默认不超时)
  private static final int CONNECTION_REQUEST_TIMEOUT = 60000;
  // 连接空闲超时时间(默认60000毫秒)
  private static final int IDLE_CONNECTION_TIME = 60000;
  // 失败请求重试次数(默认3次)
  private static final int MAX_ERROR_RETRY = 20;

  // 上传单个文件大小限制(2GB)
  private static final long FILE_SINGLE_LIMIT_LENGTH = 2 * 1024 * 1024 * 1024L;
  // 上传分片文件大小限制(1GB)
  private static final long FILE_PART_LIMIT_LENGTH = 1 * 1024 * 1024 * 1024L;

  // 文件读写缓存大小
  private static final int BUF_SIZE = 4096;

  private final String bucket;
  private final OSS ossClient;

  /**
   * 阿里云OSS工具类构造函数 - 参数初始化
   *
   * @param endpoint        阿里云OSS-endpoint
   * @param accessKeyId     阿里云OSS-accessKeyId
   * @param accessKeySecret 阿里云OSS-accessKeySecret
   * @param bucket          阿里云OSS-bucket
   */
  public OssUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucket) {
    this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, this.config());
    this.bucket = bucket;
  }

  /**
   * OSS快速上传(文件/目录)
   * <p>无需实例化OssUtil类;其他复杂功能->实例化OssUtil类并自行组装</p>
   *
   * @param endpoint            阿里云OSS-endpoint
   * @param accessKeyId         阿里云OSS-accessKeyId
   * @param accessKeySecret     阿里云OSS-accessKeySecret
   * @param bucket              阿里云OSS-bucket
   * @param localPath           本地文件路径
   * @param ossPath             OSS文件路径
   * @param excludeFileNameList 排除的文件名称列表(目录上传时有效,文件下载时设置为[null])
   * @param excludeExtList      排除的扩展名(目录上传时有效,文件下载时设置为[null])
   * @param isClearFirst        是否在上传前删除OSS文件或目录
   */
  public static void quickUpload(final String endpoint, final String accessKeyId, final String accessKeySecret, final String bucket,
                                 final String localPath, final String ossPath,
                                 final List<String> excludeFileNameList, final List<String> excludeExtList,
                                 final boolean isClearFirst) throws Exception {
    OssUtil ossUtil = null;
    try {
      ossUtil = new OssUtil(endpoint, accessKeyId, accessKeySecret, bucket);
      if (isClearFirst) ossUtil.delete(ossPath);
      ossUtil.upload(ossPath, localPath, excludeFileNameList, excludeExtList);
    } catch (Exception ex) {
      throw ex;
    } finally {
      if (ossUtil != null) ossUtil.close();
    }
  }


  /**
   * OSS快速下载(文件/目录)
   * <p>无需实例化OssUtil类;其他复杂功能->实例化OssUtil类并自行组装</p>
   *
   * @param endpoint        阿里云OSS-endpoint
   * @param accessKeyId     阿里云OSS-accessKeyId
   * @param accessKeySecret 阿里云OSS-accessKeySecret
   * @param bucket          阿里云OSS-bucket
   * @param ossPath         OSS文件路径
   * @param localPath       本地文件路径
   * @param isClearFirst    是否在下载前删除本地文件或目录
   * @throws Exception 异常
   */
  public static void quickDownload(final String endpoint, final String accessKeyId, final String accessKeySecret, final String bucket,
                                   final String ossPath, final String localPath,
                                   final boolean isClearFirst) throws Exception {
    OssUtil ossUtil = null;
    try {
      if (isClearFirst) FileUtil.delete(localPath);
      ossUtil = new OssUtil(endpoint, accessKeyId, accessKeySecret, bucket);
      FileUtil.delete(localPath);
      ossUtil.download(ossPath, localPath);
    } catch (Exception ex) {
      throw ex;
    } finally {
      if (ossUtil != null) ossUtil.close();
    }
  }

  /**
   * 关闭OSS客户端
   */
  public void close() {
    if (ossClient != null) ossClient.shutdown();
  }

  // 防止OSS资源未被释放
  @PreDestroy
  public void destroy() {
    this.close();
  }

  /**
   * 上传(文件/目录)
   *
   * @param localPath           本地文件/目录路径
   * @param ossPath             OSS文件/目录路径
   * @param excludeFileNameList 排除的文件名称列表(目录上传时有效,文件下载时设置为[null])
   * @param excludeExtList      排除的扩展名(目录上传时有效,文件下载时设置为[null])
   */
  public void upload(final String localPath, final String ossPath,
                     final List<String> excludeFileNameList, final List<String> excludeExtList) throws Exception {

    // 本地文件/目录不存在 -> 退出
    if (!FileUtil.isExists(localPath)) throw new OssException(String.format("本地文件/目录[%s]不存在", localPath));

    File localFile = new File(localPath);
    try {
      if (localFile.isDirectory()) {
        // 目录上传(递归)
        File[] localFiles = localFile.listFiles();
        if (localFiles != null && localFiles.length > 0) {
          String iFileName;
          for (File iFile : localFiles) {
            iFileName = iFile.getName();
            if (!iFileName.startsWith(FileUtil.NAME_EXCLUDE_PREFIX) &&
                (null == excludeFileNameList || !excludeFileNameList.contains(iFileName)) &&
                (null == excludeExtList || !excludeExtList.contains(FileUtil.getExtension(iFileName).toLowerCase()))) {
              upload(iFile.getAbsolutePath(), FileUtil.getChildPath(ossPath, iFileName), excludeFileNameList, excludeExtList);
            }
          }
        }
      } else {
        // 文件上传
        if (new File(localPath).length() <= FILE_SINGLE_LIMIT_LENGTH) {
          ossClient.putObject(new PutObjectRequest(this.bucket, ossPath, new File(localPath), this.metadata()));
        } else {
          putFilePart(localPath, ossPath);
        }
      }
    } catch (Exception ex) {
      throw new OssException(String.format("本地文件/目录[%s]上传失败", localPath), ex);
    }
  }

  /**
   * 下载OSS文件/目录至本地
   * <p>阿里OSS目前无法判断OSS路径是否为目录,因此只支持文件上传</p>
   *
   * @param ossPath   OSS文件/目录路径
   * @param localPath 本地文件/目录路径
   */
  public void download(String ossPath, String localPath) throws Exception {

    try {
      if (isFileExists(ossPath)) {
        this.downloadFile(ossPath, localPath);
      } else if (isDirExists(FileUtil.getDirPath(ossPath))) {
        for (String ossFilePath : this.listFiles(FileUtil.getDirPath(ossPath))) {
          this.downloadFile(ossFilePath, ossFilePath.replaceFirst(FileUtil.getDirPath(ossPath), FileUtil.getDirPath(localPath)));
        }
      } else {
        throw new OssException(String.format("OSS文件/目录[%s]不存在", ossPath));
      }

    } catch (Exception ex) {
      if (ex instanceof OssException) throw ex;
      throw new OssException(String.format("OSS文件/目录[%s]下载失败", ossPath), ex);
    }
  }

  /**
   * 删除OSS文件/目录
   * <p>删除目录:删除该目录及目录内所有内容</p>
   *
   * @param ossPath OSS文件/目录路径
   */
  public void delete(String ossPath) {
    try {
      if (isFileExists(ossPath)) {
        ossClient.deleteObject(this.bucket, ossPath);
      } else if (isDirExists(FileUtil.getDirPath(ossPath))) {
        String nextMarker = null;
        ObjectListing objectListing = null;
        List<String> listObjects = new ArrayList<>();
        do {
          objectListing = ossClient.listObjects(new ListObjectsRequest(this.bucket).withPrefix(FileUtil.getDirPath(ossPath)).withMarker(nextMarker));
          if (objectListing.getObjectSummaries().size() > 0) {
            for (OSSObjectSummary item : objectListing.getObjectSummaries()) {
              listObjects.add(item.getKey());
            }
            ossClient.deleteObjects(new DeleteObjectsRequest(this.bucket).withKeys(listObjects));
          }
          nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
      }

    } catch (Exception ex) {
      throw new OssException(String.format("删除文件/目录[%s]失败", ossPath), ex);
    }
  }

  /**
   * OSS文件分片上传
   *
   * @param localFilePath 本地文件路径
   * @param ossFilePath   OSS文件路径
   * @throws Exception 异常
   */
  private void putFilePart(String localFilePath, String ossFilePath) throws Exception {

    String partUploadId = null;
    InputStream localInputStream = null;

    try {
      // 初始化分片
      partUploadId = ossClient.initiateMultipartUpload(new InitiateMultipartUploadRequest(this.bucket, ossFilePath, this.metadata())).getUploadId();
      List<PartETag> partETagList = new ArrayList<>();

      // 计算分片
      final File localFile = new File(localFilePath);
      long localFileLen = localFile.length();

      int partCount = (int) (localFileLen / FILE_PART_LIMIT_LENGTH);
      if (localFileLen % FILE_PART_LIMIT_LENGTH != 0) partCount++;

      // 遍历分片上传
      UploadPartRequest uploadPartRequest;
      UploadPartResult uploadPartResult;
      long startPos, partSize;
      for (int i = 0; i < partCount; i++) {
        startPos = i * FILE_PART_LIMIT_LENGTH;
        partSize = (i + 1 == partCount) ? (localFileLen - startPos) : FILE_PART_LIMIT_LENGTH;

        localInputStream = new FileInputStream(localFile);
        localInputStream.skip(startPos);

        uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(this.bucket);
        uploadPartRequest.setKey(ossFilePath);
        uploadPartRequest.setUploadId(partUploadId);
        uploadPartRequest.setInputStream(localInputStream);
        uploadPartRequest.setPartSize(partSize);
        uploadPartRequest.setPartNumber(i + 1); // 设置分片号,范围是1~10000
        uploadPartResult = ossClient.uploadPart(uploadPartRequest);
        // 保存OSS的返回结果结果中的PartETag
        partETagList.add(uploadPartResult.getPartETag());
      }

      // 分片合并为一个文件的文件
      ossClient.completeMultipartUpload(new CompleteMultipartUploadRequest(this.bucket, ossFilePath, partUploadId, partETagList));
    } catch (Exception ex) {
      // 取消分片上传
      if (StringUtil.notBlank(partUploadId)) {
        ListPartsRequest listPartsRequest = new ListPartsRequest(this.bucket, ossFilePath, partUploadId);
        listPartsRequest.setMaxParts(100);
        listPartsRequest.setPartNumberMarker(1);
        PartListing partListing = ossClient.listParts(listPartsRequest);
        // 如果分片数据已上传了 -> 删除数据
        if (partListing.getParts().size() > 0) {
          ossClient.abortMultipartUpload(new AbortMultipartUploadRequest(this.bucket, ossFilePath, partUploadId));
        }
      }
      throw ex;
    } finally {
      if (localInputStream != null) localInputStream.close();
    }

  }

  /**
   * 获取目录下文件的路径列表
   * <p>包含所有子目录下的文件<p/>
   *
   * @param ossDirPath OSS目录路径
   * @return 文件路径列表
   */
  private List<String> listFiles(String ossDirPath) {
    String stdOssDirPath = FileUtil.getDirPath(ossDirPath);
    List<String> listObjects = null;
    try {
      ListObjectsV2Result listObjectsV2Result = ossClient.listObjectsV2(this.bucket, stdOssDirPath);

      if (listObjectsV2Result != null) {
        List<OSSObjectSummary> listOssObj = listObjectsV2Result.getObjectSummaries();
        if (ListUtil.notEmpty(listOssObj)) {
          listObjects = new ArrayList<>();
          for (OSSObjectSummary item : listOssObj) {
            listObjects.add(item.getKey());
          }
        }
      }
    } catch (Exception ex) {
      // Nothing
    }
    return listObjects;
  }

  /**
   * OSS目录是否存在
   *
   * @param ossDirPath OSS目录路径
   * @return true:存在 / false:不存在
   */
  public boolean isDirExists(String ossDirPath) {
    try {
      return ListUtil.notEmpty(this.listFiles(ossDirPath));
    } catch (Exception ex) {
      throw new OssException(String.format("检查文件[%s]是否存在失败", ossDirPath), ex);
    }
  }

  /**
   * OSS文件是否存在
   *
   * @param ossFilePath OSS文件路径
   * @return true:存在 / false:不存在
   */
  public boolean isFileExists(String ossFilePath) {
    try {
      return ossClient.doesObjectExist(this.bucket, ossFilePath);
    } catch (Exception ex) {
      throw new OssException(String.format("检查文件[%s]是否存在失败", ossFilePath), ex);
    }
  }

  /**
   * 获取文件/目录的上级目录路径
   *
   * @param ossPath OSS文件/目录
   * @return 上级目录路径
   */
  public String getParentPath(String ossPath) {
    try {
      String stdOssPath = FileUtil.getDirPath(ossPath);
      return stdOssPath.substring(0, stdOssPath.lastIndexOf(FileUtil.PATH_SEPARATOR));
    } catch (Exception ex) {
      throw new OssException(String.format("获取上级目录[%s]失败", ossPath), ex);
    }
  }

  /**
   * 下载OSS文件至本地
   * <p>阿里OSS目前无法判断OSS路径是否为目录,因此只支持文件上传</p>
   *
   * @param ossFilePath   OSS文件路径
   * @param localFilePath 本地文件路径
   */
  private void downloadFile(String ossFilePath, String localFilePath) throws Exception {
    InputStream ossInputStream = null;
    BufferedOutputStream localOutputStream = null;
    try {
      // 读取OSS文件流
      ossInputStream = ossClient.getObject(this.bucket, ossFilePath).getObjectContent();
      // 写入本地文件流
      localOutputStream = new BufferedOutputStream(new FileOutputStream(FileUtil.create(localFilePath), false));

      // 写入本地文件
      if (ossInputStream != null) {
        byte[] bytes = new byte[BUF_SIZE];
        int len;
        // 写入输出流
        while ((len = ossInputStream.read(bytes)) > 0) localOutputStream.write(bytes, 0, len);
        // 关闭输入的文件流
        ossInputStream.close();
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      if (ossInputStream != null) ossInputStream.close();
      if (localOutputStream != null) localOutputStream.close();
    }
  }

  /**
   * ObjectMetadata
   *
   * @return ObjectMetadata
   */
  private ObjectMetadata metadata() {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
    metadata.setHeader("x-oss-forbid-overwrite", "false");
    metadata.setContentEncoding(OSSConstants.DEFAULT_CHARSET_NAME);
    return metadata;
  }

  /**
   * ClientConfiguration
   *
   * @return ClientConfiguration
   */
  private ClientBuilderConfiguration config() {
    ClientBuilderConfiguration config = new ClientBuilderConfiguration();
    config.setMaxConnections(MAX_CONNECTIONS);
    config.setSocketTimeout(SOCKET_TIMEOUT);
    config.setConnectionTimeout(CONNECTION_TIMEOUT);
    config.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
    config.setIdleConnectionTime(IDLE_CONNECTION_TIME);
    config.setMaxErrorRetry(MAX_ERROR_RETRY);
    return config;
  }

}
