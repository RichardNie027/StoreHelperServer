package com.tlg.storehelper.controller;

import com.nec.lib.utils.ArrayUtil;
import com.nec.lib.utils.Base64Util;
import com.nec.lib.utils.RedisUtil;
import com.tlg.storehelper.entity.ds1.ErpStore;
import com.tlg.storehelper.pojo.BaseResponseVo;
import com.tlg.storehelper.pojo.SimpleListResponseVo;
import com.tlg.storehelper.pojo.SimpleMapResponseVo;
import com.tlg.storehelper.service.BusinessService;
import com.tlg.storehelper.service.CommonService;
import com.tlg.storehelper.service.ErpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.ArrayUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Controller
public class H5Controller {

    @Autowired
    private BusinessService businessService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ErpService erpService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = "/h5/brand/picNames", method = RequestMethod.GET)
    private @ResponseBody SimpleMapResponseVo brandPicNames(String brandKey){
        SimpleMapResponseVo vo = new SimpleMapResponseVo();
        if(brandKey == null)
            vo.setCodeAndMessage(500, "参数缺失");
        else {
            Map<String, String> map = businessService.getBrandPicNames(brandKey);
            vo.map.putAll(map);
            List<ErpStore> allStoreList = erpService.getAllStore();
            long storeCountInBrand = allStoreList.stream().filter(x->x.storeCode.substring(0,1).equals(brandKey)).count();
            vo.map.put("storeCountInBrand", storeCountInBrand);
            vo.setSuccessfulMessage("success");
        }
        return vo;
    }

    /**
     * 图片下载
     * @param response
     * @param brandKey 品牌首字
     * @param type  Base64的“类别（目录）”
     * @param name_time Base64的“文件名+时间”
     */
    @RequestMapping(value = "/h5/brand/pic/{brandKey}/{type}/{name_time}", method= RequestMethod.GET)
    private @ResponseBody void downloadPic(HttpServletResponse response, @PathVariable String brandKey, @PathVariable String type, @PathVariable String name_time) {
        if(businessService.queryBrandPicture(brandKey, type, name_time)) {
            RedisUtil redisUtil = RedisUtil.getInstance(redisTemplate);
            String key = brandKey + "_" + type + "_" + name_time;
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
//    private @ResponseBody void brandHome(HttpServletResponse response, String brand, String type) throws IOException {
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

    /**关于我们*/
    @RequestMapping("/h5/homePage")
    private String homePage(ModelMap map, @RequestParam(value = "storeCode", defaultValue = "") String storeCode) {
        String brandKey = storeCode.isEmpty() ? "" : storeCode.substring(0,1);
        String brand =  brandKey.equals("2") ? "朗姿" : brandKey.equals("7") ? "敦奴" : brandKey.equals("8") ? "菲姿" : brandKey.equals("M") ? "马天奴" : brandKey.equals("F") ? "拉娜菲" : brandKey.equals("A") ? "奥菲欧" : "本";
        String areaKey = storeCode.isEmpty() ? "" : storeCode.substring(1);
        String[] hubei = {"01","02","04","06","07","08","10","12","17","20","22","23","24","33","34","36","38","41","49","58","59","62","69","71","72","78"};
        String[] hunan = {"30","35","39","42","48","51","54","56","57","60","63","64","65","75","76","79","80"};
        String area = ArrayUtils.contains(hubei, areaKey) ? "湖北省" : ArrayUtils.contains(hunan, areaKey) ? "湖南省" : "本省";
        map.addAttribute("brand",brand);
        map.addAttribute("area",area);
        return "home";
    }

}
