package com.lion.manage.service.department.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentResponsibleUserDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.dto.AddDepartmentDto;
import com.lion.manage.entity.department.dto.UpdateDepartmentDto;
import com.lion.manage.entity.department.vo.DetailsDepartmentVo;
import com.lion.manage.entity.department.vo.TreeDepartmentVo;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.service.department.DepartmentResponsibleUserService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.ward.WardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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

    @Autowired
    private DepartmentResponsibleUserDao departmentResponsibleUserDao;

    @Autowired
    private DepartmentResponsibleUserService departmentResponsibleUserService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private WardService wardService;

    @Override
    public Department add(AddDepartmentDto addDepartmentDto) {
        Department department = new Department();
        BeanUtils.copyProperties(addDepartmentDto,department);
        assertNameExist(department.getName(),null);
        department = this.save(department);
        departmentResponsibleUserService.save(addDepartmentDto.getResponsible(),department.getId());
        return department;
    }


    @Override
    public List<TreeDepartmentVo> treeList(String name) {
        List<Department> list= new ArrayList<>();
        if (StringUtils.hasText(name)){
            list = departmentDao.findByNameLike(name);
        }else {
            list = departmentDao.findByParentIdOrderByCreateDateTimeAsc(0L);
        }
        return convertVo(list);
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

    @Override
    public DetailsDepartmentVo details(Long id) {
        Department department = this.findById(id);
        DetailsDepartmentVo detailsDepartmentVo = new DetailsDepartmentVo();
        BeanUtils.copyProperties(department, detailsDepartmentVo);
        detailsDepartmentVo.setResponsibleUser(departmentResponsibleUserService.responsibleUser(department.getId()));
        return detailsDepartmentVo;
    }

    @Override
    public void update(UpdateDepartmentDto updateDepartmentDto) {
        Department department = new Department();
        BeanUtils.copyProperties(updateDepartmentDto,department);
        assertNameExist(department.getName(),department.getId());
        this.update(department);
        this.departmentResponsibleUserDao.deleteByDepartmentId(department.getId());
        departmentResponsibleUserService.save(updateDepartmentDto.getResponsible(),department.getId());
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(d->{
            Department department = this.findById(d.getId());
            if (Objects.nonNull(department) && !Objects.equals(department.getParentId(),0L) ) {
                deleteById(d.getId());
                departmentUserDao.deleteByDepartmentId(d.getId());
                departmentResponsibleUserDao.deleteByDepartmentId(d.getId());
                wardService.deleteByDepartmentId(d.getId());
                List<Department> list = departmentDao.findByParentIdOrderByCreateDateTimeAsc(d.getId());
                List<DeleteDto> tmp = new ArrayList<DeleteDto>();
                list.forEach(de ->{
                    DeleteDto deleteDto = new DeleteDto();
                    deleteDto.setId(de.getId());
                    tmp.add(deleteDto);
                });
                delete(tmp);
            }
        });
    }


    private List<TreeDepartmentVo> convertVo(List<Department> list){
        List<TreeDepartmentVo> returnList = new ArrayList<TreeDepartmentVo>();
        list.forEach(department -> {
            TreeDepartmentVo treeDepartmentVo = new TreeDepartmentVo();
            BeanUtils.copyProperties(department,treeDepartmentVo);
            treeDepartmentVo.setChildren(convertVo(departmentDao.findByParentIdOrderByCreateDateTimeAsc(department.getId())));
            treeDepartmentVo.setResponsibleUser(departmentResponsibleUserService.responsibleUser(department.getId()));
            returnList.add(treeDepartmentVo);
        });
        return returnList;
    }


}
