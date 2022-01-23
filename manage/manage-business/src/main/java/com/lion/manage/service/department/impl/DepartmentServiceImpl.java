package com.lion.manage.service.department.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.department.DepartmentDao;
import com.lion.manage.dao.department.DepartmentResponsibleUserDao;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentResponsibleUser;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.entity.department.dto.AddDepartmentDto;
import com.lion.manage.entity.department.dto.UpdateDepartmentDto;
import com.lion.manage.entity.department.vo.DetailsDepartmentVo;
import com.lion.manage.entity.department.vo.TreeDepartmentVo;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.service.department.DepartmentResponsibleUserService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.ward.WardService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.expose.role.RoleExposeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private RegionDao regionDao;

    @Autowired
    private WardService wardService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private RoleExposeService roleExposeService;


    @Override
    @Transactional
    public Department add(AddDepartmentDto addDepartmentDto) {
        Department department = new Department();
        BeanUtils.copyProperties(addDepartmentDto,department);
        assertNameExist(department.getName(),null);
        department = this.save(department);
        departmentResponsibleUserService.save(addDepartmentDto.getResponsible(),department.getId());
        persistenceRedis(department);
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
        if ((Objects.isNull(id) && Objects.nonNull(department)) || (Objects.nonNull(id) && Objects.nonNull(department) && !Objects.equals(department.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000077"));
        }
    }

    @Override
    public DetailsDepartmentVo details(Long id) {
        com.lion.core.Optional<Department> optionalDepartment = this.findById(id);
        DetailsDepartmentVo detailsDepartmentVo = new DetailsDepartmentVo();
        if (optionalDepartment.isEmpty()) {
            return detailsDepartmentVo;
        }
        Department department = optionalDepartment.get();
        BeanUtils.copyProperties(department, detailsDepartmentVo);
        detailsDepartmentVo.setResponsibleUser(departmentResponsibleUserService.responsibleUser(department.getId()));
        List<DepartmentResponsibleUser> list = departmentResponsibleUserDao.findByDepartmentId(department.getId());
        List<Long> responsible = new ArrayList<>();
        list.forEach(departmentResponsibleUser -> {
            responsible.add(departmentResponsibleUser.getUserId());
        });
        detailsDepartmentVo.setResponsible(responsible);
        return detailsDepartmentVo;
    }

    @Override
    @Transactional
    public void update(UpdateDepartmentDto updateDepartmentDto) {
        Department department = new Department();
        BeanUtils.copyProperties(updateDepartmentDto,department);
        assertNameExist(department.getName(),department.getId());
        this.update(department);
        this.departmentResponsibleUserDao.deleteByDepartmentId(department.getId());
        departmentResponsibleUserService.save(updateDepartmentDto.getResponsible(),department.getId());
        persistenceRedis(department);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(d->{
            com.lion.core.Optional<Department> optional = this.findById(d.getId());
            if (optional.isPresent() ) {
                deleteById(d.getId());
                departmentUserDao.deleteByDepartmentId(d.getId());
                departmentResponsibleUserDao.deleteByDepartmentId(d.getId());
                wardService.deleteByDepartmentId(d.getId());
                List<Region> listRegion = regionDao.findByDepartmentId(d.getId());
                List<DeleteDto> regionDeleteDtoList = new ArrayList<DeleteDto>();
                listRegion.forEach(region -> {
                    DeleteDto dto = new DeleteDto();
                    dto.setId(region.getId());
                    regionDeleteDtoList.add(dto);
                });
                if (regionDeleteDtoList.size()>0) {
                    regionService.delete(regionDeleteDtoList);
                }
                redisTemplate.delete(RedisConstants.DEPARTMENT+d.getId());
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

    @Override
    public List<Department> ownerDepartment() {
        List<Long> listIds = responsibleDepartment(null);
        DepartmentUser departmentUser = departmentUserDao.findFirstByUserId(CurrentUserUtil.getCurrentUserId());
        if (Objects.nonNull(departmentUser)) {
            com.lion.core.Optional<Department> optional = findById(departmentUser.getDepartmentId());
            if (optional.isPresent()) {
                listIds.add(optional.get().getId());
            }
        }
        return departmentDao.findByIdIn(listIds);
    }

    @Override
    public List<Long> responsibleDepartment(Long departmentId) {
        List<Long> departmentIds = new ArrayList<>();
        Long userId = CurrentUserUtil.getCurrentUserId();
        Role role = roleExposeService.find(userId);
        if (Objects.nonNull(role)) {
            if (role.getCode().toLowerCase().indexOf("admin") < 0) {
                List<Department> list = new ArrayList<>();
                if (Objects.nonNull(departmentId)) {
                    list = departmentResponsibleUserService.findDepartment(userId, departmentId);
                } else {
                    list = departmentDao.findResponsibleDepartmentByUserId(userId);
                }
                list.forEach(department -> {
                    departmentIds.add(department.getId());
                });
                if (departmentIds.size()<=0) {
                    departmentIds.add(Long.MAX_VALUE);
                }
            } else {
                if (Objects.nonNull(departmentId)) {
                    departmentIds.add(departmentId);
                }else {
                    List<Department> list = findAll();
                    list.forEach(department -> {
                        departmentIds.add(department.getId());
                    });
                }
            }
        }
//        else {
//            departmentIds.add(Long.MAX_VALUE);
//        }
        return departmentIds;
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

    private void persistenceRedis(Department department){
        redisTemplate.opsForValue().set(RedisConstants.DEPARTMENT+department.getId(),department,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }


}
