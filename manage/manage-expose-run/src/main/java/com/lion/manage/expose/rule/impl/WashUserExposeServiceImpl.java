package com.lion.manage.expose.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashUserDao;
import com.lion.manage.entity.rule.WashUser;
import com.lion.manage.expose.rule.WashUserExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午9:14
 **/
@DubboService(interfaceClass = WashUserExposeService.class)
public class WashUserExposeServiceImpl extends BaseServiceImpl<WashUser> implements WashUserExposeService {

    @Autowired
    private WashUserDao washUserDao;

    @Override
    public List<WashUser> find(Long userId) {
        return washUserDao.findByUserId(userId);
    }
}
