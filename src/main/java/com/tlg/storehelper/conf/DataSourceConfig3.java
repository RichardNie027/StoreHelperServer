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
@MapperScan(value = "com.tlg.storehelper.dao.ds3", sqlSessionTemplateRef  = "sqlSessionTemplate3")
public class DataSourceConfig3 {
    @Bean(name = "dataSource3")
    @ConfigurationProperties(prefix = "spring.datasource.ds3")
    @LiquibaseDataSource
    public DataSource testDataSource() {
        HikariDataSource ds = (HikariDataSource) DataSourceBuilder.create().build();
        ds.setConnectionTestQuery("SELECT 1");
        return ds;
    }

    @Bean(name = "sqlSessionFactory3")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("dataSource3") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean(name = "transactionManager3")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("dataSource3") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionTemplate3")
    //   @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("sqlSessionFactory3") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}