package com.tlg.storehelper.service.impl;

import com.google.gson.Gson;
import com.nec.lib.utils.Base64Util;
import com.nec.lib.utils.FileUtil;
import com.tlg.storehelper.pojo.BaseResponseVo;
import com.tlg.storehelper.pojo.SimpleMapResponseVo;
import com.tlg.storehelper.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;

@Service
public class CommonServiceImpl implements CommonService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *  APP版本信息
     * @param response
     * @param downloadFilePath  被下载的文件在服务器中的路径
     * @return 200,801
     */
    @Override
    public SimpleMapResponseVo getVersion(HttpServletResponse response, String downloadFilePath) {
        if(!downloadFilePath.endsWith("/") && !downloadFilePath.endsWith("\\"))
            downloadFilePath = downloadFilePath + "/";

        SimpleMapResponseVo responseVo = new SimpleMapResponseVo();
        File file = new File(downloadFilePath + "version.json");
        if (file.exists()) {
            try {
                String content= FileUtils.readFileToString(file,"UTF-8");
                Gson gson = new Gson();
                responseVo = gson.fromJson(content, SimpleMapResponseVo.class);
                Double versionCode = (Double) responseVo.map.get("versionCode");
                responseVo.map.put("versionCode", versionCode.intValue());
            } catch (IOException e) {
                responseVo.code = 801;
                responseVo.msg = "无法取得APP版本";
                logger.error(e.getMessage(), e);
            }
        } else {
            responseVo.code = 801;
            responseVo.msg = "无法取得APP版本";
        }
        return responseVo;
    }

    /**
     * 下载文件
     * @param response
     * @param downloadFilePath  被下载的文件在服务器中的路径
     * @param fileName  被下载文件的名称
     * @return 200,500
     */
    @Override
    public BaseResponseVo downloadFile(HttpServletResponse response, String downloadFilePath, String fileName) {
        if(!downloadFilePath.endsWith("/") && !downloadFilePath.endsWith("\\"))
            downloadFilePath = downloadFilePath + "/";

        BaseResponseVo responseVo = new BaseResponseVo();

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
                responseVo.code = 200;
                responseVo.msg = "下载成功";
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response.reset();
                response.setContentType("application/json;charset=UTF-8");
                responseVo.code = 500;
                responseVo.msg = "下载失败";
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        } else {
            responseVo.code = 404;
            responseVo.msg = "文件不存在";
        }
        return responseVo;
    }

    /**
     * 下载文件
     * @param response
     * @param base64File  被下载Base64编码的文件流
     * @return 200,500
     */
    @Override
    public BaseResponseVo downloadFileFromStream(HttpServletResponse response, String base64File, String fileName) {
        BaseResponseVo responseVo = new BaseResponseVo();
        byte[] bytes = Base64Util.base64ToByteArray(base64File);
        fileName = fileName + FileUtil.getFileExtension(Arrays.copyOf(bytes,10));

        response.setContentType("application/force-download");  // 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.addIntHeader("Content-Length", new Long(bytes.length).intValue());
        try {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes);
            responseVo.setSuccessfulMessage("success");
            return responseVo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.reset();
            response.setContentType("application/json;charset=UTF-8");
            responseVo.code = 500;
            responseVo.msg = "下载失败";
        }
        return responseVo;
    }
}
