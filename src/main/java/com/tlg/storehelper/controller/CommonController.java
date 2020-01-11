package com.tlg.storehelper.controller;

import com.nec.lib.utils.Base64Util;
import com.nec.lib.utils.DateUtil;
import com.nec.lib.utils.RedisUtil;
import com.tlg.storehelper.pojo.BaseResponseVo;
import com.tlg.storehelper.pojo.SimpleMapResponseVo;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.CommonService;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 为店铺代理程序提供：动态密码（3位）、后台基准日期（8位）
     * 可选提供：微销转货单的目标店铺集（4位一组，逗号分隔）+ 当日销售和库存的回传URL（http开头）
     */
    @RequestMapping(value = "/pre_api/dynamicPwd")
    private BaseResponseVo storeDynamicPwd(HttpServletResponse response, String storeCode, String ip) {
        String targetStoreCode = "A999";    //浪沙回传数据：微销转货单的目标店铺，多店铺以“逗号”分隔
        String pwd = businessService.getStoreDynamicPwd(storeCode, ip);
        BaseResponseVo baseResponseVo = new BaseResponseVo();
        if (pwd != null) {
            String today = DateUtil.toStr(new Date(), "yyyyMMdd");
            //today = "20190630";   //debug
            String url4Upload = "http://192.168.1.5:8080/storehelper/pre_api/uploadPosData";
            //baseResponseVo.setSuccessfulMessage(Base64Util.byteArrayToBase64((pwd + today).getBytes()));  //不回传浪沙数据
            baseResponseVo.setSuccessfulMessage(Base64Util.byteArrayToBase64((pwd + today + targetStoreCode + url4Upload).getBytes()));
        } else {
            baseResponseVo.code = 404;
            baseResponseVo.msg = "";
        }
        return baseResponseVo;
    }

    @RequestMapping(value = "/pre_api/appVersion")
    private SimpleMapResponseVo storeHelperAppLastestVersion(HttpServletResponse response) {
        return commonService.getVersion(response, sApkPath);
    }

    @RequestMapping(value = "/pre_api/downloadApk", method=RequestMethod.GET)
    private void downloadApk(HttpServletResponse response) {
        BaseResponseVo ret = commonService.downloadFile(response, sApkPath, sApkFilename);
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
            if(picStream==null || picStream.isEmpty()) {
                response.setStatus(1007);
                response.setHeader("msg", "No Pic");
            }
            BaseResponseVo ret = commonService.downloadFileFromStream(response, picStream, goodsNo);
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
