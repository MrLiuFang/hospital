package com.lion.event.expose;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication ( scanBasePackages = {"com.lion.**"} )
@EnableDiscoveryClient
@DubboComponentScan(basePackages = {"com.lion.**"})
@EntityScan({"com.lion.manage.entity.**"})
public class ApplicationEventExposeRun {

    public static void main ( String args[] ) throws Exception {
        /*
         * new SpringApplicationBuilder(Application.class)
         * .web(WebApplicationType.NONE) .run(args);
         */
        SpringApplication.run(ApplicationEventExposeRun.class, args);
    }
}