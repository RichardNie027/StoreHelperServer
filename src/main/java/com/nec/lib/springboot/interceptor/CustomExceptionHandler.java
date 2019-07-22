package com.nec.lib.springboot.interceptor;

import com.nec.lib.springboot.exception.CustomException;
import com.nec.lib.springboot.exception.CustomViewException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {
    /**
     * 不管是访问返回视图接口，还是返回json串接口，只要抛出的excetion异常全部由这个方法拦截，并统一返回json串
     * 统一异常拦截 rest接口
     */
    @ExceptionHandler(Exception.class)  //Exception 这个是所有异常的父类
    @ResponseBody   //定义异常统一返回json，即使是返回视图的接口
    Map<String,String> handleControllerException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        Map<String,String> map = new HashMap();
        map.put("code", String.valueOf(status.value()));
        map.put("msg", ex.getMessage());
        //这时会返回我们统一的异常json
        return map;
    }
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * 拦截自定义异常 CustomException rest接口
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    Map<String,String> handleControllerCustomException(HttpServletRequest request, CustomException ex) {
        HttpStatus status = getStatus(request);
        Map<String,String> map = new HashMap();
        map.put("code", String.valueOf(status.value()));
        map.put("msg", "customer error msg");
        return map;
    }

    /**
     * 拦截抛出CustomViewException 异常的方法并返回视图
     */
    @ExceptionHandler(CustomViewException.class)
    ModelAndView handleControllerCustomExceptionView(HttpServletRequest request, CustomViewException ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        HttpStatus status = getStatus(request);
        Map<String,String> map = new HashMap();
        map.put("code", String.valueOf(status.value()));
        map.put("msg", "customer error msg");
        return modelAndView;
    }
}
