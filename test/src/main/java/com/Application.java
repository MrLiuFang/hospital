package com;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/20 上午9:49
 */

@SpringBootApplication(scanBasePackages = "com")
@EnableDubbo(scanBasePackages = "com")
public class Application {

//    @DubboReference
//    private Test test;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
