package com.lion.event;

import com.lion.config.JPAQueryFactoryConfiguration;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.rocketmq.client.log.ClientLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = "com.lion.**",excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = JPAQueryFactoryConfiguration.class)})
@EnableDiscoveryClient
@DubboComponentScan(basePackages = {"com.lion.**"})
@EnableOpenApi
public class ApplicationEventHandle {

    public static void main ( String args[] ) throws Exception {
        /*
         * new SpringApplicationBuilder(Application.class)
         * .web(WebApplicationType.NONE) .run(args);
         */
        System.setProperty(ClientLogger.CLIENT_LOG_USESLF4J,"true");
        SpringApplication.run(ApplicationEventHandle.class, args);
    }
}