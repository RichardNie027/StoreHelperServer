package com.tlg.storehelper.service.impl;

import com.google.gson.Gson;
import com.tlg.storehelper.pojo.BaseResponseEntity;
import com.tlg.storehelper.pojo.SimpleMapEntity;
import com.tlg.storehelper.service.CommonService;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Service
public class CommonServiceImpl implements CommonService {

    /**
     *  APP版本信息
     * @param response
     * @param downloadFilePath  被下载的文件在服务器中的路径
     * @return 200,801
     */
    @Override
    public SimpleMapEntity getVersion(HttpServletResponse response, String downloadFilePath) {
        if(!downloadFilePath.endsWith("/") && !downloadFilePath.endsWith("\\"))
            downloadFilePath = downloadFilePath + "/";

        SimpleMapEntity entity = new SimpleMapEntity();
        File file = new File(downloadFilePath + "version.json");
        if (file.exists()) {
            try {
                String content= FileUtils.readFileToString(file,"UTF-8");
                Gson gson = new Gson();
                entity = gson.fromJson(content, SimpleMapEntity.class);
                Double versionCode = (Double) entity.result.get("versionCode");
                entity.result.put("versionCode", versionCode.intValue());
            } catch (IOException e) {
                entity.code = 801;
                entity.msg = "无法取得APP版本";
                e.printStackTrace();
            }
        } else {
            entity.code = 801;
            entity.msg = "无法取得APP版本";
        }
        return entity;
    }

    /**
     * 下载APP APK文件
     * @param response
     * @param downloadFilePath  被下载的文件在服务器中的路径
     * @param fileName  被下载文件的名称
     * @return 200,500
     */
    @Override
    public BaseResponseEntity downloadFile(HttpServletResponse response, String downloadFilePath, String fileName) {
        if(!downloadFilePath.endsWith("/") && !downloadFilePath.endsWith("\\"))
            downloadFilePath = downloadFilePath + "/";

        BaseResponseEntity responseEntity = new BaseResponseEntity();

        File file = new File(downloadFilePath + fileName);
        if (file.exists()) {
            response.setContentType("application/force-download");  // 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            response.addIntHeader("Content-Length", new Long(file.length()).intValue());
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                responseEntity.code = 200;
                responseEntity.msg = "下载成功";
            } catch (Exception e) {
                e.printStackTrace();
                response.reset();
                response.setContentType("application/json;charset=UTF-8");
                responseEntity.code = 500;
                responseEntity.msg = "下载失败";
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            responseEntity.code = 404;
            responseEntity.msg = "文件不存在";
        }
        return responseEntity;
    }
}
