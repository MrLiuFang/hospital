package com.lion.upms.service.role.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.upms.dao.role.RoleUserDao;
import com.lion.upms.entity.role.RoleUser;
import com.lion.upms.service.role.RoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:14
 */
@Service
public class RoleUserServiceImpl extends BaseServiceImpl<RoleUser> implements RoleUserService {

    @Autowired
    private RoleUserDao roleUserDao;

    @Override
    public void deleteByRoleId(Long roleId) {
        roleUserDao.deleteByRoleId(roleId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        roleUserDao.deleteByUserId(userId);
    }

    @Override
    public void relationRole(Long userId, Long roleId) {
        roleUserDao.deleteByUserId(userId);
        if (Objects.nonNull(roleId) && roleId>0) {
            RoleUser roleUser = new RoleUser();
            roleUser.setRoleId(roleId);
            roleUser.setUserId(userId);
            roleUserDao.save(roleUser);
        }
    }

    @Override
    public List<RoleUser> find(Long roleId) {
        return roleUserDao.findByRoleId(roleId);
    }
}
