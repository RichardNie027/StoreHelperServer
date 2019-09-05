package com.tlg.storehelper.controller;

import com.tlg.storehelper.pojo.BaseResponseEntity;
import com.tlg.storehelper.pojo.SimpleMapEntity;
import com.tlg.storehelper.service.CommonService;
import com.tlg.storehelper.service.ErpService;
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

    @Autowired
    private CommonService commonService;
    @Autowired
    private ErpService erpService;

    @RequestMapping(value = "/pre_api/appVersion")
    private SimpleMapEntity storeHelperAppLastestVersion(HttpServletResponse response) {
        return commonService.getVersion(response, sApkPath);
    }

    @RequestMapping(value = "/pre_api/downloadApk", method=RequestMethod.GET)
    private void downloadApk(HttpServletResponse response) {
        BaseResponseEntity ret = commonService.downloadFile(response, sApkPath, sApkFilename);
        if(ret.code != 200) {
            response.setStatus(404);
            response.setHeader("msg", "Apk download error.");
            return;
        }
    }

    @RequestMapping(value = "/pre_api/pic/{goodsNo}", method= RequestMethod.GET)
    private void downloadPic(HttpServletResponse response, @PathVariable String goodsNo) {
        String picPathName = erpService.getGoodsPictureName(goodsNo);
        if(picPathName == null) {
            response.setStatus(404);
            response.setHeader("msg", "Invalid goodsNo");
            return;
        }
        int lastIdx = picPathName.lastIndexOf("\\");
        if(lastIdx == -1) {
            response.setStatus(404);
            response.setHeader("msg", "No Pic");
            return;
        }
        BaseResponseEntity ret = commonService.downloadFile(response, picPathName.substring(0,lastIdx+1), picPathName.substring(lastIdx+1));
        if(ret.code != 200) {
            response.setStatus(404);
            response.setHeader("msg", ret.msg);
        }
    }
}
