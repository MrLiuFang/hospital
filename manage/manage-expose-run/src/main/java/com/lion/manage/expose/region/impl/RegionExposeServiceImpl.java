package com.lion.manage.expose.region.impl;

import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.core.PageResultData;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionType;
import com.lion.manage.entity.region.vo.ListRegionVo;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.region.RegionTypeService;
import com.lion.manage.service.rule.WashTemplateService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/4/24 下午2:58
 **/
@DubboService(interfaceClass = RegionExposeService.class)
public class RegionExposeServiceImpl extends BaseServiceImpl<Region> implements RegionExposeService {

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private RegionService regionService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private WashTemplateService washTemplateService;

    @Autowired
    private RegionTypeService regionTypeService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

//    @Override
//    public Region find(Long deviceGroupId) {
//        return regionDao.findFirstByDeviceGroupId(deviceGroupId);
//    }

//    @Override
//    @Transactional
//    public void deleteDeviceGroup(Long deviceGroupId) {
//        regionDao.deleteDeviceGroup(deviceGroupId);
//    }

    @Override
    public List<Region> findByBuildFloorId(Long buildFloorId) {
        return regionDao.findByBuildFloorId(buildFloorId);
    }

    @Override
    public List<Region> findByDepartmentId(Long departmentId) {
        return regionDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Region> findByDepartmentIds(List<Long> departmentIds) {
        return regionDao.findByDepartmentIdIn(departmentIds);
    }

    @Override
    public PageResultData<List<Region>> find(LionPage lionPage) {
        Page<Region> page = findNavigator(lionPage);
        PageResultData pageResultData = new PageResultData(page.getContent(),new LionPage(page.getNumber(),page.getSize()),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public List<ListRegionVo> find(String keyword, Long departmentId) {
        List<Region> list = regionDao.find(keyword,keyword,departmentId);
        List<ListRegionVo> returnList = new ArrayList<>();
        list.forEach(region -> {
            ListRegionVo vo = new ListRegionVo();
            BeanUtils.copyProperties(region,vo);
            com.lion.core.Optional<Build> optionalBuild = buildService.findById(region.getBuildId());
            if (optionalBuild.isPresent()){
                vo.setBuildName(optionalBuild.get().getName());
            }
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorService.findById(region.getBuildFloorId());
            if (optionalBuildFloor.isPresent()){
                vo.setBuildFloorName(optionalBuildFloor.get().getName());
            }
            com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(region.getDepartmentId());
            if (optionalDepartment.isPresent()){
                vo.setDepartmentName(optionalDepartment.get().getName());
            }
            if (Objects.nonNull(region.getWashTemplateId())) {
                vo.setWashTemplateVo(washTemplateService.details(region.getWashTemplateId()));
            }
            com.lion.core.Optional<RegionType> optionalRegionType = regionTypeService.findById(region.getRegionTypeId());
            vo.setRegionType(optionalRegionType.isPresent()?optionalRegionType.get():null);
            vo.setDevices(deviceExposeService.findByRegionIdAndDeviceClassify(region.getId(), DeviceClassify.HAND_WASHING));
            returnList.add(vo);
        });
        return returnList;
    }

    @Override
    public Optional<Region> find(String name) {
        Region region = regionDao.findFirstByName(name);
        return Objects.nonNull(region)?Optional.of(region):Optional.empty();
    }
}
