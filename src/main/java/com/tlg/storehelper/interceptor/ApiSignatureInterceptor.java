package com.tlg.storehelper.interceptor;

import com.google.gson.Gson;
import com.nec.lib.utils.DateUtil;
import com.nec.lib.utils.XxteaUtil;
import com.tlg.storehelper.service.BusinessService;
import org.apache.tomcat.jni.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Component("apiSignatureInterceptor")
public class ApiSignatureInterceptor implements HandlerInterceptor {

    @Autowired
    private BusinessService businessService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        try {
            RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
            Map<String, String> sortedMap = new TreeMap<>();
            Enumeration<String> t = httpServletRequest.getHeaderNames();
            while(t.hasMoreElements()) {
                System.out.println(t.nextElement());
            }
            //header
            String appversion = httpServletRequest.getHeader("appversion");
            String apiversion = httpServletRequest.getHeader("apiversion");
            String timestamp = httpServletRequest.getHeader("timestamp");
            String uid = httpServletRequest.getHeader("uid");
            String session = httpServletRequest.getHeader("session");
            String signature = httpServletRequest.getHeader("signature");
            if(appversion!=null && "1.0".compareTo(appversion)<=0)
                sortedMap.put("appversion", appversion);
            else {
                responseJson(httpServletResponse, 904, "APP版本不支持");
                return false;
            }
            if(apiversion!=null && "1.0".compareTo(apiversion)<=0)
                sortedMap.put("apiversion", apiversion);
            else {
                responseJson(httpServletResponse, 903, "API版本不支持");
                return false;
            }
            if(timestamp!=null) {
                Date date1 = DateUtil.fromStr(timestamp, "yyyyMMddHHmmss");
                long time1 = date1.getTime()/1000;
                long time2 = new Date().getTime()/1000;
                if(Math.abs(time2-time1) < 10*60)
                    sortedMap.put("timestamp", timestamp);
                else {
                    responseJson(httpServletResponse, 901, "时间误差超出允许");
                    return false;
                }
            } else {
                responseJson(httpServletResponse, 901, "时间误差超出允许");
                return false;
            }
            switch (businessService.checkUserToken(uid, session)) {
                case -1:
                    responseJson(httpServletResponse, 999, "用户身份无效");
                    return false;
                case -2:
                    responseJson(httpServletResponse, 999, "令牌校验无效");
                    return false;
                default:
                    sortedMap.put("uid", uid);
                    sortedMap.put("session", session);
            }
            if(signature==null || signature.isEmpty()) {
                responseJson(httpServletResponse, 902, "签名校验无效");
                return false;
            }
            //(post)body
            String body = requestWrapper.getBody();
            if(!body.trim().isEmpty()) {
                Gson gson = new Gson();
                Map bodyMap = gson.fromJson(body, Map.class);
                sortedMap.putAll(bodyMap);
            }
            //(get)parameters
            Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
            if(null != parameterNames) {
                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    String paramValue = httpServletRequest.getParameter(paramName);
                    sortedMap.put(paramName, paramValue);
                }
            }

            //生成签名，校验签名
            StringBuffer signatureBuffer = new StringBuffer();
            for(String key: sortedMap.keySet()) {
                signatureBuffer.append(key).append(sortedMap.get(key));
            }
            String signatureStr = "";
            String token = session.isEmpty() ? "store_helper" : session;
            try {
                signatureStr = XxteaUtil.encryptBase64String(signatureBuffer.toString(), "UTF-8", token);
            } catch (Exception e) {}
            //
            if(!signatureStr.equals(signature)) {
                responseJson(httpServletResponse, 902, "签名校验无效");
                return false;
            }

            return true;
        }catch (Exception e){
            responseJson(httpServletResponse, 902, "签名校验过程出错");
            System.out.println("签名校验过程出错：" + e.getMessage());
        }
        return false;
    }

    private void responseJson(HttpServletResponse response, int code, String msg) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.print("{\"code\":" + code + ",\"msg\":\"" + msg + "\"}");
        out.flush();
        out.close();
    }
}
