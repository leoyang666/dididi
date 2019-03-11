package com.codingforhappy.config;

import com.codingforhappy.dao.sql.ExampleDao;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.logging.LogFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@MapperScan("com.codingforhappy.dao")
@PropertySource("classpath:db.properties")
public class MybatisConfig {

    @Autowired
    private Environment env;

    public static void main(String args[]) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-app.xml");
        ExampleDao dao = context.getBean(ExampleDao.class);
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("phonenum", "17816876192");
        map.put("password", "fad");
        map.put("nickname", "happy");
        dao.insertPassengers(map);
    }

    @Bean
    public DataSource getDataSource() {
        LogFactory.useLog4JLogging();
        PooledDataSource dataSource = new PooledDataSource();
//        dataSource.setPoolPingQuery("SELECT 1");
//        dataSource.setPoolPingEnabled(true);
        dataSource.setDriver(env.getProperty("jdbc.driver"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.password"));
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean getSqlSessionFactory() throws IOException {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
//        sqlSessionFactory.setConfigLocation(new ClassPathResource("mybatis_config.xml"));
        sqlSessionFactory.setDataSource(getDataSource());
        sqlSessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
        );
        return sqlSessionFactory;
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
