package com.tlg.storehelper.service;

import com.tlg.storehelper.pojo.BaseResponseVo;
import com.tlg.storehelper.pojo.SimpleMapResponseVo;

import javax.servlet.http.HttpServletResponse;

public interface CommonService {

    SimpleMapResponseVo getVersion(HttpServletResponse response, String downloadFilePath);

    BaseResponseVo downloadFile(HttpServletResponse response, String downloadFilePath, String fileName);

    BaseResponseVo downloadFileFromStream(HttpServletResponse response, String base64File, String fileName);

}
