package com.lion.upms.expose.role.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.upms.dao.role.RoleDao;
import com.lion.upms.entity.role.Role;
import com.lion.upms.expose.role.RoleExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 下午8:11
 */
@DubboService
public class RoleExposeServiceImpl extends BaseServiceImpl<Role> implements RoleExposeService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public Role find(Long userId) {
        return roleDao.findByUserId(userId);
    }
}
