package com.lion.manage.expose.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashDao;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.expose.rule.WashExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午3:43
 **/
@DubboService(interfaceClass = WashExposeService.class)
public class WashExposeServiceImpl extends BaseServiceImpl<Wash> implements WashExposeService {

    @Autowired
    private WashDao washDao;

    @Override
    public Wash find(Long regionId, Long userId) {
        return washDao.find(regionId,userId);
    }

    @Override
    public List<Wash> find(Long regionId) {
        return washDao.find(regionId);
    }

    @Override
    public List<Wash> findLoopWash(Long userId) {
        return washDao.findLoopWash(userId, WashRuleType.LOOP);
    }

    @Override
    public List<Wash> findLoopWash(Boolean isAllUser) {
        return washDao.findFirstByTypeAndIsAllUser(WashRuleType.LOOP,isAllUser);
    }
}
