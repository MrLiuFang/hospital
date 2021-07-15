package com.lion.upms.expose.tcc.impl;

import com.lion.exception.BusinessException;
import com.lion.upms.expose.tcc.UserTccExposeService;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/15 下午8:26
 */
@DubboService(interfaceClass = UserTccExposeService.class)
@Log
public class UserTccExposeServiceImpl implements UserTccExposeService {
    @Override
    public void tryBusiness(String id) {
        log.info("UserTccExposeService:tryBusiness");
    }

    @Override
    public void commitTcc(BusinessActionContext context) {
        log.info("UserTccExposeService:commitTcc:"+context.getActionContext("id"));
    }

    @Override
    public void cancelTcc(BusinessActionContext context) {
        log.info("UserTccExposeService:cancelTcc:"+context.getActionContext("id"));
    }
}
