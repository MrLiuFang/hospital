package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.service.department.DepartmentUserService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sun.swing.StringUIClientPropertyKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:29
 */
@DubboService(interfaceClass = DepartmentUserExposeService.class)
public class DepartmentUserExposeServiceImpl extends BaseServiceImpl<DepartmentUser> implements DepartmentUserExposeService {

    @Autowired
    private DepartmentUserDao departmentUserDao;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private UserExposeService userExposeService;

    @Override
    @Transactional
    public void relationDepartment(Long userId, Long departmentId) {
        departmentUserService.relationDepartment(userId,departmentId);
    }

    @Override
    public Department findDepartment(Long userId) {
        return departmentDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        departmentUserDao.deleteByUserId(userId);
    }

    @Override
    public List<Long> findAllUser(Long departmentId) {
        List<DepartmentUser> list = departmentUserDao.findByDepartmentId(departmentId);
        List<Long> returnList = new ArrayList<Long>();
        list.forEach(departmentUser -> {
            returnList.add(departmentUser.getUserId());
        });
        return returnList;
    }

    @Override
    public List<Long> findAllUser(Long departmentId, String name) {
        if (StringUtils.hasText(name)) {
            List<User> userList = userExposeService.findByName(name);
            List<Long> userIds = new ArrayList<>();
            userList.forEach(user -> {
                userIds.add(user.getId());
            });
            if (Objects.nonNull(userIds) && userIds.size()>0) {
                List<DepartmentUser> list = departmentUserDao.findByDepartmentIdAndUserIdIn(departmentId,userIds);
                List<Long> returnList = new ArrayList<Long>();
                list.forEach(departmentUser -> {
                    returnList.add(departmentUser.getUserId());
                });
                return returnList;
            }
        }
        return findAllUser(departmentId);
    }

    @Override
    public Integer count(Long departmentId) {
        return departmentUserDao.countByDepartmentId(departmentId);
    }
}
