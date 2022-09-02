package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.service.department.DepartmentUserService;
import com.lion.upms.entity.enums.State;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public List<Long> findAllUser(Long departmentId, String name, List<Long> userIds) {
        List<Long> returnList = new ArrayList<Long>();
        List<User> userList = userExposeService.find(name,userIds);
        List<Long> _userIds = new ArrayList<>();
        if (Objects.nonNull(userList)) {
            userList.forEach(user -> {
                _userIds.add(user.getId());
            });
        }
        if (Objects.nonNull(userIds) && userIds.size()>0) {
            List<DepartmentUser> list = departmentUserDao.findByDepartmentIdAndUserIdIn(departmentId,_userIds);
            list.forEach(departmentUser -> {
                returnList.add(departmentUser.getUserId());
            });
        }
        return returnList;
    }

    @Override
    public Integer count(Long departmentId, State deviceState, List<Long> userIds) {
        List<DepartmentUser>  list = null;
        if (Objects.isNull(deviceState) && (Objects.isNull(userIds) || userIds.size()<=0)) {
            return departmentUserDao.countByDepartmentId(departmentId);
        }else if (Objects.isNull(deviceState) && (Objects.nonNull(userIds) || userIds.size()>0)) {
            return departmentUserDao.countByDepartmentIdAndUserIdIn(departmentId,userIds);
        }

        if (Objects.nonNull(deviceState) && (Objects.isNull(userIds) || userIds.size()<=0)) {
            list = departmentUserDao.findByDepartmentId(departmentId);
        }else if (Objects.nonNull(deviceState) && (Objects.nonNull(userIds) || userIds.size()>0)){
            list = departmentUserDao.findByDepartmentIdAndUserIdIn(departmentId,userIds);
        }
        List<Long> ids = new ArrayList<>();
        ids.add(Long.MAX_VALUE);
        list.forEach(departmentUser -> {
            ids.add(departmentUser.getUserId());
        });
        return userExposeService.count(ids,deviceState);
    }
}
