package com.xk.usm.common.util;

import com.aliyun.oss.model.Callback;
import com.xk.common.util.StringUtil;

/**
 * @program: xk-framewrok
 * @description: OssFileUpload
 * @author: Wang Zhenhua
 * @create: 2018-11-29
 * @version: v1.0.0 创建文件, Wang Zhenhua, 2018-11-29
 **/
public class OssFileUpload {

    private String filepath;

    private String filename;

    private String checkpointfile;

    private Callback callback;

    /**
     * 获取文件的完整路径
     * @return
     */
    public String getFullFileName(){
        String fullname = new String(filepath);
        if (StringUtil.isNotBlank(fullname) && '/' != fullname.charAt(fullname.length() - 1) && '/' != filename.charAt(0)){
            fullname = fullname + "/";
        }
        fullname += filename;
        return fullname;
    }

    /**
     * 获取分片上传结果文件的完整路径<br>
     *     默认文件名为filename+uploadFile.ucp
     * @return
     */
    public String getFullCheckpointfile(){
        String fullname = new String(filepath);
        if (StringUtil.isNotBlank(fullname) && '/' != fullname.charAt(fullname.length() - 1) && '/' != filename.charAt(0)){
            fullname = fullname + "/";
        }
        String ucpfile = StringUtil.isNotBlank(checkpointfile) ? checkpointfile : filename + "uploadFile.ucp";
        fullname += ucpfile;
        return fullname;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCheckpointfile() {
        return checkpointfile;
    }

    public void setCheckpointfile(String checkpointfile) {
        this.checkpointfile = checkpointfile;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
