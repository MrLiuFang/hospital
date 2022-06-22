package com.lion.manage.service.department;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.dto.AddDepartmentDto;
import com.lion.manage.entity.department.dto.UpdateDepartmentDto;
import com.lion.manage.entity.department.vo.DetailsDepartmentVo;
import com.lion.manage.entity.department.vo.TreeDepartmentVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:22
 */
public interface DepartmentService extends BaseService<Department> {

    /**
     * 新增科室
     * @param addDepartmentDto
     * @return
     */
    public Department add(AddDepartmentDto addDepartmentDto);

    /**
     * 科室属性列表
     * @param name
     * @return
     */
    List<TreeDepartmentVo> treeList(String name);

    /**
     * 判断科室名称是否存在
     * @param name
     * @param id
     */
    void assertNameExist(String name,Long id);

    /**
     * 科室详情
     * @param id
     * @return
     */
    public DetailsDepartmentVo details(Long id);

    /**
     * 修改科室
     * @param updateDepartmentDto
     */
    public void update(UpdateDepartmentDto updateDepartmentDto);

    /**
     * 删除科室
     * @param deleteDtoList
     */
    void delete( List<DeleteDto> deleteDtoList);


    /**
     *  获取有权限的部门
     *
     * @return
     */
    public List<Department> ownerDepartment();

    /**
     * 获取负责的部门
     * @param departmentId
     * @return
     */
    public List<Long> responsibleDepartment(Long departmentId);




}
