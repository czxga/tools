package com.xk.usm.common.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.xk.common.util.PropertiesUtil;
import com.xk.common.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * @program: xk-framewrok
 * @description: oss服务
 * @author: Wang Zhenhua
 * @create: 2018-07-26
 * @version: v1.0.0 创建文件, Wang Zhenhua
 * @version: v1.0.1 增加字符串方法，增加通用删除方法, Wang Zhenhua
 **/
public class OSSUtil {

    private Logger logger = LoggerFactory.getLogger(OSSUtil.class);

    //阿里云API的内或外网域名
    private String ENDPOINT;
    //阿里云API的密钥Access Key ID
    private String ACCESS_KEY_ID;
    //阿里云API的密钥Access Key Secret
    private String ACCESS_KEY_SECRET;
    //阿里云API的存储空间（Bucket）
    private String BUCKET;

    private OSSClient ossClient;

  public OSSUtil(){
        PropertiesUtil propertiesUtil = new PropertiesUtil("config.properties",
                PropertiesUtil.SourceType.ResourceType);
        ENDPOINT = propertiesUtil.getString("endpoint");
        ACCESS_KEY_ID = propertiesUtil.getString("accessKeyId");
        ACCESS_KEY_SECRET = propertiesUtil.getString("accessKeySecret");
        BUCKET = propertiesUtil.getString("bucket");
        this.ossClient = new OSSClient(BUCKET + "." + ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    /**
     * 新建Bucket如果不存在
     * @param bucketName bucket名称
     * @return true 新建Bucket成功
     * */
    public boolean createBucketIfNotExist(OSSClient client, String bucketName){
        if (client.doesBucketExist(bucketName)) {
            return true;
        }
        Bucket bucket = client.createBucket(bucketName);
        return bucketName.equals(bucket.getName());
    }

    /**
     * 上传对象
     * 此对象必须实现Serializable接口
     * @param key
     * @param object
     * @return
     */
    public String putObject(String key,Object object) {
        byte[] objectbytes = SerializeUtil.serialize(object);
        return putFile(key,objectbytes);
    }

    /**
     * 上传String字符串
     * @param key
     * @param string
     * @return
     */
    public String putString(String key,String string) {
        byte[] objectbytes = string.getBytes();
        return putFile(key,objectbytes);
    }

    /**
     * 列举文件
     * 列举存储空间下的文件。最多列举100个文件
     * @param prefix
     * @return
     */
    public ObjectListing listObjects(String prefix){
        ObjectListing objectListing = ossClient.listObjects(BUCKET,prefix);
        return  objectListing;
    }

    public boolean doesObjectExist(String key){
        return ossClient.doesObjectExist(BUCKET,key);
    }

    /**
     * 获取对象
     * 此对象必须实现Serializable接口
     * @param key
     * @return
     */
    public Object getObject(String key){
        return SerializeUtil.unserialize(getFile(key));
    }

    /**
     * 获取字符串
     * @param key
     * @return
     */
    public String getString(String key){
        byte[] buffer = getFile(key);
        String content = null;
        try {
            content = new String(buffer,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 删除对象
     * @param key
     */
    @Deprecated
    public void deleteObject(String key){
        deleteFile(key);
    }

    /**
     * 上传文件
     * @param key
     * @param filecontent
     * @return
     */
    public String putFile(String key,byte[] filecontent){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(filecontent);
        PutObjectResult putResult = ossClient.putObject(BUCKET,key,inputStream);
        return putResult.getETag();
    }

    /**
     * 上传文件
     * @param key
     * @param is
     * @return
     */
    public String putFile(String key, InputStream is){
        PutObjectResult putResult = ossClient.putObject(BUCKET,key,is);
        return putResult.getETag();
    }

    /**
     * 上传文件
     * @param key
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public String putFile(String key, File file) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(file);
        return putFile(key, inputStream);
    }

    /**
     * 上传文件
     * @param key
     * @param file
     * @return 文件的URL
     */
    public String uploadObject2OSS(String key, File file) {
        if (null == file) {
            logger.error("上传阿里云OSS服务器文件为空");
            return null;
        }
        String resultStr = null;
        //文件名
        String fileName = file.getName();
        try {
            //以输入流的形式上传文件
            InputStream is = new FileInputStream(file);
            //文件大小
            Long fileSize = file.length();
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            //上传的文件的长度
            metadata.setContentLength(is.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 设置缓存过期时间,设置1小时后过期
            Date expire = new Date(new Date().getTime() + 3600 * 1000);
            metadata.setExpirationTime(expire);
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            //如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            //指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件   (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(BUCKET, key, is, metadata);
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e){
            e.printStackTrace();
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }

    private String getContentType(String fileName){
        //文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if(".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if(".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if(".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)  || ".png".equalsIgnoreCase(fileExtension) ) {
            return "image/jpeg";
        }
        if(".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if(".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if(".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if(".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if(".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if(".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        //默认返回类型
        return "application/octet-stream";
    }

    /**
     * 获取上传文件的地址
     * @param key
     * @return
     */
    public String getUrl(String key) {
        //如果你想把自己的资源发放给第三方用户访问，但是又不想开放Bucket的读权限，可以通过生成预签名URL的形式提供给用户一个临时的访问URL。在生成URL时，你可以指定URL过期的时间，从而限制用户长时间访问。
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(BUCKET, key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }

    /**
     * 获取文件
     * @param key
     * @return
     */
    public byte[] getFile(String key){
        OSSObject ossObject = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream inputStreamReader = null;
        try{
            ossObject = ossClient.getObject(BUCKET, key);
            byte[] buffer = new byte[1024];
            int len;
            inputStreamReader = ossObject.getObjectContent();
            while ((len = inputStreamReader.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos.toByteArray();
        } catch(Exception ex){
            ex.printStackTrace();
        } finally {
            try{
                baos.close();
                inputStreamReader.close();
            } catch (Exception ex){}
        }
        return null;
    }

    /**
     * 获取文件，并写入目标文件
     * @param key
     * @param filePath
     * @param fileName
     */
    public void getFile(String key, String filePath, String fileName){
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+"\\"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(getFile(key));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     * @param key
     */
    @Deprecated
    public void deleteFile(String key){
        ossClient.deleteObject(BUCKET,key);
    }

    /**
     * 从oss删除指定key的内容
     * @param key
     */
    public void delete(String key){
        ossClient.deleteObject(BUCKET,key);
    }

    /**
     * 关闭连接
     */
    public void shutdownClient(){
        ossClient.shutdown();
    }
}
