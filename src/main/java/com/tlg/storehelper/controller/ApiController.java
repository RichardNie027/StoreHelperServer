package com.tlg.storehelper.controller;

import com.tlg.storehelper.pojo.GoodsBarcodeEntity;
import com.tlg.storehelper.pojo.SimpleEntity;
import com.tlg.storehelper.pojo.SimpleListEntity;
import com.tlg.storehelper.pojo.SimpleMapEntity;
import com.tlg.storehelper.service.ApiService;
import com.tlg.storehelper.service.BusinessService;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;
    @Autowired
    private BusinessService businessService;

    @RequestMapping("/api/getToken")
    public SimpleMapEntity getToken(){
        SimpleMapEntity simpleMapEntity = new SimpleMapEntity();
        simpleMapEntity.result.put("token", "TianLiGe");
        simpleMapEntity.setSuccessfulMessage("success");
        return simpleMapEntity;
    }

    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public SimpleListEntity<String> loginValidation(@RequestBody LoginBean loginBean){
        return apiService.loginValidation(loginBean.username, loginBean.password);
    }

    public static class LoginBean {
        public String username;
        public String password;
        public LoginBean() {}
    }

    @RequestMapping("/api/getGoodsBarcodeList")
    public SimpleEntity<String> getGoodsBarcodeList(String lastModDate){
        return apiService.getGoodsBarcodeList(lastModDate);
    }

    /*
    @RequestMapping("/api/getGoodsList")
    public GoodsBarcodeEntity getGoodsBarcodeList(HttpServletRequest request, @RequestParam(value = "lastModDate", required = false) String lastModDate){
        String baseDateString = "20000101000000";
        lastModDate = lastModDate == null || lastModDate.isEmpty() ? baseDateString : lastModDate;
        try {
            new SimpleDateFormat ("yyyyMMddHHmmss").parse(lastModDate);
        } catch (ParseException e) {
            lastModDate = baseDateString;
        }
        return apiService.getGoodsBarcodeList(lastModDate);
    }*/

}
