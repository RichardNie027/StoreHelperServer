package com.tlg.storehelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * SpringBoot默认扫描Application入口类所在的包以及子包，也可以主动配置
 */
@SpringBootApplication(scanBasePackages = {"com.tlg.storehelper.controller","com.tlg.storehelper.service.impl","com.nec.lib.springboot","com.tlg.storehelper.interceptor","datasource"}, exclude = {DataSourceAutoConfiguration.class})    //扫描controller和service
//@EnableAutoConfiguration    //自动载入应用程序所需的所有Bean，在类路径中查找
@ServletComponentScan       //注册过滤器注解
@MapperScan(basePackages = {"com.tlg.storehelper.dao.main","com.tlg.storehelper.dao.second"})   //扫描dao
@EnableTransactionManagement
public class StorehelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorehelperApplication.class, args);
    }

}
