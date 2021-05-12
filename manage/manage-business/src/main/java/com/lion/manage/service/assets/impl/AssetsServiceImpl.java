package com.lion.manage.service.assets.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.assets.AssetsBorrowDao;
import com.lion.manage.dao.assets.AssetsDao;
import com.lion.manage.dao.assets.AssetsFaultDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.dto.AddAssetsDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsVo;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.service.assets.AssetsService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:15
 */
@Service
public class AssetsServiceImpl extends BaseServiceImpl<Assets> implements AssetsService {

    @Autowired
    private AssetsDao assetsDao;

    @Autowired
    private AssetsBorrowDao assetsBorrowDao;

    @Autowired
    private AssetsFaultDao assetsFaultDao;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RegionService regionService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private TagAssetsExposeService tagAssetsExposeService;

    @Override
    @Transactional
    //    @GlobalTransactional
    public void add(AddAssetsDto addAssetsDto) {
        Assets assets = new Assets();
        BeanUtils.copyProperties(addAssetsDto,assets);
        assertNameExist(assets.getName(),null);
        assertCodeExist(assets.getCode(),null);
        assertRegionExist(assets.getRegionId());
        assets = setBuildAndFloorAndDepartment(assets);
        assertBuildExist(assets.getBuildId());
        assertBuildFloorExist(assets.getBuildFloorId());
        assertDepartmentExist(assets.getDepartmentId());
        assets = this.save(assets);
        if (Objects.nonNull(addAssetsDto.getTagCode())) {
            tagAssetsExposeService.relation(assets.getId(), addAssetsDto.getTagCode());
        }
    }

    @Override
    @Transactional
    //    @GlobalTransactional
    public void update(UpdateAssetsDto updateAssetsDto) {
        Assets assets = new Assets();
        BeanUtils.copyProperties(updateAssetsDto,assets);
        assertNameExist(assets.getName(),assets.getId());
        assertCodeExist(assets.getCode(),assets.getId());
        assertRegionExist(assets.getRegionId());
        assets = setBuildAndFloorAndDepartment(assets);
        assertBuildExist(assets.getBuildId());
        assertBuildFloorExist(assets.getBuildFloorId());
        assertDepartmentExist(assets.getDepartmentId());
        this.update(assets);
        if (Objects.nonNull(updateAssetsDto.getTagCode())) {
            tagAssetsExposeService.relation(assets.getId(), updateAssetsDto.getTagCode());
        }else {
            tagAssetsExposeService.deleteByAssetsId(assets.getId());
        }
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            this.deleteById(deleteDto.getId());
            assetsBorrowDao.deleteByAssetsId(deleteDto.getId());
            assetsFaultDao.deleteByAssetsId(deleteDto.getId());
            tagAssetsExposeService.deleteByAssetsId(deleteDto.getId());
        });
    }

    @Override
    public DetailsAssetsVo details(Long id) {
        Assets assets = this.findById(id);
        if (Objects.isNull(assets)) {
            return null;
        }
        DetailsAssetsVo detailsAssetsVo = new DetailsAssetsVo();
        BeanUtils.copyProperties(assets,detailsAssetsVo);
        detailsAssetsVo.setBorrowCount(assetsBorrowDao.countByAssetsId(assets.getId()));
        detailsAssetsVo.setFaultCount(assetsFaultDao.countByAssetsId(assets.getId()));
        detailsAssetsVo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
        if (Objects.nonNull(assets.getBuildId())){
            Build build = buildService.findById(assets.getBuildId());
            if (Objects.nonNull(build)){
                detailsAssetsVo.setPosition(build.getName());
            }
        }
        if (Objects.nonNull(assets.getBuildFloorId())){
            BuildFloor buildFloor = buildFloorService.findById(assets.getBuildFloorId());
            if (Objects.nonNull(buildFloor)){
                detailsAssetsVo.setPosition(detailsAssetsVo.getPosition()+buildFloor.getName());
            }
        }
        if (Objects.nonNull(assets.getDepartmentId())){
            Department department = departmentService.findById(assets.getDepartmentId());
            if (Objects.nonNull(department)){
                detailsAssetsVo.setDepartmentName(department.getName());
            }
        }
        Tag tag = tagExposeService.findById(assets.getId());
        if (Objects.nonNull(tag)){
            detailsAssetsVo.setTagCode(tag.getTagCode());
            detailsAssetsVo.setTagId(tag.getId());
        }
        return detailsAssetsVo;
    }
    private void assertDepartmentExist(Long id) {
        Department department = this.departmentService.findById(id);
        if (Objects.isNull(department) ){
            BusinessException.throwException("该科室不存在");
        }
    }
    private void assertBuildExist(Long id) {
        Build build = this.buildService.findById(id);
        if (Objects.isNull(build) ){
            BusinessException.throwException("该建筑不存在");
        }
    }
    private void assertBuildFloorExist(Long id) {
        BuildFloor buildFloor = this.buildFloorService.findById(id);
        if (Objects.isNull(buildFloor) ){
            BusinessException.throwException("该建筑楼层不存在");
        }
    }
    private void assertRegionExist(Long id) {
        Region region = this.regionService.findById(id);
        if (Objects.isNull(region) ){
            BusinessException.throwException("该区域不存在");
        }
    }

    private void assertNameExist(String name, Long id) {
        Assets assets = assetsDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(assets)) || (Objects.nonNull(id) && Objects.nonNull(assets) && !Objects.equals(assets.getId(),id)) ){
            BusinessException.throwException("该资产名称已存在");
        }
    }

    private void assertCodeExist(String code, Long id) {
        Assets assets = assetsDao.findFirstByCode(code);
        if ((Objects.isNull(id) && Objects.nonNull(assets)) || (Objects.nonNull(id) && Objects.nonNull(assets) && !Objects.equals(assets.getId(),id)) ){
            BusinessException.throwException("该资产编码已存在");
        }
    }

    private Assets setBuildAndFloorAndDepartment(Assets assets){
        Region region = regionService.findById(assets.getRegionId());
        assets.setBuildId(region.getBuildId());
        assets.setBuildFloorId(region.getBuildFloorId());
        assets.setDepartmentId(region.getDepartmentId());
        return assets;
    }
}
