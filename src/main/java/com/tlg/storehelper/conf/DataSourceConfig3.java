package com.tlg.storehelper.conf;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "com.tlg.storehelper.dao.third", sqlSessionTemplateRef  = "thirdSqlSessionTemplate")
public class DataSourceConfig3 {
    @Bean(name = "thirdDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.third")
    @LiquibaseDataSource
    public DataSource testDataSource() {
        HikariDataSource ds = (HikariDataSource) DataSourceBuilder.create().build();
        ds.setConnectionTestQuery("SELECT 1");
        return ds;
    }

    @Bean(name = "thirdSqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("thirdDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name = "thirdTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("thirdDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "thirdSqlSessionTemplate")
    //   @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("thirdSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}