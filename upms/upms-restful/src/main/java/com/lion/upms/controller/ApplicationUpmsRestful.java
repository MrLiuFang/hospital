package com.lion.upms.controller;

import com.lion.core.persistence.BaseDaoFactoryBean;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication ()
@ComponentScan(basePackages = "com.lion.**")
@EnableDiscoveryClient
@DubboComponentScan(basePackages = {"com.lion.**"})
@EnableJpaRepositories(basePackages = {"com.lion.upms.dao.**"}, repositoryFactoryBeanClass = BaseDaoFactoryBean.class)
@EntityScan({"com.lion.upms.entity.**"})
@EnableJpaAuditing
@EnableOpenApi
public class ApplicationUpmsRestful {

    public static void main ( String args[] ) throws Exception {
        /*
         * new SpringApplicationBuilder(Application.class)
         * .web(WebApplicationType.NONE) .run(args);
         */
        SpringApplication.run(ApplicationUpmsRestful.class, args);
    }
}