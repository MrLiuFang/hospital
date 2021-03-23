package com.lion.manage.service.department.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.entity.department.dto.AddDepartmentDto;
import com.lion.manage.service.department.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:23
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<Department> implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private DepartmentUserDao departmentUserDao;

    @Override
    public Department add(AddDepartmentDto addDepartmentDto) {
        Department department = new Department();
        BeanUtils.copyProperties(addDepartmentDto,department);
        assertNameExist(department.getName(),null);
        department = this.save(department);
        Department finalDepartment = department;
        addDepartmentDto.getResponsible().forEach(id->{
            DepartmentUser departmentUser = new DepartmentUser();
            departmentUser.setDepartmentId(finalDepartment.getId());
            departmentUser.setUserId(id);
            departmentUser.setIsResponsible(true);
            departmentUserDao.save(departmentUser);
        });
        return department;
    }

    @Override
    public void assertNameExist(String name, Long id) {
        Department department = departmentDao.findFirstByName(name);
        if (Objects.isNull(id) && Objects.nonNull(department) ){
            BusinessException.throwException("该科室名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(department) && !department.getId().equals(id)){
            BusinessException.throwException("该科室名称已存在");
        }
    }


}
