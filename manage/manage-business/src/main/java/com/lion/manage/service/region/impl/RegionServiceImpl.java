package com.lion.manage.service.region.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.region.RegionExposeObjectDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionCctvService;
import com.lion.manage.service.region.RegionExposeObjectService;
import com.lion.manage.service.region.RegionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:08
 */
@Service
public class RegionServiceImpl extends BaseServiceImpl<Region> implements RegionService {

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private RegionCctvService regionCctvService;

    @Autowired
    private RegionExposeObjectService regionExposeObjectService;

    @Autowired
    private RegionCctvDao regionCctvDao;

    @Autowired
    private RegionExposeObjectDao regionExposeObjectDao;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public List<Region> find(Long departmentId) {
        return regionDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Region> findByBuildFloorId(Long buildFloorId) {
        return regionDao.findByBuildFloorId(buildFloorId);
    }

    @Override
    public void add(AddRegionDto addRegionDto) {
        Region region = new Region();
        BeanUtils.copyProperties(addRegionDto,region);
        assertBuildExist(region.getBuildId());
        assertBuildFloorExist(region.getBuildId(),region.getBuildFloorId());
        assertDepartmentExist(region.departmentId);
        assertNameExist(region.getName(),null);
        if (addRegionDto.isPublic && (Objects.isNull(addRegionDto.getExposeObjects()) ||addRegionDto.getExposeObjects().size()<=0) ){
            BusinessException.throwException("请选择公开对象");
        }
        region = save(region);
        regionCctvService.save(region.getId(),addRegionDto.getCctvIds());
        regionExposeObjectService.save(region.getId(),addRegionDto.getExposeObjects());
    }

    @Override
    public void add(UpdateRegionDto updateRegionDto) {
        Region region = new Region();
        BeanUtils.copyProperties(updateRegionDto,region);
        assertBuildExist(region.getBuildId());
        assertBuildFloorExist(region.getBuildId(),region.getBuildFloorId());
        assertDepartmentExist(region.departmentId);
        assertNameExist(region.getName(),region.getId());
        if (updateRegionDto.isPublic && (Objects.isNull(updateRegionDto.getExposeObjects()) ||updateRegionDto.getExposeObjects().size()<=0) ){
            BusinessException.throwException("请选择公开对象");
        }
        update(region);
        regionCctvService.save(region.getId(),updateRegionDto.getCctvIds());
        regionExposeObjectService.save(region.getId(),updateRegionDto.getExposeObjects());
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            deleteById(deleteDto.getId());
            regionCctvDao.deleteByRegionId(deleteDto.getId());
            regionExposeObjectDao.deleteByRegionId(deleteDto.getId());
        });
    }

    private void assertNameExist(String name, Long id) {
        Region region = regionDao.findFirstByName(name);
        if (Objects.isNull(id) && Objects.nonNull(region) ){
            BusinessException.throwException("该区域名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(region) && !region.getId().equals(id)){
            BusinessException.throwException("该区域名称已存在");
        }
    }

    private void assertBuildExist(Long buildId) {
        Build build = buildService.findById(buildId);
        if (Objects.isNull(build)){
            BusinessException.throwException("建筑不存在");
        }
    }

    private void assertDepartmentExist(Long departmentId) {
        Department department = departmentService.findById(departmentId);
        if (Objects.isNull(department)){
            BusinessException.throwException("科室不存在");
        }
    }

    private void assertBuildFloorExist(Long buildId,Long buildFloorId) {
        BuildFloor buildFloor = buildFloorService.findById(buildFloorId);
        if (Objects.isNull(buildFloor)){
            BusinessException.throwException("建筑楼层不存在");
        }
        if (!Objects.equals(buildFloor.getBuildId(),buildId)) {
            BusinessException.throwException("该建筑不存在此楼层");
        }
    }
}
