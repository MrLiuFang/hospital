package com.lion.manage.expose.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentResponsibleUserDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentResponsibleUser;
import com.lion.manage.entity.department.vo.ResponsibleUserVo;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.service.department.DepartmentResponsibleUserService;
import com.lion.utils.BeanToMapUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24上午11:01
 */
@DubboService(interfaceClass = DepartmentResponsibleUserExposeService.class)
public class DepartmentResponsibleUserExposeServiceImpl extends BaseServiceImpl<DepartmentResponsibleUser> implements DepartmentResponsibleUserExposeService {

    @Autowired
    private DepartmentResponsibleUserService departmentResponsibleUserService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private DepartmentResponsibleUserDao departmentResponsibleUserDao;

    @Override
    @Transactional
    public void relationDepartment(Long userId, List<Long> departmentIds) {
        departmentResponsibleUserService.relationDepartment(userId,departmentIds);
    }

    @Override
    public List<Department> findDepartment(Long userId) {
        return departmentDao.findResponsibleDepartmentByUserId(userId);
    }

    @Override
    public List<Map<String,Object>> responsibleUser(Long departmentId) {
        List<ResponsibleUserVo> list = departmentResponsibleUserService.responsibleUser(departmentId);
        List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
        list.forEach(responsibleUserVo -> {
            returnList.add(BeanToMapUtil.transBeanToMap(responsibleUserVo));
        });
        return returnList;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        departmentResponsibleUserDao.deleteByUserId(userId);
    }
}
