package com.tlg.storehelper.conf;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "com.tlg.storehelper.dao.second", sqlSessionTemplateRef  = "secondSqlSessionTemplate")
public class DataSourceConfig2 {
    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    //  @Primary
    public DataSource testDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondSqlSessionFactory")
    //  @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("secondDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/tlg/storehelper/dao/second/XMapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "secondTransactionManager")
    //   @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("secondDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "secondSqlSessionTemplate")
    //   @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("secondSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}