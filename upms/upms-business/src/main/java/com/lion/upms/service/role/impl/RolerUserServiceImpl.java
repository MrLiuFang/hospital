package com.lion.upms.service.role.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.upms.dao.role.RoleUserDao;
import com.lion.upms.entity.role.RoleUser;
import com.lion.upms.entity.role.vo.DetailsRoleUserVo;
import com.lion.upms.service.role.RolerUserService;
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
public class RolerUserServiceImpl extends BaseServiceImpl<RoleUser> implements RolerUserService {

    @Autowired
    private RoleUserDao roleUserDao;

    @Override
    public void deleteByRoleId(Long roleId) {
        roleUserDao.deleteByRoleId(roleId);
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
}
