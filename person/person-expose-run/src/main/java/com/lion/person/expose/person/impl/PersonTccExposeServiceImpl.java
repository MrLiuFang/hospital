package com.lion.person.expose.person.impl;

import com.lion.exception.BusinessException;
import com.lion.person.expose.person.PersonTccExposeService;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/15 下午8:33
 */
@DubboService(interfaceClass = PersonTccExposeService.class)
@Log
public class PersonTccExposeServiceImpl implements PersonTccExposeService {
    @Override
    public void tryBusiness(String id) {
        log.info("PersonTccExposeService:tryBusiness");
    }

    @Override
    public void commitTcc(BusinessActionContext context) {
        log.info("PersonTccExposeService:commitTcc:"+context.getActionContext("id"));
    }

    @Override
    public void cancelTcc(BusinessActionContext context) {
        log.info("PersonTccExposeService:cancelTcc:"+context.getActionContext("id"));
    }
}
