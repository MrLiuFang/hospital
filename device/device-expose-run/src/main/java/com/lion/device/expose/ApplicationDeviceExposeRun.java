package com.lion.device.expose;

import com.lion.core.persistence.BaseDaoFactoryBean;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication ( scanBasePackages = {"com.lion.**"} )
@EnableDiscoveryClient
@DubboComponentScan(basePackages = {"com.lion.**"})
@EnableJpaRepositories(basePackages = {"com.lion.device.dao.**"}, repositoryFactoryBeanClass = BaseDaoFactoryBean.class)
@EntityScan({"com.lion.device.entity.**"})
@EnableJpaAuditing
public class ApplicationDeviceExposeRun {

    public static void main ( String args[] ) throws Exception {
        /*
         * new SpringApplicationBuilder(Application.class)
         * .web(WebApplicationType.NONE) .run(args);
         */
        SpringApplication.run(ApplicationDeviceExposeRun.class, args);
    }
}