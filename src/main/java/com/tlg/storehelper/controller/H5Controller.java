package com.tlg.storehelper.controller;

import com.nec.lib.utils.RedisUtil;
import com.tlg.storehelper.pojo.BaseResponseVo;
import com.tlg.storehelper.pojo.SimpleListResponseVo;
import com.tlg.storehelper.pojo.SimpleMapResponseVo;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RestController
public class H5Controller {

    @Autowired
    private BusinessService businessService;
    @Autowired
    private CommonService commonService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = "/h5/brand/picsCount", method = RequestMethod.GET)
    private SimpleMapResponseVo brandPicsCount(String brandKey){
        SimpleMapResponseVo vo = new SimpleMapResponseVo();
        if(brandKey == null)
            vo.setCodeAndMessage(500, "参数缺失");
        else {
            Map<String, Integer> map = businessService.getBrandPicsCountByVer(brandKey);
            vo.map.putAll(map);
            vo.setSuccessfulMessage("success");
        }
        return vo;
    }

    @RequestMapping(value = "/h5/brand/pic/{brandKey}/{type}/{idx}/{picVer}", method= RequestMethod.GET)
    private void downloadPic(HttpServletResponse response, @PathVariable String brandKey, @PathVariable String type, @PathVariable long idx, @PathVariable String picVer) {
        if(businessService.queryBrandPicture(brandKey, type, idx)) {
            RedisUtil redisUtil = RedisUtil.getInstance(redisTemplate);
            String key = brandKey + "_" + type + "_" + idx;
            System.out.println(key);
            String picStream = (String)redisUtil.get(key);
            if(picStream.isEmpty()) {
                response.setStatus(1007);
                response.setHeader("msg", "No Pic");
            }
            BaseResponseVo ret = commonService.downloadFileFromStream(response, picStream, key);
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

//    @RequestMapping(value = "/h5/brandPage", method = RequestMethod.GET)
//    private void brandHome(HttpServletResponse response, String brand, String type) throws IOException {
//        String html = businessService.getBrandHtml(brand, type);
//        exportHtml(response, html);
//    }
//
//    private void exportHtml(HttpServletResponse response, String html) throws IOException {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html; charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        out.print(html);
//        out.flush();
//        out.close();
//    }
}
