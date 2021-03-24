package com.lion.manage.service.department;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.DepartmentResponsibleUser;
import com.lion.manage.entity.department.vo.ResponsibleUserVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午8:16
 */
public interface DepartmentResponsibleUserService extends BaseService<DepartmentResponsibleUser> {

    /**
     * 保存负责人
     * @param responsible
     * @param departmentId
     */
    public void save(List<Long> responsible,Long departmentId);

    /**
     * 删除科室关联负责人
     * @param departmentId
     * @return
     */
    public int deleteByDepartmentId(Long departmentId);

    /**
     * 关联科室负责人
     * @param userId
     * @param departmentIds
     */
    public void relationDepartment(Long userId,List<Long> departmentIds);

    /**
     * 获取科室负责人
     * @param departmentId
     * @return
     */
    public List<ResponsibleUserVo> responsibleUser(Long departmentId);
}
