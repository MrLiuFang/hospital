package com.lion.event.service.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.event.entity.vo.DepartmentAssetsStatisticsDetails;
import com.lion.event.entity.vo.DepartmentStaffStatisticsDetails;
import com.lion.event.entity.vo.DepartmentStatisticsDetails;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.MapStatisticsService;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 上午9:36
 */
@Service
public class MapStatisticsServiceImpl implements MapStatisticsService {

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private TagUserExposeService tagUserExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Autowired
    private CurrentPositionService currentPositionService;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private TagAssetsExposeService tagAssetsExposeService;

    @Override
    public List<RegionStatisticsDetails> regionStatisticsDetails(Long buildFloorId) {
        List<Region> regionList = regionExposeService.findByBuildFloorId(buildFloorId);
        Map<Long,RegionStatisticsDetails> map = new HashMap<>();
        for (Region region : regionList){
            RegionStatisticsDetails regionStatisticsDetails = new RegionStatisticsDetails();
            regionStatisticsDetails.setRegionId(region.getId());
            regionStatisticsDetails.setRegionName(region.getName());
            regionStatisticsDetails.setCoordinates(region.getCoordinates());
            map.put(region.getId(),regionStatisticsDetails);
        };
        map = currentPositionService.groupCount(buildFloorId,map);
        map = systemAlarmService.groupCount(buildFloorId,map);

        List<Map<String, Object>> assestCount = assetsExposeService.count(buildFloorId);
        for (Map m :assestCount){
            if (map.containsKey(m.get("region_id")) ){
                RegionStatisticsDetails regionStatisticsDetails = map.get(m.get("region_id"));
                regionStatisticsDetails.setAssetsCount(((Long) m.get("count")).intValue());
            }
        }

        List<RegionStatisticsDetails> returnList = new ArrayList<>();
        map.forEach((key,value) ->{
            returnList.add(value);
        });
        return returnList;
    }

    @Override
    public List<DepartmentStatisticsDetails> departmentStatisticsDetails() {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        List<DepartmentStatisticsDetails> returnList = new ArrayList<>();
        list.forEach(department -> {
            DepartmentStatisticsDetails departmentStatisticsDetails = new DepartmentStatisticsDetails();
            List<Region> regionList = regionExposeService.findByDepartmentId(department.getId());
            List<Long> deviceGroupIds = new ArrayList<>();
            regionList.forEach(region -> {
                if (Objects.nonNull(region.getDeviceGroupId())) {
                    deviceGroupIds.add(region.getDeviceGroupId());
                }
            });
            departmentStatisticsDetails.setLowPowerDeviceCount(deviceExposeService.countDevice(deviceGroupIds,1));
            departmentStatisticsDetails.setLowPowerTagCount(tagExposeService.countTag(department.getId(),1));
            Map<String,Integer> map = systemAlarmService.groupCount(department.getId());
            if (map.containsKey("allAlarmCount")) {
                departmentStatisticsDetails.setAllAlarmCount(map.get("allAlarmCount"));
            }
            if (map.containsKey("unalarmCount")) {
                departmentStatisticsDetails.setUnalarmCount(map.get("unalarmCount"));
            }
            if (map.containsKey("alarmCount")) {
                departmentStatisticsDetails.setAlarmCount(map.get("alarmCount"));
            }
            departmentStatisticsDetails.setAssetsCount(assetsExposeService.countByDepartmentId(department.getId()));
            departmentStatisticsDetails.setTagCount(tagExposeService.countTag(department.getId()));
            departmentStatisticsDetails.setCctvCount(cctvExposeService.count(department.getId()));
            returnList.add(departmentStatisticsDetails);
        });
        return returnList;
    }

    @Override
    public List<DepartmentStaffStatisticsDetails> departmentStaffStatisticsDetails(String name) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        List<DepartmentStaffStatisticsDetails> returnList = new ArrayList<>();
        list.forEach(department -> {
            DepartmentStaffStatisticsDetails departmentStaffStatisticsDetails = new DepartmentStaffStatisticsDetails();
            departmentStaffStatisticsDetails.setDepartmentName(department.getName());
            departmentStaffStatisticsDetails.setDepartmentId(department.getId());
            departmentStaffStatisticsDetails.setStaffCount(departmentUserExposeService.count(department.getId()));
            List<Long> userIds = departmentUserExposeService.findAllUser(department.getId(),name);
            List<DepartmentStaffStatisticsDetails.DepartmentStaff> listStaff = new ArrayList<>();
            userIds.forEach(id->{
                User user = userExposeService.findById(id);
                if (Objects.nonNull(user)) {
                    DepartmentStaffStatisticsDetails.DepartmentStaff staff = new DepartmentStaffStatisticsDetails.DepartmentStaff();
                    staff.setUserId(user.getId());
                    staff.setUserName(user.getName());
                    staff.setHeadPortrait(user.getHeadPortrait());
                    staff.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                    staff.setNumber(user.getNumber());
                    TagUser tagUser = tagUserExposeService.findByUserId(user.getId());
                    if (Objects.nonNull(tagUser)) {
                        Tag tag = tagExposeService.findById(tagUser.getTagId());
                        if (Objects.nonNull(tag)){
                            staff.setBattery(tag.getBattery());
                        }
                    }
                    listStaff.add(staff);
                }
            });
            departmentStaffStatisticsDetails.setDepartmentStaffs(listStaff);
            returnList.add(departmentStaffStatisticsDetails);
        });

        return returnList;
    }

    @Override
    public List<DepartmentAssetsStatisticsDetails> departmentAssetsStatisticsDetails(String keyword) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        List<DepartmentAssetsStatisticsDetails> returnList = new ArrayList<>();
        list.forEach(department -> {
            DepartmentAssetsStatisticsDetails departmentAssetsStatisticsDetails = new DepartmentAssetsStatisticsDetails();
            departmentAssetsStatisticsDetails.setDepartmentName(department.getName());
            departmentAssetsStatisticsDetails.setDepartmentId(department.getId());
            departmentAssetsStatisticsDetails.setAssetsCount(assetsExposeService.countByDepartmentId(department.getId()));
            List<Assets> assets = assetsExposeService.findByDepartmentId(department.getId(),"%"+keyword+"%" ,"%"+keyword+"%");
            List<DepartmentAssetsStatisticsDetails.AssetsVo> assetsVos= new ArrayList<>();
            assets.forEach(a ->{
                DepartmentAssetsStatisticsDetails.AssetsVo vo = new DepartmentAssetsStatisticsDetails.AssetsVo();
                BeanUtils.copyProperties(a,vo);
                TagAssets tagAssets = tagAssetsExposeService.find(a.getId());
                if (Objects.nonNull(tagAssets)) {
                    Tag tag = tagExposeService.findById(tagAssets.getTagId());
                    if (Objects.nonNull(tag)){
                        vo.setBattery(tag.getBattery());
                    }
                }
                assetsVos.add(vo);
            });
            departmentAssetsStatisticsDetails.setAssets(assetsVos);
            returnList.add(departmentAssetsStatisticsDetails);
        });
        return returnList;
    }
}
