package com.tlg.storehelper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * SpringBoot默认扫描Application入口类所在的包以及子包，也可以主动配置
 */
@SpringBootApplication(scanBasePackages = {"com.tlg.storehelper.controller","com.tlg.storehelper.service.impl","com.nec.lib.springboot","datasource"}, exclude = {DataSourceAutoConfiguration.class})    //扫描controller和service
//@EnableAutoConfiguration    //自动载入应用程序所需的所有Bean，在类路径中查找
@MapperScan(basePackages = "com.tlg.storehelper.dao")   //扫描dao
public class StorehelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorehelperApplication.class, args);
    }

}
