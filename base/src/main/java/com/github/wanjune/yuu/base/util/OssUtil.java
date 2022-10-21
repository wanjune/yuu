package com.github.wanjune.yuu.base.util;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.github.wanjune.yuu.base.exception.OssException;
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
  private static final long FILE_LIMIT_SIZE = 2 * 1024 * 1024 * 1024L;   // 2 GB
  private static final long FILE_PART_SIZE = 1 * 1024 * 1024 * 1024L;   // 1 GB
  private final String bucket;
  private final OSS ossClient;

  public OssUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucket) {
    this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, this.getDefConf());
    this.bucket = bucket;
  }

  /**
   * OSS文件上传
   *
   * @param ossFilePath   OSS文件路径
   * @param localFilePath 本地文件路径
   */
  public void uploadFile(String ossFilePath, String localFilePath) throws Exception {
    try {
      // 计算文件大小
      final File localFile = new File(localFilePath);
      long localFileLength = localFile.length();

      if (localFileLength <= FILE_LIMIT_SIZE) {
        putObjectSimple(ossFilePath, localFilePath);
      } else {
        putObjectPart(ossFilePath, localFilePath);
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
   * @param ossFilePath OSS文件路径
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
   * OSS文件或目录路径
   *
   * @param ossFileOrDirPath OSS文件路径
   */
  private void deleteObject(String ossFileOrDirPath) {
    ossClient.deleteObject(this.bucket, ossFileOrDirPath);
  }

  /**
   * OSS文件单个上传
   *
   * @param ossFilePath   OSS文件路径
   * @param localFilePath 本地文件路径
   */
  private void putObjectSimple(String ossFilePath, String localFilePath) throws Exception {
    try {
      PutObjectRequest putObjectSimpleRequest = new PutObjectRequest(this.bucket, ossFilePath, new File(localFilePath));
      putObjectSimpleRequest.setMetadata(this.getDefMetadata());
      this.ossClient.putObject(putObjectSimpleRequest);

      log.info(String.format("[%s]OSS上传文件[%s]->[%s]正常结束!", "putObjectSimple", localFilePath, ossFilePath));
    } catch (Exception ex) {
      log.error(String.format("[%s]OSS上传文件[%s]->[%s]发生异常!", "putObjectSimple", localFilePath, ossFilePath), ex);
      throw ex;
    }
  }

  /**
   * OSS文件分片上传
   *
   * @param ossFilePath   OSS文件路径
   * @param localFilePath 本地文件路径
   */
  private void putObjectPart(String ossFilePath, String localFilePath) throws Exception {

    String uploadId = null;

    try {
      InitiateMultipartUploadRequest putObjectPartRequest = new InitiateMultipartUploadRequest(this.bucket, ossFilePath);
      putObjectPartRequest.setObjectMetadata(this.getDefMetadata());

      // 初始化分片
      InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(putObjectPartRequest);
      uploadId = uploadResult.getUploadId();
      List<PartETag> partETags = new ArrayList<>();

      // 计算分片
      final File localFile = new File(localFilePath);
      long localFileLength = localFile.length();
      int partCount = (int) (localFileLength / FILE_PART_SIZE);
      if (localFileLength % FILE_PART_SIZE != 0) {
        partCount++;
      }

      // 遍历分片上传
      UploadPartRequest uploadPartRequest;
      UploadPartResult uploadPartResult;
      for (int i = 0; i < partCount; i++) {
        long startPos = i * FILE_PART_SIZE;
        long curPartSize = (i + 1 == partCount) ? (localFileLength - startPos) : FILE_PART_SIZE;

        InputStream instream = new FileInputStream(localFile);
        instream.skip(startPos);

        uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(this.bucket);
        uploadPartRequest.setKey(ossFilePath);
        uploadPartRequest.setUploadId(uploadId);
        uploadPartRequest.setInputStream(instream);
        uploadPartRequest.setPartSize(curPartSize);
        uploadPartRequest.setPartNumber(i + 1); // 设置分片号,范围是1~10000
        uploadPartResult = this.ossClient.uploadPart(uploadPartRequest);
        // 保存OSS的返回结果结果中的PartETag
        partETags.add(uploadPartResult.getPartETag());
      }

      // 分片合并为一个文件的文件
      ossClient.completeMultipartUpload(new CompleteMultipartUploadRequest(this.bucket, ossFilePath, uploadId, partETags));

      log.info(String.format("[%s]OSS分片上传文件[%s]->[%s]正常结束!", "putObjectPart", localFilePath, ossFilePath));
    } catch (Exception ex) {
      log.error(String.format("[%s]OSS分片上传文件,[%s]->[%s]发生异常!", "putObjectPart", localFilePath, ossFilePath), ex);

      // 取消分片上传
      if (StringUtil.notBlank(uploadId)) {
        try {
          ListPartsRequest listPartsRequest = new ListPartsRequest(this.bucket, ossFilePath, uploadId);
          listPartsRequest.setMaxParts(100);
          listPartsRequest.setPartNumberMarker(1);
          PartListing partListing = ossClient.listParts(listPartsRequest);

          // 如果分片数据已上传了 -> 删除数据
          if (partListing.getParts().size() > 0) {
            ossClient.abortMultipartUpload(new AbortMultipartUploadRequest(this.bucket, ossFilePath, uploadId));
            log.info(String.format("[%s]取消文件分片上传[%s]->[%s]正常结束!", "putObjectPart", localFilePath, ossFilePath));
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
   * @param ossFilePath OSS文件路径
   * @return true:存在 / false:不存在
   */
  public boolean isFileExist(String ossFilePath) {
    try {
      return ossClient.doesObjectExist(this.bucket, ossFilePath);
    } catch (Exception ex) {
      throw new OssException(String.format("文件检查[%s]失败", ossFilePath), ex);
    }
  }

  /**
   * 获取文件或目录的上级目录路径
   *
   * @param ossFileOrDir OSS文件或目录
   * @return 上级目录路径
   */
  public String getParent(String ossFileOrDir) {
    try {
      String strFilePath = StringUtil.removeEnd(ossFileOrDir, FileUtil.PATH_SEPARATOR); // 去除尾部分隔符
      return strFilePath.substring(0, strFilePath.lastIndexOf(FileUtil.PATH_SEPARATOR));
    } catch (Exception ex) {
      throw new OssException(String.format("获取上级目录[%s]失败", ossFileOrDir), ex);
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
    ossConf.setMaxConnections(200); // 允许打开的最大HTTP连接数(默认为1024个)
    ossConf.setSocketTimeout(300000); // Socket层传输数据的超时时间(默认50000毫秒)
    ossConf.setConnectionTimeout(120000); // 建立连接的超时时间(默认50000毫秒)
    ossConf.setConnectionRequestTimeout(60000); // 从连接池中获取连接的超时时间[单位:毫秒](默认不超时)
    ossConf.setIdleConnectionTime(60000); // 连接空闲超时时间(默认60000毫秒)
    ossConf.setMaxErrorRetry(20); // 失败请求重试次数(默认3次)
    return ossConf;
  }

  /**
   * 关闭OSS客户端
   */
  @PreDestroy
  public void close() {
    if (ossClient != null) ossClient.shutdown();
  }

}
