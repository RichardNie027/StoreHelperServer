package datasource;

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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "com.tlg.storehelper.dao.main", sqlSessionTemplateRef  = "mainSqlSessionTemplate")  //关联的mapper.xml所在位置
public class DataSourceConfig1 {
    @Bean(name = "mainDataSource") //作为一个bean对象并命名
    @ConfigurationProperties(prefix = "spring.datasource.main") //配置文件中，该数据源的前缀
    @Primary   //用于标记主数据源，除了主数据源外，其余注入文件都不添加该注解
    @LiquibaseDataSource
    public DataSource testDataSource() {
        HikariDataSource ds = (HikariDataSource) DataSourceBuilder.create().build();
        ds.setConnectionTestQuery("SELECT 1");
        return ds;
    }

    @Bean(name = "mainSqlSessionFactory")
    @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("mainDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/tlg/storehelper/dao/second/XMapper.xml"));//对应mapper.xml的具体位置
        return bean.getObject();
    }

    @Bean(name = "mainTransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("mainDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "mainSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("mainSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}