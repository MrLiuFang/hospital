package com.lion.event.service.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.expose.file.FileExposeService;
import com.lion.common.utils.RedisUtil;
import com.lion.core.ResultData;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.event.dao.TagRecordDao;
import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.TagRecord;
import com.lion.event.entity.vo.*;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.MapStatisticsService;
import com.lion.event.service.PositionService;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Autowired
    private TagRecordDao tagRecordDao;

    @Autowired
    private PositionService positionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

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
    public List<DepartmentStatisticsDetailsVo> departmentStatisticsDetails() {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        List<DepartmentStatisticsDetailsVo> returnList = new ArrayList<>();
        list.forEach(department -> {
            DepartmentStatisticsDetailsVo departmentStatisticsDetailsVo = new DepartmentStatisticsDetailsVo();
            List<Region> regionList = regionExposeService.findByDepartmentId(department.getId());
            List<Long> deviceGroupIds = new ArrayList<>();
            regionList.forEach(region -> {
                if (Objects.nonNull(region.getDeviceGroupId())) {
                    deviceGroupIds.add(region.getDeviceGroupId());
                }
            });
            departmentStatisticsDetailsVo.setLowPowerDeviceCount(deviceExposeService.countDevice(deviceGroupIds,1));
            departmentStatisticsDetailsVo.setLowPowerTagCount(tagExposeService.countTag(department.getId(),1));
            Map<String,Integer> map = systemAlarmService.groupCount(department.getId());
            if (map.containsKey("allAlarmCount")) {
                departmentStatisticsDetailsVo.setAllAlarmCount(map.get("allAlarmCount"));
            }
            if (map.containsKey("unalarmCount")) {
                departmentStatisticsDetailsVo.setUnalarmCount(map.get("unalarmCount"));
            }
            if (map.containsKey("alarmCount")) {
                departmentStatisticsDetailsVo.setAlarmCount(map.get("alarmCount"));
            }
            departmentStatisticsDetailsVo.setAssetsCount(assetsExposeService.countByDepartmentId(department.getId()));
            departmentStatisticsDetailsVo.setTagCount(tagExposeService.countTag(department.getId()));
            departmentStatisticsDetailsVo.setCctvCount(cctvExposeService.count(department.getId()));
            returnList.add(departmentStatisticsDetailsVo);
        });
        return returnList;
    }

    @Override
    public DepartmentStaffStatisticsDetailsVo departmentStaffStatisticsDetails(String name) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        DepartmentStaffStatisticsDetailsVo departmentStaffStatisticsDetailsVo = new DepartmentStaffStatisticsDetailsVo();
        List<DepartmentStaffStatisticsDetailsVo.DepartmentVo> departmentVos = new ArrayList<>();
        departmentStaffStatisticsDetailsVo.setDepartmentVos(departmentVos);
        list.forEach(department -> {
            DepartmentStaffStatisticsDetailsVo.DepartmentVo vo = new DepartmentStaffStatisticsDetailsVo.DepartmentVo();
            vo.setDepartmentName(department.getName());
            vo.setDepartmentId(department.getId());
            departmentStaffStatisticsDetailsVo.setStaffCount(departmentStaffStatisticsDetailsVo.getStaffCount() + departmentUserExposeService.count(department.getId()));
            List<Long> userIds = departmentUserExposeService.findAllUser(department.getId(),name);
            List<DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo> listStaff = new ArrayList<>();
            userIds.forEach(id->{
                User user = userExposeService.findById(id);
                if (Objects.nonNull(user)) {
                    DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo staff = new DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo();
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
            vo.setDepartmentStaffVos(listStaff);
            departmentVos.add(vo);
        });
        return departmentStaffStatisticsDetailsVo;
    }

    @Override
    public DepartmentAssetsStatisticsDetailsVo departmentAssetsStatisticsDetails(String keyword) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        DepartmentAssetsStatisticsDetailsVo departmentAssetsStatisticsDetailsVo =new DepartmentAssetsStatisticsDetailsVo();
        List<DepartmentAssetsStatisticsDetailsVo.DepartmentVo> departmentVos = new ArrayList<>();
        departmentAssetsStatisticsDetailsVo.setDepartmentVos(departmentVos);
        list.forEach(department -> {
            DepartmentAssetsStatisticsDetailsVo.DepartmentVo vo = new DepartmentAssetsStatisticsDetailsVo.DepartmentVo();
            vo.setDepartmentName(department.getName());
            vo.setDepartmentId(department.getId());
            departmentAssetsStatisticsDetailsVo.setAssetsCount(departmentAssetsStatisticsDetailsVo.getAssetsCount() + assetsExposeService.countByDepartmentId(department.getId()));
            List<Assets> assets = assetsExposeService.findByDepartmentId(department.getId(),keyword ,keyword);
            List<DepartmentAssetsStatisticsDetailsVo.AssetsVo> assetsVos= new ArrayList<>();
            assets.forEach(a ->{
                DepartmentAssetsStatisticsDetailsVo.AssetsVo assetsVo = new DepartmentAssetsStatisticsDetailsVo.AssetsVo();
                BeanUtils.copyProperties(a,assetsVo);
                TagAssets tagAssets = tagAssetsExposeService.find(a.getId());
                if (Objects.nonNull(tagAssets)) {
                    Tag tag = tagExposeService.findById(tagAssets.getTagId());
                    if (Objects.nonNull(tag)){
                        assetsVo.setBattery(tag.getBattery());
                    }
                }
                assetsVos.add(assetsVo);
            });
            vo.setAssetsVos(assetsVos);
            departmentVos.add(vo);
        });
        return departmentAssetsStatisticsDetailsVo;
    }

    @Override
    public DepartmentTagStatisticsDetailsVo departmentTagStatisticsDetails(String keyword) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        DepartmentTagStatisticsDetailsVo departmentTagStatisticsDetailsVo = new DepartmentTagStatisticsDetailsVo();
        List<DepartmentTagStatisticsDetailsVo.DepartmentVo> departmentVos = new ArrayList<>();
        departmentTagStatisticsDetailsVo.setDepartmentVos(departmentVos);
        list.forEach(department -> {
            DepartmentTagStatisticsDetailsVo.DepartmentVo departmentVo = new DepartmentTagStatisticsDetailsVo.DepartmentVo();
            departmentTagStatisticsDetailsVo.setTagCount(departmentTagStatisticsDetailsVo.getTagCount() + tagExposeService.countTag(department.getId(), TagPurpose.THERMOHYGROGRAPH));
            List<Tag> tagList = tagExposeService.find(department.getId(),TagPurpose.THERMOHYGROGRAPH,keyword);
            List<DepartmentTagStatisticsDetailsVo.TagVo> tagVos = new ArrayList<>();
            tagList.forEach(tag -> {
                DepartmentTagStatisticsDetailsVo.TagVo vo = new DepartmentTagStatisticsDetailsVo.TagVo();
                BeanUtils.copyProperties(tag,vo);
                TagRecord record = tagRecordDao.find(tag.getId());
                if (Objects.nonNull(record)) {
                    if (Objects.nonNull(record.getT())){
                        vo.setTemperature(record.getT());
                    }
                    if (Objects.nonNull(record.getH())){
                        vo.setHumidity(record.getH());
                    }
                    if (Objects.nonNull(record.getDdt())){
                        vo.setDataDateTime(record.getDdt());
                    }
                }
                tagVos.add(vo);
            });
            departmentVo.setTagVos(tagVos);
            departmentVos.add(departmentVo);
        });
        return departmentTagStatisticsDetailsVo;
    }

    @Override
    public StaffDetailsVo staffDetails(Long userId) {
        User user = userExposeService.findById(userId);
        if (Objects.isNull(user)){
            return null;
        }
        StaffDetailsVo staffDetailsVo = new StaffDetailsVo();
        BeanUtils.copyProperties(user,staffDetailsVo);
        TagUser tagUser = tagUserExposeService.findByUserId(userId);
        if (Objects.nonNull(tagUser)){
            Tag tag = tagExposeService.findById(tagUser.getTagId());
            if (Objects.nonNull(tag)){
                staffDetailsVo.setBattery(tag.getBattery());
            }
        }
        Department department = departmentUserExposeService.findDepartment(userId);
        if (Objects.nonNull(department)){
            staffDetailsVo.setDepartmentId(department.getId());
            staffDetailsVo.setDepartmentName(department.getName());
        }
        List<Department> departmentResponsibleList = departmentResponsibleUserExposeService.findDepartment(userId);
        List<StaffDetailsVo.DepartmentResponsibleVo> departmentResponsibleVos = new ArrayList<>();
        departmentResponsibleList.forEach(d -> {
            StaffDetailsVo.DepartmentResponsibleVo vo = new StaffDetailsVo.DepartmentResponsibleVo();
            vo.setDepartmentId(d.getId());
            vo.setDepartmentName(d.getName());
            departmentResponsibleVos.add(vo);
        });
        staffDetailsVo.setDepartmentResponsibleVos(departmentResponsibleVos);
        LocalDateTime now = LocalDateTime.now();
        staffDetailsVo.setPositions(positionService.find(userId,LocalDateTime.of(now.toLocalDate(), LocalTime.MIN),now));
        staffDetailsVo.setUserCurrentRegionVo(userCurrentRegion(userId));
        staffDetailsVo.setSystemAlarms(systemAlarmService.find(userId,false,LocalDateTime.of(now.toLocalDate(), LocalTime.MIN),now));
        return staffDetailsVo;
    }

    @Override
    public UserCurrentRegionVo userCurrentRegion(Long userId) {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+userId);
        if (Objects.isNull(userCurrentRegionDto)) {
            CurrentPosition currentPosition = currentPositionService.find(userId);
            if (Objects.nonNull(currentPosition)){
                userCurrentRegionDto = new UserCurrentRegionDto();
                userCurrentRegionDto.setUserId(userId);
                userCurrentRegionDto.setRegionId(currentPosition.getRi());
            }
        }
        if (Objects.nonNull(userCurrentRegionDto)){
            UserCurrentRegionVo vo = new UserCurrentRegionVo();
            vo.setFirstEntryTime(userCurrentRegionDto.getFirstEntryTime());
            Region region = redisUtil.getRegionById(userCurrentRegionDto.getRegionId());
            if (Objects.nonNull(region)) {
                vo.setRegionId(region.getId());
                vo.setRegionName(region.getName());
                Build build = redisUtil.getBuild(region.getBuildId());
                if (Objects.nonNull(build)) {
                    vo.setBuildId(build.getId());
                    vo.setBuildName(build.getName());
                }
                BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    vo.setBuildFloorId(buildFloor.getId());
                    vo.setBuildFloorName(buildFloor.getName());
                }
                Department department = redisUtil.getDepartment(region.getDepartmentId());
                if (Objects.nonNull(department)) {
                    vo.setDepartmentId(department.getId());
                    vo.setDepartmentName(department.getName());
                }
            }
            return vo;
        }
        return null;
    }
}
