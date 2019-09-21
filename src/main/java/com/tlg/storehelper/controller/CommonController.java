package com.tlg.storehelper.controller;

import com.nec.lib.utils.Base64Util;
import com.nec.lib.utils.DateUtil;
import com.nec.lib.utils.NetUtil;
import com.nec.lib.utils.RedisUtil;
import com.tlg.storehelper.pojo.BaseResponseEntity;
import com.tlg.storehelper.pojo.SimpleMapEntity;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.CommonService;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class CommonController {

    public static String sApkPath = "D:/AndroidStudioProjects/APKs/StoreHelper/release/";
    public static String sApkFilename = "StoreHelper.apk";

    @Autowired
    private CommonService commonService;
    @Autowired
    private ErpService erpService;
    @Autowired
    private BusinessService businessService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = "/pre_api/dynamicPwd")
    private BaseResponseEntity storeDynamicPwd(HttpServletResponse response, String storeCode) {
        String pwd = businessService.getStoreDynamicPwd(storeCode);
        BaseResponseEntity baseResponseEntity = new BaseResponseEntity();
        if (pwd != null) {
            String today = DateUtil.toStr(new Date(), "yyyyMMdd");
            //today = "20170831";
            String url4Upload = "http://192.168.1.19:8080/runsaPosApi/uploadPosData";
            baseResponseEntity.setSuccessfulMessage(Base64Util.byteArrayToBase64((pwd + today + url4Upload).getBytes()));
        } else {
            baseResponseEntity.code = 404;
            baseResponseEntity.msg = "";
        }
        return baseResponseEntity;
    }

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
        if(erpService.queryGoodsPicture(goodsNo)) {
            RedisUtil redisUtil = RedisUtil.getInstance(redisTemplate);
            String picStream = (String)redisUtil.get(goodsNo);
            if(picStream.isEmpty()) {
                response.setStatus(1007);
                response.setHeader("msg", "No Pic");
            }
            BaseResponseEntity ret = commonService.downloadFileFromStream(response, picStream, goodsNo);
            if(ret == null || ret.code != 200) {
                response.setStatus(404);
                response.setHeader("msg", ret==null ? "" : ret.msg);
            }
        } else {
            response.setStatus(1007);
            response.setHeader("msg", "No Pic");
            return;
        }
    }
}
