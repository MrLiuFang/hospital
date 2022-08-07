package com.lion.manage.service.department.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.service.department.DepartmentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:24
 */
@Service
public class DepartmentUserServiceImpl extends BaseServiceImpl<DepartmentUser> implements DepartmentUserService {

    @Autowired
    private DepartmentUserDao departmentUserDao;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public int deleteByDepartmentId(Long departmentId) {
        return departmentUserDao.deleteByDepartmentId(departmentId);
    }

    @Override
    @Transactional
    public void relationDepartment(Long userId, Long departmentId) {
        departmentUserDao.deleteByUserId(userId);
        redisTemplate.delete(RedisConstants.USER_DEPARTMENT+userId);
        if (Objects.nonNull(departmentId)) {
            DepartmentUser departmentUser = new DepartmentUser();
            departmentUser.setUserId(userId);
            departmentUser.setDepartmentId(departmentId);
            redisTemplate.opsForValue().set(RedisConstants.USER_DEPARTMENT+userId,departmentId,5, TimeUnit.MINUTES);
            this.save(departmentUser);
        }
    }

    @Override
    public Department findDepartment(Long userId) {
        return departmentDao.findByUserId(userId);
    }
}
