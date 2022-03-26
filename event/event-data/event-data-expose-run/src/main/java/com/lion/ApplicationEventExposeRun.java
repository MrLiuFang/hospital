package com.lion;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.lion.config.JPAQueryFactoryConfiguration;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        DruidDataSourceAutoConfigure.class ,
        HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = {"com.lion.**"},excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = JPAQueryFactoryConfiguration.class)})
@DubboComponentScan(basePackages = {"com.lion.**"})
@EnableDiscoveryClient
public class ApplicationEventExposeRun {

    public static void main ( String args[] ) throws Exception {
        /*
         * new SpringApplicationBuilder(Application.class)
         * .web(WebApplicationType.NONE) .run(args);
         */
        SpringApplication.run(ApplicationEventExposeRun.class, args);
    }
}