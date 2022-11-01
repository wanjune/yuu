package com.github.wanjune.yuu.util;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.github.wanjune.yuu.exception.OssException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云OSS工具类
 *
 * @author wanjune
 * @since 2020-09-01
 */
@Component
@Slf4j
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

  private final String bucket;
  private final OSS ossClient;

  public OssUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucket) {
    this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, this.getDefConf());
    this.bucket = bucket;
  }

  /**
   * 关闭OSS客户端
   */
  @PreDestroy
  public void close() {
    if (ossClient != null) ossClient.shutdown();
  }

  /**
   * OSS文件上传
   *
   * @param ossFilePath   OSS文件路径
   * @param localFilePath 本地文件路径
   */
  public void putFile(String ossFilePath, String localFilePath) throws Exception {
    try {
      if (new File(localFilePath).length() <= FILE_SINGLE_LIMIT_LENGTH) {
        putFileSingle(ossFilePath, localFilePath);
      } else {
        putFilePart(ossFilePath, localFilePath);
      }
    } catch (Exception ex) {
      throw new OssException(String.format("上传文件[%s]失败", ossFilePath), ex);
    }
  }

  /**
   * OSS文件删除
   *
   * @param ossFilePath OSS文件路径
   */
  public void deleteFile(String ossFilePath) {
    try {
      deleteObject(ossFilePath);
    } catch (Exception ex) {
      throw new OssException(String.format("删除文件[%s]失败", ossFilePath), ex);
    }
  }

  /**
   * OSS目录删除
   *
   * @param ossDirPath OSS目录路径
   */
  public void deleteDir(String ossDirPath) {
    try {
      List<String> objPaths = this.listDir(ossDirPath);
      if (ListUtil.notEmpty(objPaths)) {
        for (String objPath : objPaths) {
          deleteFile(objPath);
        }
      }
      deleteObject(ossDirPath);
    } catch (Exception ex) {
      throw new OssException(String.format("删除目录[%s]失败", ossDirPath), ex);
    }
  }

  /**
   * 删除OSS文件/目录
   *
   * @param ossPath OSS文件路径
   */
  private void deleteObject(String ossPath) {
    ossClient.deleteObject(this.bucket, ossPath);
  }

  /**
   * OSS文件单个上传
   *
   * @param ossPath   OSS文件路径
   * @param localPath 本地文件路径
   */
  private void putFileSingle(String ossPath, String localPath) throws Exception {
    try {
      PutObjectRequest putObjectSimpleRequest = new PutObjectRequest(this.bucket, ossPath, new File(localPath));
      putObjectSimpleRequest.setMetadata(this.getDefMetadata());
      this.ossClient.putObject(putObjectSimpleRequest);

      log.info(String.format("[%s]OSS上传文件[%s]->[%s]正常结束!", "putObjectSimple", localPath, ossPath));
    } catch (Exception ex) {
      log.error(String.format("[%s]OSS上传文件[%s]->[%s]发生异常!", "putObjectSimple", localPath, ossPath), ex);
      throw ex;
    }
  }

  /**
   * OSS文件分片上传
   *
   * @param ossPath   OSS文件路径
   * @param localPath 本地文件路径
   */
  private void putFilePart(String ossPath, String localPath) throws Exception {

    String uploadId = null;

    try {
      InitiateMultipartUploadRequest putObjectPartRequest = new InitiateMultipartUploadRequest(this.bucket, ossPath);
      putObjectPartRequest.setObjectMetadata(this.getDefMetadata());

      // 初始化分片
      InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(putObjectPartRequest);
      uploadId = uploadResult.getUploadId();
      List<PartETag> partETagList = new ArrayList<>();

      // 计算分片
      final File localFile = new File(localPath);
      long localFileLen = localFile.length();
      int partCount = (int) (localFileLen / FILE_PART_LIMIT_LENGTH);
      if (localFileLen % FILE_PART_LIMIT_LENGTH != 0) {
        partCount++;
      }

      // 遍历分片上传
      UploadPartRequest uploadPartRequest;
      UploadPartResult uploadPartResult;
      long startPos, curPartSize;
      for (int i = 0; i < partCount; i++) {
        startPos = i * FILE_PART_LIMIT_LENGTH;
        curPartSize = (i + 1 == partCount) ? (localFileLen - startPos) : FILE_PART_LIMIT_LENGTH;

        InputStream instream = new FileInputStream(localFile);
        instream.skip(startPos);

        uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(this.bucket);
        uploadPartRequest.setKey(ossPath);
        uploadPartRequest.setUploadId(uploadId);
        uploadPartRequest.setInputStream(instream);
        uploadPartRequest.setPartSize(curPartSize);
        uploadPartRequest.setPartNumber(i + 1); // 设置分片号,范围是1~10000
        uploadPartResult = this.ossClient.uploadPart(uploadPartRequest);
        // 保存OSS的返回结果结果中的PartETag
        partETagList.add(uploadPartResult.getPartETag());
      }

      // 分片合并为一个文件的文件
      ossClient.completeMultipartUpload(new CompleteMultipartUploadRequest(this.bucket, ossPath, uploadId, partETagList));

      log.info(String.format("[%s]OSS分片上传文件[%s]->[%s]正常结束!", "putObjectPart", localPath, ossPath));
    } catch (Exception ex) {
      log.error(String.format("[%s]OSS分片上传文件,[%s]->[%s]发生异常!", "putObjectPart", localPath, ossPath), ex);

      // 取消分片上传
      if (StringUtil.notBlank(uploadId)) {
        try {
          ListPartsRequest listPartsRequest = new ListPartsRequest(this.bucket, ossPath, uploadId);
          listPartsRequest.setMaxParts(100);
          listPartsRequest.setPartNumberMarker(1);
          PartListing partListing = ossClient.listParts(listPartsRequest);

          // 如果分片数据已上传了 -> 删除数据
          if (partListing.getParts().size() > 0) {
            ossClient.abortMultipartUpload(new AbortMultipartUploadRequest(this.bucket, ossPath, uploadId));
            log.info(String.format("[%s]取消文件分片上传[%s]->[%s]正常结束!", "putObjectPart", localPath, ossPath));
          }
        } catch (Exception amuEx) {
          log.error(String.format("[%s]取消文件分片上传,发生异常!", "putObjectPart"), amuEx);
        }
      }

      throw ex;
    }

  }

  /**
   * 获取指定目录下所有目录和文件的路径
   *
   * @param ossDirPath OSS目录路径
   * @return 目录和文件路径列表
   */
  private List<String> listDir(String ossDirPath) {

    List<String> listObjPath = null;

    try {
      ListObjectsV2Result listObjectsV2Result = ossClient.listObjectsV2(this.bucket, ossDirPath);

      if (listObjectsV2Result != null) {
        List<OSSObjectSummary> listOssObj = listObjectsV2Result.getObjectSummaries();

        if (ListUtil.notEmpty(listOssObj)) {
          listObjPath = new ArrayList<>();
          for (OSSObjectSummary item : listOssObj) {
            listObjPath.add(item.getKey());
          }
        }
      }
    } catch (Exception ex) {
      log.error(String.format("[%s]获取目录[%s]内容,发生异常!", "listDir", ossDirPath), ex);
      throw ex;
    }

    return listObjPath;
  }

  /**
   * OSS文件是否存在
   *
   * @param ossPath OSS文件路径
   * @return true:存在 / false:不存在
   */
  public boolean isExists(String ossPath) {
    try {
      return ossClient.doesObjectExist(this.bucket, ossPath);
    } catch (Exception ex) {
      throw new OssException(String.format("检查文件[%s]是否存在失败", ossPath), ex);
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
      String stdOssPath = StringUtil.removeEnd(ossPath, FileUtil.PATH_SEPARATOR); // 去除尾部分隔符
      return stdOssPath.substring(0, stdOssPath.lastIndexOf(FileUtil.PATH_SEPARATOR));
    } catch (Exception ex) {
      throw new OssException(String.format("获取上级目录[%s]失败", ossPath), ex);
    }
  }

  /**
   * OSS的Metadata
   *
   * @return Metadata
   */
  private ObjectMetadata getDefMetadata() {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
    metadata.setHeader("x-oss-forbid-overwrite", "false");
    metadata.setContentEncoding(OSSConstants.DEFAULT_CHARSET_NAME);
    return metadata;
  }

  /**
   * OSS的配置
   *
   * @return OSS配置
   */
  private ClientBuilderConfiguration getDefConf() {
    ClientBuilderConfiguration ossConf = new ClientBuilderConfiguration();
    ossConf.setMaxConnections(MAX_CONNECTIONS);
    ossConf.setSocketTimeout(SOCKET_TIMEOUT);
    ossConf.setConnectionTimeout(CONNECTION_TIMEOUT);
    ossConf.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
    ossConf.setIdleConnectionTime(IDLE_CONNECTION_TIME);
    ossConf.setMaxErrorRetry(MAX_ERROR_RETRY);
    return ossConf;
  }

}
