package com.lion.event.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.lion.common.constants.RedisConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.enums.Type;
import com.lion.common.expose.file.FileExposeService;
import com.lion.common.utils.RedisUtil;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.device.DeviceGroupExposeService;
import com.lion.device.expose.fault.FaultExposeService;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.event.dao.CurrentPositionDao;
import com.lion.event.dao.HumitureRecordDao;
import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.HumitureRecord;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.UserTagButtonRecord;
import com.lion.event.entity.vo.*;
import com.lion.event.service.*;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.assets.AssetsFaultExposeService;
import com.lion.manage.expose.assets.AssetsTypeExposeService;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.region.RegionCctvExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientReport;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.PatientReportExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.expose.role.RoleExposeService;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.upms.expose.user.UserTypeExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private HumitureRecordDao humitureRecordDao;

    @Autowired
    private PositionService positionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private FaultExposeService faultExposeService;

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @DubboReference
    private WardRoomSickbedExposeService wardRoomSickbedExposeService;

//    @DubboReference
//    private PatientDoctorExposeService patientDoctorExposeService;
//
//    @DubboReference
//    private PatientNurseExposeService patientNurseExposeService;

    @DubboReference
    private DeviceGroupExposeService deviceGroupExposeService;

    @DubboReference
    private DeviceGroupDeviceExposeService deviceGroupDeviceExposeService;

    @Autowired
    private UserTagButtonRecordService userTagButtonRecordService;

    @Autowired
    private ObjectMapper objectMapper;

//    @DubboReference
//    private RestrictedAreaExposeServiceService restrictedAreaExposeServiceService;

    @DubboReference
    private PatientReportExposeService patientReportExposeService;

    @DubboReference
    private AssetsFaultExposeService assetsFaultExposeService;

    @DubboReference
    private UserTypeExposeService userTypeExposeService;

    @DubboReference
    private RegionCctvExposeService regionCctvExposeService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private CurrentPositionDao currentPositionDao;

    @DubboReference
    private AssetsTypeExposeService assetsTypeExposeService;

    @DubboReference
    private RoleExposeService roleExposeService;


    private final String FONT = "simsun.ttc";

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

//        List<Map<String, Object>> assestCount = assetsExposeService.count(buildFloorId);
//        for (Map m :assestCount){
//            if (map.containsKey(m.get("region_id")) ){
//                RegionStatisticsDetails regionStatisticsDetails = map.get(m.get("region_id"));
//                regionStatisticsDetails.setAssetsCount(((Long) m.get("count")).intValue());
//            }
//        }

        List<RegionStatisticsDetails> returnList = new ArrayList<>();
        map.forEach((key,value) ->{
            returnList.add(value);
        });
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        returnList.forEach(regionStatisticsDetails -> {
            Long regionId = regionStatisticsDetails.getRegionId();
            regionStatisticsDetails.setCctvCount(regionCctvExposeService.count(regionId));
            regionStatisticsDetails.setTodayAssetsCount(positionService.count(Type.ASSET,regionId,buildFloorId , startDateTime, endDateTime));
            regionStatisticsDetails.setTodayMigrantCount(positionService.count(Type.MIGRANT,regionId,buildFloorId , startDateTime, endDateTime));
            regionStatisticsDetails.setTodayStaffCount(positionService.count(Type.STAFF,regionId, buildFloorId, startDateTime, endDateTime));
            regionStatisticsDetails.setTodayPatientCount(positionService.count(Type.PATIENT,regionId,buildFloorId , startDateTime, endDateTime));
        });
        return returnList;
    }

    @Override
    public DepartmentStatisticsDetailsVo departmentStatisticsDetails(Long departmentId) {
//        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
//        List<DepartmentStatisticsDetailsVo> returnList = new ArrayList<>();
//        list.forEach(id -> {
        DepartmentStatisticsDetailsVo departmentStatisticsDetailsVo = new DepartmentStatisticsDetailsVo();
        com.lion.core.Optional<Department> optional = departmentExposeService.findById(departmentId);
        if (!optional.isPresent()) {
            return departmentStatisticsDetailsVo;
        }
        Department department = optional.get();
        if (Objects.nonNull(department)) {
//                departmentStatisticsDetailsVo.setDepartmentId(departmentId);
            departmentStatisticsDetailsVo.setDepartmentName(department.getName());
            List<Region> regionList = regionExposeService.findByDepartmentId(departmentId);
            List<Long> deviceGroupIds = new ArrayList<>();
//                regionList.forEach(region -> {
//                    if (Objects.nonNull(region.getDeviceGroupId())) {
//                        deviceGroupIds.add(region.getDeviceGroupId());
//                    }
//                });
            departmentStatisticsDetailsVo.setLowPowerDeviceCount(deviceExposeService.countDevice(departmentId));
            departmentStatisticsDetailsVo.setLowPowerTagCount(tagExposeService.countTag(departmentId, 2));
            departmentStatisticsDetailsVo.setLowPowerTagCount(departmentStatisticsDetailsVo.getLowPowerTagCount() + tagExposeService.countTag(departmentId, 1));
            Map<String, Integer> map = systemAlarmService.groupCount(departmentId);
            if (map.containsKey("allAlarmCount")) {
                departmentStatisticsDetailsVo.setAllAlarmCount(map.get("allAlarmCount"));
            }
            if (map.containsKey("unalarmCount")) {
                departmentStatisticsDetailsVo.setUnalarmCount(map.get("unalarmCount"));
            }
            if (map.containsKey("alarmCount")) {
                departmentStatisticsDetailsVo.setAlarmCount(map.get("alarmCount"));
            }
            departmentStatisticsDetailsVo.setOnlineStaffCount(departmentUserExposeService.count(departmentId,null,null));
            departmentStatisticsDetailsVo.setAssetsCount(assetsExposeService.countByDepartmentId(departmentId, null, null));
            departmentStatisticsDetailsVo.setTagCount(tagExposeService.countTag(departmentId));
            departmentStatisticsDetailsVo.setCctvCount(cctvExposeService.count(departmentId));
            departmentStatisticsDetailsVo.setPatientCount(patientExposeService.count(departmentId,null,null));
            departmentStatisticsDetailsVo.setPatientAlarmCount(patientExposeService.count(departmentId,State.ALARM,null));
            departmentStatisticsDetailsVo.setFaultCount(assetsExposeService.countFault(departmentId));
//                returnList.add(departmentStatisticsDetailsVo);
        }
//        });
//        DepartmentStatisticsDetailsVo returnVo = new DepartmentStatisticsDetailsVo();
//        returnList.forEach(o->{
//            returnVo.setDepartmentName((Objects.isNull(returnVo.getDepartmentName())?"":returnVo.getDepartmentName()+"/")+o.getDepartmentName());
//            returnVo.setAssetsCount(returnVo.getAssetsCount()+o.getAssetsCount());
//            returnVo.setAlarmCount(returnVo.getAlarmCount()+o.getAlarmCount());
//            returnVo.setAllAlarmCount(returnVo.getAllAlarmCount()+o.getAllAlarmCount());
//            returnVo.setCctvCount(returnVo.getCctvCount()+o.getCctvCount());
//            returnVo.setFaultCount(returnVo.getFaultCount()+o.getFaultCount());
//            returnVo.setPatientCount(returnVo.getPatientCount()+o.getPatientCount());
//            returnVo.setLowPowerDeviceCount(returnVo.getLowPowerDeviceCount()+o.getLowPowerDeviceCount());
//            returnVo.setLowPowerTagCount(returnVo.getLowPowerTagCount()+o.getLowPowerTagCount());
//            returnVo.setTagCount(returnVo.getTagCount()+o.getTagCount());
//            returnVo.setUnalarmCount(returnVo.getUnalarmCount()+o.getUnalarmCount());
//            returnVo.setCctvAlarmCount(returnVo.getCctvAlarmCount()+o.getCctvAlarmCount());
//            returnVo.setOnlineStaffCount(returnVo.getOnlineStaffCount()+o.getOnlineStaffCount());
//            returnVo.setPatientAlarmCount(returnVo.getPatientAlarmCount()+o.getPatientAlarmCount());
//        });
        return departmentStatisticsDetailsVo;
    }

    @Override
    public RegionStatisticsDetailsVo regionStatisticsDetails1(Long regionId) {
        com.lion.core.Optional<Region> optional = regionExposeService.findById(regionId);
        if (!optional.isPresent()) {
            return null;
        }
        List<RegionStatisticsDetails> list = regionStatisticsDetails(optional.get().getBuildFloorId());
        RegionStatisticsDetailsVo vo = new RegionStatisticsDetailsVo();
        list.forEach(regionStatisticsDetails -> {
            if (Objects.equals(regionStatisticsDetails.getRegionId(),regionId)) {
                BeanUtils.copyProperties(regionStatisticsDetails,vo);
            }
        });
        vo.setCctvCount(regionCctvExposeService.count(regionId));
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        vo.setTodayAssetsCount(positionService.count(Type.ASSET,regionId, null, startDateTime, endDateTime));
        vo.setTodayMigrantCount(positionService.count(Type.MIGRANT,regionId,null , startDateTime, endDateTime));
        vo.setTodayStaffCount(positionService.count(Type.STAFF,regionId,null , startDateTime, endDateTime));
        vo.setTodayPatientCount(positionService.count(Type.PATIENT,regionId,null , startDateTime, endDateTime));
        return vo;
    }

    @Override
    public DepartmentStaffStatisticsDetailsVo departmentStaffStatisticsDetails(Boolean isAll, String name, Long regionId, Long departmentId) {
        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
        DepartmentStaffStatisticsDetailsVo departmentStaffStatisticsDetailsVo = new DepartmentStaffStatisticsDetailsVo();
        List<DepartmentStaffStatisticsDetailsVo.DepartmentVo> departmentVos = new ArrayList<>();

        List<Region> regionList = new ArrayList<>();
        list.forEach(id -> {
            regionList.addAll(regionExposeService.findByDepartmentId(id));
        });
        List<Long> listIds = new ArrayList<>();
        if (Objects.nonNull(regionId)) {
            listIds = this.find(Type.STAFF, regionId);
        }else {
            for (Region region : regionList) {
                listIds.addAll(this.find(Type.STAFF, region.getId()));
            }
        }

        List<Long> finalListIds = listIds;
        list.forEach(id -> {
            com.lion.core.Optional<Department> optional = departmentExposeService.findById(id);
            if (!optional.isPresent()){
                return;
            }
            Department department = optional.get();
            DepartmentStaffStatisticsDetailsVo.DepartmentVo vo = new DepartmentStaffStatisticsDetailsVo.DepartmentVo();
            vo.setDepartmentName(department.getName());
            vo.setDepartmentId(department.getId());
            departmentStaffStatisticsDetailsVo.setStaffCount(departmentStaffStatisticsDetailsVo.getStaffCount() + departmentUserExposeService.count(department.getId(),null, finalListIds));
            departmentStaffStatisticsDetailsVo.setNormalStaffCount(departmentStaffStatisticsDetailsVo.getNormalStaffCount() + departmentUserExposeService.count(department.getId(), com.lion.upms.entity.enums.State.NORMAL, finalListIds));
            departmentStaffStatisticsDetailsVo.setAbnormalStaffCount(departmentStaffStatisticsDetailsVo.getAbnormalStaffCount() + departmentUserExposeService.count(department.getId(), com.lion.upms.entity.enums.State.ALARM, finalListIds));
            List<Long> userIds = departmentUserExposeService.findAllUser(department.getId(),name, (Objects.equals(true,isAll)) ?null:finalListIds);
            List<DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo> listStaff = new ArrayList<>();
            userIds.forEach(userId->{
                com.lion.core.Optional<User> optionalUser = userExposeService.findById(userId);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo staff = new DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo();
                    staff.setUserId(user.getId());
                    staff.setUserName(user.getName());
                    List<SystemAlarm> list1 = systemAlarmService.find(user.getId(),false,LocalDateTime.now().minusDays(3),LocalDateTime.now());
                    if (list1.size()>0) {
                        userExposeService.updateState(user.getId(),com.lion.upms.entity.enums.State.ALARM.getKey());
                        staff.setDeviceState(com.lion.upms.entity.enums.State.ALARM);
                    }else {
                        userExposeService.updateState(user.getId(),com.lion.upms.entity.enums.State.NORMAL.getKey());
                        staff.setDeviceState(com.lion.upms.entity.enums.State.NORMAL);
                    }
                    staff.setIsInRegion(finalListIds.contains(user.getId()));
                    com.lion.core.Optional<UserType> optionalUserType = userTypeExposeService.findById(user.getUserTypeId());
                    staff.setUserType(optionalUserType.isPresent()?optionalUserType.get():null);
                    staff.setHeadPortrait(user.getHeadPortrait());
                    staff.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                    staff.setNumber(user.getNumber());
                    TagUser tagUser = tagUserExposeService.findByUserId(user.getId());
                    if (Objects.nonNull(tagUser)) {
                        com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(tagUser.getTagId());
                        if (optionalTag.isPresent()){
                            Tag tag = optionalTag.get();
                            staff.setBattery(tag.getBattery());
                            staff.setTagCode(tag.getTagCode());
                        }
                    }
                    UserTagButtonRecord userTagButtonRecord = userTagButtonRecordService.findLsat(user.getId());
                    if (Objects.nonNull(userTagButtonRecord)) {
                        staff.setTagRuleEffect(userTagButtonRecord.getBn());
                        staff.setTagRuleEffectDateTime(userTagButtonRecord.getDdt());
                        staff.setButtonId(userTagButtonRecord.getBi());
                    }
                    listStaff.add(staff);
                }
            });
            Collections.sort(listStaff);
            vo.setDepartmentStaffVos(listStaff);
            departmentVos.add(vo);
        });
        departmentStaffStatisticsDetailsVo.setDepartmentVos(departmentVos);
        return departmentStaffStatisticsDetailsVo;
    }

    @Override
    public DepartmentAssetsStatisticsDetailsVo departmentAssetsStatisticsDetails(String keyword, Long regionId, Long departmentId) {
        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
        DepartmentAssetsStatisticsDetailsVo departmentAssetsStatisticsDetailsVo =new DepartmentAssetsStatisticsDetailsVo();
        List<DepartmentAssetsStatisticsDetailsVo.AssetsDepartmentVo> assetsDepartmentVos = new ArrayList<>();
        departmentAssetsStatisticsDetailsVo.setAssetsDepartmentVos(assetsDepartmentVos);
        List<Long> listIds = new ArrayList<>();
        if (Objects.nonNull(regionId)) {
            listIds = this.find(Type.ASSET, regionId);
        }
        List<Long> finalListIds = listIds;
        list.forEach(id -> {
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(id);
            if (optionalDepartment.isPresent()) {
                Department department = optionalDepartment.get();
                DepartmentAssetsStatisticsDetailsVo.AssetsDepartmentVo vo = new DepartmentAssetsStatisticsDetailsVo.AssetsDepartmentVo();
                vo.setDepartmentName(department.getName());
                vo.setDepartmentId(department.getId());
                departmentAssetsStatisticsDetailsVo.setAssetsCount(departmentAssetsStatisticsDetailsVo.getAssetsCount() + assetsExposeService.countByDepartmentId(department.getId(), com.lion.manage.entity.enums.State.USED,finalListIds ));
                departmentAssetsStatisticsDetailsVo.setNormalAssetsCount(departmentAssetsStatisticsDetailsVo.getNormalAssetsCount() + assetsExposeService.count(department.getId(), com.lion.manage.entity.enums.State.USED, false,false, finalListIds));
//                departmentAssetsStatisticsDetailsVo.setAbnormalAssetsCount(departmentAssetsStatisticsDetailsVo.getAbnormalAssetsCount() + assetsExposeService.countByDepartmentId(department.getId(), com.lion.manage.entity.enums.State.ALARM, finalListIds));
                List<Assets> assets = assetsExposeService.findByDepartmentId(department.getId(), keyword, keyword, finalListIds);
                List<DepartmentAssetsStatisticsDetailsVo.AssetsVo> assetsVos = new ArrayList<>();
                assets.forEach(a -> {
                    DepartmentAssetsStatisticsDetailsVo.AssetsVo assetsVo = new DepartmentAssetsStatisticsDetailsVo.AssetsVo();
                    BeanUtils.copyProperties(a, assetsVo);
                    Integer i = assetsFaultExposeService.countNotFinish(a.getId());
                    if (i>0){
                        assetsExposeService.updateState(a.getId(),2);
                    }
                    assetsVo.setIsFault(i>0);
                    TagAssets tagAssets = tagAssetsExposeService.find(a.getId());
                    if (Objects.nonNull(tagAssets)) {
                        com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(tagAssets.getTagId());
                        if (optionalTag.isPresent()) {
                            Tag tag = optionalTag.get();
                            assetsVo.setBattery(tag.getBattery());
                            assetsVo.setTagCode(tag.getTagCode());
                            assetsVo.setDeviceName(tag.getDeviceName());
                            assetsVo.setDeviceCode(tag.getDeviceCode());
                            Optional<AssetsType> optional = assetsTypeExposeService.findById(a.getAssetsTypeId());
                            if (optional.isPresent()) {
                                assetsVo.setAssetsTypeName(optional.get().getAssetsTypeName());
                            }
//                            assetsVo.setTagType(tag.getType());
                            assetsVo.setTagPurpose(tag.getPurpose());
                        }
                    }
                    assetsVos.add(assetsVo);
                });
                vo.setAssetsVos(assetsVos);
                assetsDepartmentVos.add(vo);
            }
        });
        return departmentAssetsStatisticsDetailsVo;
    }
//    http://219.131.241.227:9503/api/event-data-restful/department/tag/statistics/details?departmentId=992390272140705792&pageSize=10&pageNumber=1&keyword=
//    http://219.131.241.227:9503/api/event-data-restful/department/device/statistics/details?regionId=993492491678253056&pageSize=10&pageNumber=1&keyword=
    @Override
    public DepartmentTagStatisticsDetailsVo departmentTagStatisticsDetails(String keyword, Long regionId, Long departmentId) {
        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
        DepartmentTagStatisticsDetailsVo departmentTagStatisticsDetailsVo = new DepartmentTagStatisticsDetailsVo();
        List<DepartmentTagStatisticsDetailsVo.TagDepartmentVo> tagDepartmentVos = new ArrayList<>();
        departmentTagStatisticsDetailsVo.setTagDepartmentVos(tagDepartmentVos);
        List<Long> listIds = new ArrayList<>();
        if (Objects.nonNull(regionId)) {
            listIds = this.find(Type.HUMIDITY, regionId);
            listIds = this.find(Type.TEMPERATURE, regionId);
        }
        List<Long> finalListIds = listIds;
        list.forEach(id -> {
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(id);
            if (optionalDepartment.isPresent()) {
                Department department = optionalDepartment.get();
                DepartmentTagStatisticsDetailsVo.TagDepartmentVo tagDepartmentVo = new DepartmentTagStatisticsDetailsVo.TagDepartmentVo();
                tagDepartmentVo.setDepartmentName(department.getName());
                tagDepartmentVo.setDepartmentId(department.getId());
                departmentTagStatisticsDetailsVo.setTagCount(departmentTagStatisticsDetailsVo.getTagCount() + tagExposeService.countTag(department.getId(), TagPurpose.THERMOHYGROGRAPH, com.lion.device.entity.enums.State.USED, null));
                departmentTagStatisticsDetailsVo.setNormalTagCount(departmentTagStatisticsDetailsVo.getNormalTagCount() + tagExposeService.countTag(department.getId(), TagPurpose.THERMOHYGROGRAPH, com.lion.device.entity.enums.State.USED,false ));
                departmentTagStatisticsDetailsVo.setAbnormalTagCount(departmentTagStatisticsDetailsVo.getAbnormalTagCount() + tagExposeService.countTag(department.getId(), TagPurpose.THERMOHYGROGRAPH, com.lion.device.entity.enums.State.USED, true));

                List<Tag> tagList = tagExposeService.find(department.getId(), TagPurpose.THERMOHYGROGRAPH, keyword, finalListIds);
                List<DepartmentTagStatisticsDetailsVo.TagVo> tagVos = new ArrayList<>();
                tagList.forEach(tag -> {
                    DepartmentTagStatisticsDetailsVo.TagVo vo = new DepartmentTagStatisticsDetailsVo.TagVo();
                    BeanUtils.copyProperties(tag, vo);
                    HumitureRecord record = humitureRecordDao.find(tag.getId(),false);
                    if (Objects.nonNull(record)) {
                        vo.setTemperature(record.getT());
                        vo.setHumidity(record.getH());
                        vo.setDataDateTime(record.getDdt());
                    }
                    HumitureRecord previousRecord = humitureRecordDao.find(tag.getId(),true);
                    if (Objects.nonNull(previousRecord)) {
                        vo.setPreviousTemperature(previousRecord.getT());
                        vo.setPreviousHumidity(previousRecord.getH());
                        vo.setPreviousDataDateTime(previousRecord.getDdt());
                    }
                    tagVos.add(vo);
                });
                tagDepartmentVo.setTagVos(tagVos);
                tagDepartmentVos.add(tagDepartmentVo);
            }
        });
        return departmentTagStatisticsDetailsVo;
    }

    @Override
    public DepartmentPatientStatisticsDetailsVo departmentPatientStatisticsDetails(String name, Long regionId, Long departmentId) {
        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
        DepartmentPatientStatisticsDetailsVo departmentPatientStatisticsDetailsVo = new DepartmentPatientStatisticsDetailsVo();
        List<DepartmentPatientStatisticsDetailsVo.PatientDepartmentVo> patientDepartmentVos = new ArrayList<>();
        departmentPatientStatisticsDetailsVo.setPatientDepartmentVos(patientDepartmentVos);
        List<Long> listIds = new ArrayList<>();
        if (Objects.nonNull(regionId)) {
            listIds = this.find(Type.PATIENT, regionId);
        }
        List<Long> finalListIds = listIds;
        list.forEach(id -> {
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(id);
            if (optionalDepartment.isPresent()) {
                Department department = optionalDepartment.get();
                DepartmentPatientStatisticsDetailsVo.PatientDepartmentVo patientDepartmentVo = new DepartmentPatientStatisticsDetailsVo.PatientDepartmentVo();
                patientDepartmentVo.setDepartmentName(department.getName());
                patientDepartmentVo.setDepartmentId(department.getId());
                departmentPatientStatisticsDetailsVo.setPatientCount(departmentPatientStatisticsDetailsVo.getPatientCount() + patientExposeService.count(department.getId(), null,finalListIds ));
                departmentPatientStatisticsDetailsVo.setNormalPatientCount(departmentPatientStatisticsDetailsVo.getNormalPatientCount() + patientExposeService.count(department.getId(), State.NORMAL, finalListIds));
                departmentPatientStatisticsDetailsVo.setAbnormalPatientCount(departmentPatientStatisticsDetailsVo.getAbnormalPatientCount() + patientExposeService.count(department.getId(), State.ALARM,finalListIds ));
                List<DepartmentPatientStatisticsDetailsVo.PatientVo> patientVos = new ArrayList<>();
                List<Patient> patientList = patientExposeService.find(department.getId(), name, finalListIds);
                patientList.forEach(patient -> {
                    DepartmentPatientStatisticsDetailsVo.PatientVo vo = new DepartmentPatientStatisticsDetailsVo.PatientVo();
                    Tag tag = tagExposeService.find(patient.getTagCode());
                    if (Objects.nonNull(tag)) {
                        vo.setBattery(tag.getBattery());
                    }
                    com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
                    if (optionalWardRoomSickbed.isPresent()) {
                        vo.setBedCode(optionalWardRoomSickbed.get().getBedCode());
                    }
                    vo.setId(patient.getId());
                    vo.setName(patient.getName());
                    List<SystemAlarm> list1 = systemAlarmService.find(patient.getId(),false,LocalDateTime.now().minusDays(3),LocalDateTime.now());
                    if (list1.size()>0) {
                        patientExposeService.updateState(patient.getId(),State.ALARM.getKey());
                        vo.setDeviceState(State.ALARM);
                    }else {
                        patientExposeService.updateState(patient.getId(),State.NORMAL.getKey());
                        vo.setDeviceState(State.NORMAL);
                    }
                    vo.setGender(patient.getGender());
//                    vo.setPatientState(patient.getPatientState());
                    vo.setTagCode(patient.getTagCode());
                    vo.setHeadPortrait(patient.getHeadPortrait());
                    vo.setHeadPortraitUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
                    patientVos.add(vo);
                });
                patientDepartmentVo.setPatientVos(patientVos);
                patientDepartmentVos.add(patientDepartmentVo);
            }
        });
        return departmentPatientStatisticsDetailsVo;
    }

    @Override
    public DepartmentTemporaryPersonStatisticsDetailsVo departmentTemporaryPersonStatisticsDetails(String name, Long regionId, Long departmentId) {
        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
        DepartmentTemporaryPersonStatisticsDetailsVo departmentTemporaryPersonStatisticsDetailsVo = new DepartmentTemporaryPersonStatisticsDetailsVo();
        List<DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonDepartmentVo> temporaryPersonDepartmentVos = new ArrayList<>();
        List<Long> listIds = new ArrayList<>();
        if (Objects.nonNull(regionId)) {
            listIds = this.find(Type.MIGRANT, regionId);
        }
        List<Long> finalListIds = listIds;
        list.forEach(id -> {
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(id);
            if (optionalDepartment.isPresent()) {
                Department department = optionalDepartment.get();
                DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonDepartmentVo temporaryPersonDepartmentVo = new DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonDepartmentVo();
                temporaryPersonDepartmentVo.setDepartmentName(department.getName());
                temporaryPersonDepartmentVo.setDepartmentId(department.getId());
                departmentTemporaryPersonStatisticsDetailsVo.setTemporaryPersonCount(departmentTemporaryPersonStatisticsDetailsVo.getTemporaryPersonCount() + temporaryPersonExposeService.count(department.getId(), null,finalListIds ));
                departmentTemporaryPersonStatisticsDetailsVo.setNormalTemporaryPersonCount(departmentTemporaryPersonStatisticsDetailsVo.getNormalTemporaryPersonCount() + temporaryPersonExposeService.count(department.getId(), State.NORMAL, finalListIds));
                departmentTemporaryPersonStatisticsDetailsVo.setAbnormalTemporaryPersonCount(departmentTemporaryPersonStatisticsDetailsVo.getAbnormalTemporaryPersonCount() + temporaryPersonExposeService.count(department.getId(), State.ALARM, finalListIds));
                List<DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo> temporaryPersonVos = new ArrayList<>();
                List<TemporaryPerson> temporaryPersonList = temporaryPersonExposeService.find(department.getId(), name, finalListIds);
                temporaryPersonList.forEach(temporaryPerson -> {
                    DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo vo = new DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo();
                    Tag tag = tagExposeService.find(temporaryPerson.getTagCode());
                    if (Objects.nonNull(tag)) {
                        vo.setBattery(tag.getBattery());
                    }
                    vo.setName(temporaryPerson.getName());
                    List<SystemAlarm> list1 = systemAlarmService.find(temporaryPerson.getId(),false,LocalDateTime.now().minusDays(3),LocalDateTime.now());
                    if (list1.size()>0) {
                        temporaryPersonExposeService.updateState(temporaryPerson.getId(),State.ALARM.getKey());
                        vo.setDeviceState(State.ALARM);
                    }else {
                        temporaryPersonExposeService.updateState(temporaryPerson.getId(),State.NORMAL.getKey());
                        vo.setDeviceState(State.NORMAL);
                    }
                    vo.setTagCode(temporaryPerson.getTagCode());
                    vo.setId(temporaryPerson.getId());
                    vo.setHeadPortrait(temporaryPerson.getHeadPortrait());
                    vo.setHeadPortraitUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
                    temporaryPersonVos.add(vo);
                });
                temporaryPersonDepartmentVo.setTemporaryPersonVos(temporaryPersonVos);
                temporaryPersonDepartmentVos.add(temporaryPersonDepartmentVo);
            }
        });
        departmentTemporaryPersonStatisticsDetailsVo.setTemporaryPersonDepartmentVos(temporaryPersonDepartmentVos);
        return departmentTemporaryPersonStatisticsDetailsVo;
    }

    @Override
    public DepartmentDeviceStatisticsDetailsVo departmentDeviceStatisticsDetails(String keyword, Long regionId, Long departmentId) {
        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
        DepartmentDeviceStatisticsDetailsVo departmentDeviceStatisticsDetailsVo = new DepartmentDeviceStatisticsDetailsVo();
        List<DepartmentDeviceStatisticsDetailsVo.DepartmentDeviceDetailsVo> departmentDeviceDetailsVos = new ArrayList<DepartmentDeviceStatisticsDetailsVo.DepartmentDeviceDetailsVo>();
//        List<DepartmentDeviceGroupStatisticsDetailsVo.DeviceGroupDetailsVo> deviceGroupDetailsVos = new ArrayList<>();
        list.forEach(id->{
//            departmentDeviceGroupStatisticsDetailsVo.setDeviceGroupCount(departmentDeviceGroupStatisticsDetailsVo.getDeviceGroupCount() + deviceGroupExposeService.count(id));
            departmentDeviceStatisticsDetailsVo.setNormalDeviceCount(departmentDeviceStatisticsDetailsVo.getNormalDeviceCount() + deviceExposeService.count(id, Arrays.asList(new com.lion.device.entity.enums.State[]{com.lion.device.entity.enums.State.USED}), false));
            departmentDeviceStatisticsDetailsVo.setAbnormalDeviceCount(departmentDeviceStatisticsDetailsVo.getAbnormalDeviceCount() + deviceExposeService.count(id, Arrays.asList(new com.lion.device.entity.enums.State[]{com.lion.device.entity.enums.State.USED,}), true));
            DepartmentDeviceStatisticsDetailsVo.DepartmentDeviceDetailsVo deviceDetailsVo = new DepartmentDeviceStatisticsDetailsVo.DepartmentDeviceDetailsVo();
            deviceDetailsVo.setDepartmentId(id);
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(id);
            deviceDetailsVo.setDepartmentName(optionalDepartment.isPresent()?optionalDepartment.get().getName():"");
            List<Device> devices = deviceExposeService.findByDepartmentId(id, keyword);
            List<DetailsDeviceVo> detailsDeviceVos =  new ArrayList<DetailsDeviceVo>();
            devices.forEach(device -> {
                DetailsDeviceVo detailsDeviceVo = deviceExposeService.details(device.getId());
                detailsDeviceVos.add(detailsDeviceVo);
            });
            deviceDetailsVo.setDetailsDeviceVos(detailsDeviceVos);
            deviceDetailsVo.setCctvs(cctvExposeService.findDepartmentId(departmentId,keyword ));
            departmentDeviceDetailsVos.add(deviceDetailsVo);

//            List<Region> regionList = regionExposeService.findByDepartmentId(id);
//            regionList.forEach(region->{
//                DepartmentDeviceGroupStatisticsDetailsVo.DeviceGroupDetailsVo deviceGroupDetailsVo = new DepartmentDeviceGroupStatisticsDetailsVo.DeviceGroupDetailsVo();
//                DeviceGroup deviceGroup = deviceGroupExposeService.findById(region.getDeviceGroupId());
//                if (Objects.nonNull(deviceGroup)) {
//                    deviceGroupDetailsVo.setName(deviceGroup.getName());
//                    List<DetailsDeviceVo> detailsDeviceVos = new ArrayList<>();
//                    List<DeviceGroupDevice> deviceGroupDevices = deviceGroupDeviceExposeService.find(deviceGroup.getId());
//                    deviceGroupDevices.forEach(deviceGroupDevice -> {
//                        Device device = deviceExposeService.findById(deviceGroupDevice.getDeviceId());
//                        if (Objects.nonNull(device)) {
//                            DetailsDeviceVo detailsDeviceVo = new DetailsDeviceVo();
//                            BeanUtils.copyProperties(device,detailsDeviceVo);
//                            detailsDeviceVo.setImgUrl(fileExposeService.getUrl(device.getImg()));
//                            detailsDeviceVos.add(detailsDeviceVo);
//                        }
//                    });
//                    deviceGroupDetailsVo.setDetailsDeviceVos(detailsDeviceVos);
//                    deviceGroupDetailsVos.add(deviceGroupDetailsVo);
//                }
//            });
        });
//        departmentDeviceGroupStatisticsDetailsVo.setDeviceGroupDetailsVos(deviceGroupDetailsVos);
        departmentDeviceStatisticsDetailsVo.setDepartmentDeviceDetailsVos(departmentDeviceDetailsVos);
        return departmentDeviceStatisticsDetailsVo;
    }

    @Override
    public StaffDetailsVo staffDetails(Long userId) {
        com.lion.core.Optional<User> optionalUser = userExposeService.findById(userId);
        if (!optionalUser.isPresent()){
            return null;
        }
        User user = optionalUser.get();
        StaffDetailsVo staffDetailsVo = new StaffDetailsVo();
        BeanUtils.copyProperties(user,staffDetailsVo);
        TagUser tagUser = tagUserExposeService.findByUserId(userId);
        if (Objects.nonNull(tagUser)){
            com.lion.core.Optional<Tag>  optionalTag = tagExposeService.findById(tagUser.getTagId());
            if (optionalTag.isPresent()){
                staffDetailsVo.setBattery(optionalTag.get().getBattery());
            }
        }
        SystemAlarm systemAlarm =  systemAlarmService.findLast(userId);
        if (Objects.nonNull(systemAlarm)) {
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            staffDetailsVo.setAlarm(systemAlarmType.getDesc());
            staffDetailsVo.setAlarmType(systemAlarmType);
            staffDetailsVo.setAlarmDataTime(systemAlarm.getDt());
            staffDetailsVo.setAlarmId(systemAlarm.get_id());
        }
        Department department = departmentUserExposeService.findDepartment(userId);
        if (Objects.nonNull(department)){
            staffDetailsVo.setDepartmentId(department.getId());
            staffDetailsVo.setDepartmentName(department.getName());
        }
        List<Long> departmentResponsibleList = departmentExposeService.responsibleDepartment(null);
        List<StaffDetailsVo.DepartmentResponsibleVo> departmentResponsibleVos = new ArrayList<>();
        departmentResponsibleList.forEach(id -> {
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(id);
            if (optionalDepartment.isPresent()) {
                Department department1 = optionalDepartment.get();
                StaffDetailsVo.DepartmentResponsibleVo vo = new StaffDetailsVo.DepartmentResponsibleVo();
                vo.setDepartmentId(department1.getId());
                vo.setDepartmentName(department1.getName());
                departmentResponsibleVos.add(vo);
            }
        });
        staffDetailsVo.setDepartmentResponsibleVos(departmentResponsibleVos);
        LocalDateTime now = LocalDateTime.now();
//        staffDetailsVo.setPositions(positionService.findUserId(userId,LocalDateTime.of(now.toLocalDate(), LocalTime.MIN),now));
        staffDetailsVo.setCurrentRegionVo(userCurrentRegion(userId));
//        staffDetailsVo.setSystemAlarms(systemAlarmService.find(userId,false,LocalDateTime.of(now.toLocalDate(), LocalTime.MIN),now));
        return staffDetailsVo;
    }

    @Override
    public CurrentRegionVo userCurrentRegion(Long userId) {
        CurrentPosition currentPosition = currentPositionService.find(userId);
        if (Objects.nonNull(currentPosition)){
            CurrentRegionVo vo = convertVo(currentPosition);
            vo.setFirstEntryTime(currentPosition.getDdt());
            return vo;
        }
        return null;
    }

    @Override
    public AssetsDetailsVo assetsDetails(Long assetsId) {
        com.lion.core.Optional<Assets> optionalAssets = assetsExposeService.findById(assetsId);
        if (!optionalAssets.isPresent()){
            return null;
        }
        Assets assets = optionalAssets.get();
        AssetsDetailsVo assetsDetailsVo = new AssetsDetailsVo();
        BeanUtils.copyProperties(assets,assetsDetailsVo);
        com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(assetsDetailsVo.getRegionId());
        if (optionalRegion.isPresent()) {
            Region region = optionalRegion.get();
            assetsDetailsVo.setRegionId(region.getId());
            assetsDetailsVo.setRegionName(region.getName());
            com.lion.core.Optional<Build> optionalBuild = buildExposeService.findById(region.getBuildId());
            if (optionalBuild.isPresent()) {
                Build build = optionalBuild.get();
                assetsDetailsVo.setBuildId(build.getId());
                assetsDetailsVo.setBuildName(build.getName());
            }
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
            if (optionalBuildFloor.isPresent()) {
                BuildFloor buildFloor = optionalBuildFloor.get();
                assetsDetailsVo.setBuildFloorId(buildFloor.getId());
                assetsDetailsVo.setBuildFloorName(buildFloor.getName());
            }
            com.lion.core.Optional<Department> optionalDepartment =  departmentExposeService.findById(region.getDepartmentId());
            if (optionalDepartment.isPresent()) {
                Department department = optionalDepartment.get();
                assetsDetailsVo.setDepartmentId(department.getId());
                assetsDetailsVo.setDepartmentName(department.getName());
            }
        }
        TagAssets tagAssets = tagAssetsExposeService.find(assetsId);
        if (Objects.nonNull(tagAssets)) {
            com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(tagAssets.getTagId());
            if (optionalTag.isPresent()){
                assetsDetailsVo.setBattery(optionalTag.get().getBattery());
            }
        }
        LocalDateTime now = LocalDateTime.now();
        return assetsDetailsVo;
    }

    @Override
    public PatientDetailsVo patientDetails(Long patientId) {
        PatientDetailsVo vo = new PatientDetailsVo();
        com.lion.core.Optional<Patient> optionalPatient = patientExposeService.findById(patientId);
        if (!optionalPatient.isPresent()) {
            return null;
        }
        Patient patient = optionalPatient.get();
        BeanUtils.copyProperties(patient,vo);
        Tag tag = tagExposeService.find(patient.getTagCode());
        if (Objects.nonNull(tag)) {
            vo.setBattery(tag.getBattery());
        }
        SystemAlarm systemAlarm =  systemAlarmService.findLast(patientId);
        if (Objects.nonNull(systemAlarm)) {
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            vo.setAlarm(systemAlarmType.getDesc());
            vo.setAlarmType(systemAlarmType);
            vo.setAlarmDataTime(systemAlarm.getDt());
            vo.setAlarmId(systemAlarm.get_id());
        }
        PatientReport patientReport = patientReportExposeService.findLast(patientId);
        if (Objects.nonNull(patientReport)) {
            vo.setReportContent(patientReport.getContent());
            vo.setReportDataTime(patientReport.getCreateDateTime());
            com.lion.core.Optional<User> optionalUser = userExposeService.findById(patientReport.getReportUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                vo.setReportUserId(user.getId());
                vo.setReportUserName(user.getName());
                vo.setReportUserHeadPortrait(user.getHeadPortrait());
                vo.setReportUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
        }
//        vo.setRestrictedAreaVos(this.restrictedArea(patientId,PersonType.PATIENT));
        com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
        if (optionalWardRoomSickbed.isPresent()) {
            vo.setBedCode(optionalWardRoomSickbed.get().getBedCode());
        }
        CurrentRegionDto currentRegion = (CurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.PATIENT_CURRENT_REGION+patientId);
        vo.setCurrentRegionVo(convertVo(currentRegion));
//        List<PatientNurse> patientNurses = patientNurseExposeService.find(patient.getId());
//        List<PatientDetailsVo.NurseVo> nurseVos = new ArrayList<>();
//        patientNurses.forEach(patientNurse -> {
//            User nurse = userExposeService.findById(patientNurse.getNurseId());
//            PatientDetailsVo.NurseVo nurseVo = new PatientDetailsVo.NurseVo();
//            if (Objects.nonNull(nurse)){
//                nurseVo.setNurseName(nurse.getName());
//                nurseVo.setNurseHeadPortrait(nurse.getHeadPortrait());
//                nurseVo.setNurseHeadPortraitUrl(fileExposeService.getUrl(nurse.getHeadPortrait()));
//                nurseVos.add(nurseVo);
//            }
//        });
//        vo.setNurseVos(nurseVos);
//        List<PatientDoctor> patientDoctors = patientDoctorExposeService.find(patient.getId());
//        List<PatientDetailsVo.DoctorVo> doctorVos = new ArrayList<>();
//        patientDoctors.forEach(patientDoctor -> {
//            User doctor = userExposeService.findById(patientDoctor.getDoctorId());
//            PatientDetailsVo.DoctorVo doctorVo = new PatientDetailsVo.DoctorVo();
//            if (Objects.nonNull(doctor)){
//                doctorVo.setDoctorName(doctor.getName());
//                doctorVo.setDoctorHeadPortrait(doctor.getHeadPortrait());
//                doctorVo.setDoctorHeadPortraitUrl(fileExposeService.getUrl(doctor.getHeadPortrait()));
//                doctorVos.add(doctorVo);
//            }
//        });
//        vo.setDoctorVos(doctorVos);
        return vo;
    }

    @Override
    public TemporaryPersonDetailsVo temporaryPersonDetails(Long temporaryPersonId) {
        com.lion.core.Optional<TemporaryPerson> optionalTemporaryPerson = temporaryPersonExposeService.findById(temporaryPersonId);
        if (optionalTemporaryPerson.isEmpty()) {
            return null;
        }
        TemporaryPerson temporaryPerson = optionalTemporaryPerson.get();
        TemporaryPersonDetailsVo vo = new TemporaryPersonDetailsVo();
        BeanUtils.copyProperties(temporaryPerson,vo);
        Tag tag = tagExposeService.find(temporaryPerson.getTagCode());
        if (Objects.nonNull(tag)) {
            vo.setBattery(tag.getBattery());
        }
        SystemAlarm systemAlarm =  systemAlarmService.findLast(temporaryPersonId);
        if (Objects.nonNull(systemAlarm)) {
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            vo.setAlarm(systemAlarmType.getDesc());
            vo.setAlarmType(systemAlarmType);
            vo.setAlarmDataTime(systemAlarm.getDt());
            vo.setAlarmId(systemAlarm.get_id());
        }
//        vo.setRestrictedAreaVos(this.restrictedArea(temporaryPersonId,PersonType.TEMPORARY_PERSON));
        return vo;
    }

    @Override
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList(Boolean isAll, Boolean isUa, List<Long> ri, Long di, Type alarmType, TagType tagType, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage, Long tagId, Long assetsId, String ids, Long deviceId, String... sorts) {
        List<Long> departmentIds = new ArrayList<>();
        Long userId = CurrentUserUtil.getCurrentUserId();
        Role role = roleExposeService.find(userId);
        if (role.getCode().toLowerCase().indexOf("admin") < 0) {
            Department department = departmentUserExposeService.findDepartment(userId);
            if (Objects.nonNull(department)) {
                departmentIds.add(department.getId());
            }
        } else {
            if (Objects.equals(isAll,false)) {
                departmentIds = departmentExposeService.responsibleDepartment(di);
            }else if (Objects.equals(isAll,true)) {
                if (Objects.nonNull(di)) {
                    departmentIds.add(di);
                }
            }
        }
        if (Objects.isNull(startDateTime) ) {
            startDateTime = LocalDateTime.now().minusDays(3);
        }
        if (Objects.nonNull(endDateTime) && Objects.isNull(startDateTime)) {
            startDateTime = endDateTime.minusDays(3);
        }
        List<Long> tagIds = tagExposeService.find(tagType,tagCode);
        return systemAlarmService.list(lionPage,departmentIds, isUa,ri, alarmType, tagIds, startDateTime, endDateTime, tagId, assetsId,ids , deviceId, sorts);
    }

    @Override
    public List<SystemAlarmGroupVo> systemAlarmGroupList(Boolean isAll, Boolean isUa, List<Long> ri, Long di, Type alarmType, TagType tagType, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage, String... sorts) {
        List<Long> departmentIds = new ArrayList<>();
        if (Objects.equals(isAll,false)) {
            departmentIds = departmentExposeService.responsibleDepartment(di);
        }else if (Objects.equals(isAll,true)) {
            if (Objects.nonNull(di)) {
                departmentIds.add(di);
            }
        }

        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(3);
        }
        if (Objects.nonNull(endDateTime)) {
            startDateTime = endDateTime.minusDays(3);
        }
//        List<Long> tagIds = tagExposeService.find(tagType,tagCode);
//        List<Long> deviceIds = new ArrayList<>();
//        deviceExposeService.findAll().forEach(device -> {
//            deviceIds.add(device.getId());
//        });
        List<org.bson.Document> list = systemAlarmService.listGroup(lionPage,departmentIds,isUa,ri,alarmType,null,null , startDateTime, endDateTime);
        List<SystemAlarmGroupVo> returnList = new ArrayList<>();
        LocalDateTime finalStartDateTime = startDateTime;
        list.forEach(document -> {
            if (document.containsKey("_id") && Objects.nonNull(document.get("_id"))) {
                SystemAlarmGroupVo vo = new SystemAlarmGroupVo();
                Long deviceId = null;
                Long tagId = null;
                Long assetsId = null;
                if (Objects.nonNull(((org.bson.Document) document.get("_id")).get("ti"))) {
                    tagId = Long.valueOf(String.valueOf(((org.bson.Document) document.get("_id")).get("ti")));
                }else if (Objects.nonNull(((org.bson.Document) document.get("_id")).get("ai"))) {
                    assetsId = Long.valueOf(String.valueOf(((org.bson.Document) document.get("_id")).get("ai")));
                }else if (Objects.nonNull(((org.bson.Document) document.get("_id")).get("dvi"))) {
                    deviceId = Long.valueOf(String.valueOf(((org.bson.Document) document.get("_id")).get("dvi")));
                }
                if (Objects.nonNull(assetsId)) {
                    Optional<Assets> assetsOptional = assetsExposeService.findById(assetsId);
                    if (assetsOptional.isPresent()) {
                        Assets assets = assetsOptional.get();
                        vo.setIsAssets(true);
                        vo.setAssetsCode(assets.getCode());
                        vo.setAssetsId(assets.getId());
                        Optional<AssetsType> assetsTypeOptional = assetsTypeExposeService.findById(assets.getAssetsTypeId());
                        if (assetsTypeOptional.isPresent()) {
                            vo.setAssetsType(assetsTypeOptional.get());
                        }
                        vo.setTitle(assets.getName());
                        vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                        vo.setImgId(assets.getImg());
                        vo.setCount(Integer.valueOf(String.valueOf(document.get("count"))));
                        IPageResultData<List<SystemAlarmVo>> listIPageResultData = systemAlarmList(null, false, null, null, null, null, null, finalStartDateTime, null, new LionPage(0, 1), null,assets.getId(), null, null, "dt");
                        List<SystemAlarmVo> list1 = listIPageResultData.getData();
                        if (Objects.nonNull(list1) && list1.size() > 0) {
                            vo.setSystemAlarm(list1.get(0));
                            vo.setDateTime(list1.get(0).getDeviceDateTime());
                        }
                        returnList.add(vo);

                    }
                }
                if (Objects.nonNull(deviceId)) {
                    Optional<Device> deviceOptional = deviceExposeService.findById(deviceId);
                    if (deviceOptional.isPresent()) {
                        Device device = deviceOptional.get();
                        vo.setIsDevice(true);
                        vo.setDeviceId(device.getId());
                        vo.setDeviceCode(device.getCode());
                        vo.setDeviceClassify(device.getDeviceClassify());
                        vo.setDeviceType(device.getDeviceType());
                        vo.setTitle(device.getName());
                        vo.setImgUrl(fileExposeService.getUrl(device.getImg()));
                        vo.setImgId(device.getImg());
                        vo.setCount(Integer.valueOf(String.valueOf(document.get("count"))));
                        IPageResultData<List<SystemAlarmVo>> listIPageResultData = systemAlarmList(null, false, null, null, null, null, null, finalStartDateTime, null, new LionPage(0, 1), null,null,null , device.getId(), "dt");
                        List<SystemAlarmVo> list1 = listIPageResultData.getData();
                        if (Objects.nonNull(list1) && list1.size() > 0) {
                            vo.setSystemAlarm(list1.get(0));
                            vo.setDateTime(list1.get(0).getDeviceDateTime());
                        }
                        returnList.add(vo);

                    }
                }
                if (Objects.nonNull(tagId)) {
                    Optional<Tag> optional = tagExposeService.findById(tagId);
                    if (optional.isPresent()) {
                        Tag tag = optional.get();
                        vo.setIsTag(true);
                        vo.setTagId(tag.getId());
                        vo.setTagCode(tag.getTagCode());
                        User user = redisUtil.getUser(tag.getId());
                        Patient patient = redisUtil.getPatientByTagId(tag.getId());
                        TemporaryPerson temporaryPerson = redisUtil.getTemporaryPersonByTagId(tag.getId());
                        if (Objects.nonNull(user)) {
                            vo.setTitle(user.getName());
                            vo.setImgId(user.getHeadPortrait());
                            vo.setImgUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                        } else if (Objects.nonNull(patient)) {
                            vo.setTitle(patient.getName());
                            vo.setImgId(patient.getHeadPortrait());
                            vo.setImgUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
                        } else if (Objects.nonNull(temporaryPerson)) {
                            vo.setTitle(temporaryPerson.getName());
                            vo.setImgId(temporaryPerson.getHeadPortrait());
                            vo.setImgUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
                        }
                        vo.setTagType(tag.getType());
                        vo.setCount(Integer.valueOf(String.valueOf(document.get("count"))));
                        LionPage lionPage1 = new LionPage(0, 1);
                        IPageResultData<List<SystemAlarmVo>> listIPageResultData = systemAlarmList(null, false, null, null, null, null, null, finalStartDateTime, null, lionPage1, tag.getId(),null,null , null, "dt");
                        List<SystemAlarmVo> list1 = listIPageResultData.getData();
                        if (Objects.nonNull(list1) && list1.size() > 0) {
                            vo.setSystemAlarm(list1.get(0));
                            vo.setDateTime(list1.get(0).getDeviceDateTime());
                        }
                        returnList.add(vo);
                    }
                }
            }
        });
        Collections.sort(returnList,new Comparator<SystemAlarmGroupVo>(){
            @Override
            public int compare(SystemAlarmGroupVo o1, SystemAlarmGroupVo o2) {
                if (Objects.nonNull(o2.getDateTime()) && Objects.nonNull(o1.getDateTime())) {
//                    if (o2.getDateTime().compareTo(o1.getDateTime()) == 0) {
//                        o2.setDateTime(o2.getDateTime().plusSeconds(1));
//                    }
                    Integer i1=o2.getDateTime().compareTo(o1.getDateTime());
                    Integer i2=(StringUtils.hasText(o2.getTitle())?o2.getTitle():"").compareTo(StringUtils.hasText(o1.getTitle())?o1.getTitle():"");
                    if (i1 == 0){
                        return i1.compareTo(i2);
                    }
                    return o2.getDateTime().compareTo(o1.getDateTime());
                }
                return 0;
            }
        });

        return returnList;
    }

    @Override
    public void systemAlarmListExport(Boolean isAll, Boolean isUa, List<Long> ri, Long di, Type alarmType, TagType tagType, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime,String ids, LionPage lionPage) throws IOException, DocumentException {
        IPageResultData<List<SystemAlarmVo>> pageResultData = systemAlarmList(isAll,isUa,ri,di, alarmType, tagType, tagCode, startDateTime, endDateTime, lionPage, null, null,ids , null, "dt");
        List<SystemAlarmVo> list = pageResultData.getData();
        BaseFont bfChinese = BaseFont.createFont(FONT+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("警告記錄.pdf", "UTF-8"));
        Document document = new Document();
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, servletOutputStream);
        String userName = CurrentUserUtil.getCurrentUserUsername();
        writer.setPageEvent(new PdfPageEventHelper(FONT,userName));
        document.open();
        PdfPTable table = new PdfPTable(8);
        table.setWidths(new int[]{10, 10, 10, 10, 20, 10, 20, 10});
        table.setWidthPercentage(100);
        PdfPCell cellTitle = new PdfPCell(new Paragraph("警告記錄", new Font(bfChinese,24)));
        cellTitle.setColspan(8);
        cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTitle);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PdfPCell cellTitle1 = new PdfPCell(new Paragraph("導出日期: "+simpleDateFormat.format(new Date()), new Font(bfChinese)));
        cellTitle1.setColspan(8);
        table.addCell(cellTitle1);
        table.addCell(new Paragraph("警報來源", fontChinese));
        table.addCell(new Paragraph("標籤碼", fontChinese));
        table.addCell(new Paragraph("標籤屬性", fontChinese));
        table.addCell(new Paragraph("科室", fontChinese));
        table.addCell(new Paragraph("報警時間", fontChinese));
		table.addCell(new Paragraph("報警區域", fontChinese));
        table.addCell(new Paragraph("報警原因", fontChinese));
        table.addCell(new Paragraph("狀態", fontChinese));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (SystemAlarmVo systemAlarmVo : list) {
            table.addCell(new Paragraph(Type.instance(systemAlarmVo.getTy()).getDesc(), fontChinese));
            table.addCell(new Paragraph(systemAlarmVo.getTagCode(), fontChinese));
            table.addCell(new Paragraph(systemAlarmVo.getTagType().getDesc()+"標籤", fontChinese));
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(systemAlarmVo.getDi());
            table.addCell(new Paragraph(optionalDepartment.isEmpty()?"":optionalDepartment.get().getName(), fontChinese));
            table.addCell(new Paragraph(dateTimeFormatter.format(systemAlarmVo.getDt()), fontChinese));
			table.addCell(new Paragraph(systemAlarmVo.getRn(), fontChinese));
			table.addCell(new Paragraph(systemAlarmVo.getAlarmContent(), fontChinese));
            String str = "";
            if (Objects.equals(systemAlarmVo.getUa(),0) || Objects.equals(systemAlarmVo.getUa(),2)) {
                str = "未處理";
            }else {
                str = "已處理";
            }
            table.addCell(new Paragraph(str, fontChinese));
        }
        document.add(table);
        document.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    private CurrentRegionVo convertVo(CurrentRegionDto currentRegionDto){
        CurrentRegionVo vo = new CurrentRegionVo();
        if (Objects.isNull(currentRegionDto)) {
            return null;
        }
        vo.setFirstEntryTime(currentRegionDto.getFirstEntryTime());
        Region region = redisUtil.getRegionById(currentRegionDto.getRegionId());
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

    private CurrentRegionVo convertVo(CurrentPosition currentPosition){
        CurrentRegionVo vo = new CurrentRegionVo();
        if (Objects.isNull(currentPosition)) {
            return null;
        }
        vo.setFirstEntryTime(currentPosition.getDdt());
        vo.setBuildId(currentPosition.getBui());
        vo.setBuildName(currentPosition.getBun());
        vo.setBuildFloorId(currentPosition.getBfi());
        vo.setBuildFloorName(currentPosition.getBfn());
        vo.setDepartmentId(currentPosition.getDi());
        vo.setDepartmentName(currentPosition.getDn());
        vo.setRegionId(currentPosition.getRi());
        vo.setRegionName(currentPosition.getRn());
        vo.setX(currentPosition.getX());
        vo.setY(currentPosition.getY());
        return vo;
    }


//    private List<RestrictedAreaVo> restrictedArea(Long pi,PersonType personType){
//        List<RestrictedArea> restrictedAreaList = restrictedAreaExposeServiceService.find(pi, personType);
//        List<RestrictedAreaVo> restrictedAreaVoList = new ArrayList<>();
//        restrictedAreaList.forEach(restrictedArea -> {
//            RestrictedAreaVo restrictedAreaVo = new RestrictedAreaVo();
//            Region region = regionExposeService.findById(restrictedArea.getRegionId());
//            if (Objects.nonNull(region)){
//                restrictedAreaVo.setRegionName(region.getName());
//                restrictedAreaVo.setRegionId(region.getId());
//                restrictedAreaVo.setRemark(region.getRemarks());
//                Build build = buildExposeService.findById(region.getBuildId());
//                if (Objects.nonNull(build)){
//                    restrictedAreaVo.setBuildName(build.getName());
//                }
//                BuildFloor buildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
//                if (Objects.nonNull(buildFloor)) {
//                    restrictedAreaVo.setBuildFloorName(buildFloor.getName());
//                }
//                restrictedAreaVoList.add(restrictedAreaVo);
//            }
//        });
//        return restrictedAreaVoList;
//    }

    private List<Long> find(Type type, Long regionId) {
       List<CurrentPosition>  list = this.currentPositionDao.findByTypAndRi(type.getKey(),regionId);
       List<Long> returnList = new ArrayList<Long>();
       list.forEach(currentPosition -> {
           if (Objects.equals(type,Type.STAFF) || Objects.equals(type,Type.MIGRANT)  || Objects.equals(type,Type.PATIENT)) {
               returnList.add(currentPosition.getPi());
           }else if (Objects.equals(type,Type.HUMIDITY) || Objects.equals(type,Type.TEMPERATURE)) {
               returnList.add(currentPosition.getTi());
           }else if (Objects.equals(type,Type.ASSET)) {
               returnList.add(currentPosition.getAdi());
           }
       });
       returnList.add(Long.MAX_VALUE);
       return returnList;
    }



}
