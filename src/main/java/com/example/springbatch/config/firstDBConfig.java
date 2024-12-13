package com.example.springbatch.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.springbatch.repository", // 첫 번째 DB에 해당하는 패키지 경로
        entityManagerFactoryRef = "firstEntityManager",
        transactionManagerRef = "firstTransactionManager"
)
public class firstDBConfig {

    @Primary
    @Bean
    public PlatformTransactionManager firstTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(firstEntityManager().getObject());
        return transactionManager;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean firstEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(firstDataSource());
        em.setPackagesToScan(new String[]{"com.example.springbatch.entity"}); // 첫 번째 DB에 맞는 엔티티 패키지 경로
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect"); // 오라클용 Hibernate Dialect 설정
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public DataSource firstDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("oracle.jdbc.OracleDriver")
                .url("jdbc:oracle:thin:@127.0.0.1:15111:SECHUL") // 오라클 DB 연결 URL
                .username("DEX_LOCAL")
                .password("DEX_LOCAL11")
                .build();
    }
}
