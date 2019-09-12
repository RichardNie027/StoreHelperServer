package com.tlg.storehelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * SpringBoot默认扫描Application入口类所在的包以及子包，也可以主动配置
 */
@SpringBootApplication(scanBasePackages = {"com.tlg.storehelper.controller","com.tlg.storehelper.service.impl","com.nec.lib.springboot","com.tlg.storehelper.interceptor","com.tlg.storehelper.conf"}, exclude = {DataSourceAutoConfiguration.class})    //扫描controller和service
//@EnableAutoConfiguration    //自动载入应用程序所需的所有Bean，在类路径中查找
@ServletComponentScan       //注册过滤器注解
//@MapperScan(basePackages = {"com.tlg.storehelper.dao.ds1","com.tlg.storehelper.dao.ds2","com.tlg.storehelper.dao.ds3"})   //扫描dao//DataSourceConfig中重复配置
@EnableTransactionManagement
public class StorehelperApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(StorehelperApplication.class, args);
    }

    // 继承SpringBootServletInitializer 实现configure 方便打war 外部服务器部署
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StorehelperApplication.class);
    }
}
