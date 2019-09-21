package com.tlg.storehelper.service;

import com.tlg.storehelper.pojo.BaseResponseEntity;
import com.tlg.storehelper.pojo.SimpleMapEntity;

import javax.servlet.http.HttpServletResponse;

public interface CommonService {

    SimpleMapEntity getVersion(HttpServletResponse response, String downloadFilePath);

    BaseResponseEntity downloadFile(HttpServletResponse response, String downloadFilePath, String fileName);

    BaseResponseEntity downloadFileFromStream(HttpServletResponse response, String base64File, String fileName);

}
