package com.tlg.storehelper.controller;

import com.tlg.storehelper.pojo.BaseResponseEntity;
import com.tlg.storehelper.pojo.SimpleMapEntity;
import com.tlg.storehelper.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class CommonController {

    public static String sApkPath = "D:/AndroidStudioProjects/APKs/StoreHelper/release/";
    public static String sApkFilename = "StoreHelper.apk";
    public static String sPicPath = "D:/Temp/pic/";

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/pre_api/appVersion")
    private SimpleMapEntity storeHelperAppLastestVersion(HttpServletResponse response) {
        return commonService.getVersion(response, sApkPath);
    }

    @RequestMapping(value = "/pre_api/downloadApk", produces="application/json;charset=utf-8", method=RequestMethod.GET)
    private String downloadApk(HttpServletResponse response) {
        BaseResponseEntity ret = commonService.downloadFile(response, sApkPath, sApkFilename);
        if(ret.code == 200)
            return null;
        else
            return ret.msg;
    }

    @RequestMapping(value = "/pre_api/pic/{pic}", method= RequestMethod.GET)
    private String downloadPic(HttpServletResponse response, @PathVariable String pic) {
        BaseResponseEntity ret = commonService.downloadFile(response, sPicPath, pic+".jpg");
        if(ret.code == 200)
            return null;
        else
            return ret.msg;
    }
}
