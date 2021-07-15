package com.lion.manage.controller.tcc;

import com.lion.annotation.AuthorizationIgnore;
import com.lion.exception.BusinessException;
import com.lion.person.expose.person.PersonTccExposeService;
import com.lion.upms.expose.tcc.UserTccExposeService;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/15 下午8:35
 */
@RestController
@RequestMapping("/tcc")
public class TccTestController {

    @DubboReference
    private UserTccExposeService userTccExposeService;

    @DubboReference
    private PersonTccExposeService personTccExposeService;

    @GetMapping("/test")
    @AuthorizationIgnore
    @GlobalTransactional
    public void testTcc(){
        userTccExposeService.tryBusiness("1");
        BusinessException.throwException("测试tcc异常");
        personTccExposeService.tryBusiness("2");
    }
}
