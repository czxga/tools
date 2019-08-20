package com.xk.usm.common.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.xk.common.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AliOSSUtil {
	private static final Logger logger = LoggerFactory.getLogger(AliOSSUtil.class);

	private static String endpoint;
	private static String accessKeyId;
	private static String accessKeySecret;
	private static String bucketName;
	private static String downloadUrl;

	static {
		PropertiesUtil propertiesUtil = new PropertiesUtil("common.properties",
				PropertiesUtil.SourceType.ResourceType);
		endpoint = propertiesUtil.getString("xk.recipe.oss.endpoint");
		accessKeyId = propertiesUtil.getString("xk.recipe.oss.access_key_id");
		accessKeySecret = propertiesUtil.getString("xk.recipe.oss.access_key_secret");
		bucketName = propertiesUtil.getString("xk.recipe.oss.bucket");
		downloadUrl = "http://"+bucketName+"."+endpoint;

	}
	
	/**
	 * 上传单个文件流
	 * 
	 * @param data
	 * @param fileSuffix
	 * @return
	 */
	public String uploadFile(InputStream data, String fileSuffix) {
		OSSClient ossClient = new OSSClient(bucketName + "." + endpoint, accessKeyId, accessKeySecret);
		try {
			String filePath = "usm_nurse" + "/" + UUID.randomUUID().toString().replaceAll("-", "") + fileSuffix;
			long startTime = System.currentTimeMillis();
			ossClient.putObject(bucketName, filePath, data);
			long endTime = System.currentTimeMillis();
			System.out.println(endTime-startTime);

			return downloadUrl + "/" + filePath;
		} catch (OSSException oe) {
			logger.error("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.");
			logger.error("Error Message: " + oe.getErrorCode());
			logger.error("Error Code: " + oe.getErrorCode());
			logger.error("Request ID: " + oe.getRequestId());
			logger.error("Host ID: " + oe.getHostId());
		} catch (ClientException ce) {
			logger.error("Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.");
			logger.error("Error Message: " + ce.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
		}
		return null;
	}
	
	/**
	 * 上传单个文件流，文件路径自定义
	 * @param data
	 * @param filePath 格式如：definedPath/yyyyMM/fileName
	 * @return
	 */
	public String uploadFileWithPath(InputStream data, String filePath) {
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		try {
			String fpath = "Java/" + filePath;
			ossClient.putObject(bucketName, fpath, data);
			
			return downloadUrl + "/" + fpath;
		} catch (OSSException oe) {
			logger.error("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.");
			logger.error("Error Message: " + oe.getErrorCode());
			logger.error("Error Code: " + oe.getErrorCode());
			logger.error("Request ID: " + oe.getRequestId());
			logger.error("Host ID: " + oe.getHostId());
		} catch (ClientException ce) {
			logger.error("Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.");
			logger.error("Error Message: " + ce.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
		}
		return null;
	}

	/**
	 * 下载文件
	 * 
	 * @param file
	 * @param targetFile
	 * @return
	 */
	public String downloadFile(String file, String targetFile) {
		OSSClient ossClient = new OSSClient(bucketName + "." + endpoint, accessKeyId, accessKeySecret);
		try {
			String key = file.replaceAll(downloadUrl + "/", "");
			DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
			// 设置本地文件
			downloadFileRequest.setDownloadFile(targetFile);
			// 设置并发下载数，默认1
			downloadFileRequest.setTaskNum(5);
			// 设置分片大小，默认100KB
			downloadFileRequest.setPartSize(1024 * 1024 * 1);
			// 开启断点续传，默认关闭
			downloadFileRequest.setEnableCheckpoint(true);
			DownloadFileResult downloadResult = ossClient.downloadFile(downloadFileRequest);
			// 下载成功时，会返回文件的元信息
			ObjectMetadata objectMetadata = downloadResult.getObjectMetadata();
			System.out.println(objectMetadata.getETag());
			System.out.println(objectMetadata.getLastModified());
			System.out.println(objectMetadata.getUserMetadata().get("meta"));
		} catch (OSSException oe) {
			logger.error("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.");
			logger.error("Error Message: " + oe.getErrorCode());
			logger.error("Error Code: " + oe.getErrorCode());
			logger.error("Request ID: " + oe.getRequestId());
			logger.error("Host ID: " + oe.getHostId());
		} catch (ClientException ce) {
			logger.error("Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.");
			logger.error("Error Message: " + ce.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
		}
		return null;
	}

	/**
	 * 删除文件
	 * 
	 * @param files
	 * @return
	 */
	public String deleteFile(List<String> files) {
		OSSClient ossClient = new OSSClient(bucketName + "." + endpoint, accessKeyId, accessKeySecret);
		String rst = null;
		try {
			List<String> keys = new ArrayList<>();
			for (String file : files) {
				keys.add(file.replaceAll(downloadUrl + "/", ""));
			}
			DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys));
			List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
			for (String object : deletedObjects) {
				System.out.println("\t" + object);
			}
			rst = deletedObjects.toString();
		} catch (OSSException oe) {
			logger.error("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.");
			logger.error("Error Message: " + oe.getErrorCode());
			logger.error("Error Code: " + oe.getErrorCode());
			logger.error("Request ID: " + oe.getRequestId());
			logger.error("Host ID: " + oe.getHostId());
		} catch (ClientException ce) {
			logger.error("Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.");
			logger.error("Error Message: " + ce.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
		}
		return rst;
	}


	/**
	 * 多图上传
	 */
	public String[] uploadImageToOSS(List<String> fileNames, List<InputStream> inputStreams) {
		/**
		 * 创建OSS客户端
		 */
		OSSClient ossClient = new OSSClient(bucketName + "." + endpoint, accessKeyId, accessKeySecret);
		try {
			ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue(
					fileNames);
			ConcurrentLinkedQueue<InputStream> streamConcurrentLinkedQueue = new ConcurrentLinkedQueue<>(
					inputStreams);
			Iterator<InputStream> inputStreamss = streamConcurrentLinkedQueue.iterator();
			ConcurrentLinkedQueue c = new ConcurrentLinkedQueue();
			for (Iterator<String> iterator = concurrentLinkedQueue.iterator(); iterator.hasNext() && inputStreamss.hasNext(); ) {
				String filePath = "usm_nurse" + "/" + UUID.randomUUID().toString().replaceAll("-", "") + iterator.next();
				ossClient.putObject(bucketName, filePath, inputStreamss.next());
				c.add(downloadUrl + "/" + filePath);
			}
			return (String[]) c.toArray(new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		} finally {
			ossClient.shutdown();
		}
		return null;
	}

}
