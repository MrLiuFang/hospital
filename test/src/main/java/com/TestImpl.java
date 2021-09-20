package com;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/20 上午9:44
 */
@DubboService( )
public class TestImpl implements Test {
    @Override
    public void test() {

    }

}
