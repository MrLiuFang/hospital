package com.lion.event.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lion.common.constants.RedisConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.enums.Type;
import com.lion.common.expose.file.FileExposeService;
import com.lion.common.utils.RedisUtil;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.fault.FaultExposeService;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.event.dao.HumitureRecordDao;
import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.HumitureRecord;
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
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientDoctor;
import com.lion.person.entity.person.PatientNurse;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientDoctorExposeService;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.PatientNurseExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
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
import java.time.LocalDateTime;
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

    @DubboReference
    private PatientDoctorExposeService patientDoctorExposeService;

    @DubboReference
    private PatientNurseExposeService patientNurseExposeService;

    @Autowired
    private HttpServletResponse response;

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
            departmentStatisticsDetailsVo.setAssetsCount(assetsExposeService.countByDepartmentId(department.getId(), null));
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
            departmentStaffStatisticsDetailsVo.setStaffCount(departmentStaffStatisticsDetailsVo.getStaffCount() + departmentUserExposeService.count(department.getId(),null ));
            departmentStaffStatisticsDetailsVo.setNormalStaffCount(departmentStaffStatisticsDetailsVo.getNormalStaffCount() + departmentUserExposeService.count(department.getId(), com.lion.upms.entity.enums.State.NORMAL));
            departmentStaffStatisticsDetailsVo.setAbnormalStaffCount(departmentStaffStatisticsDetailsVo.getAbnormalStaffCount() + departmentUserExposeService.count(department.getId(), com.lion.upms.entity.enums.State.ALARM));
            List<Long> userIds = departmentUserExposeService.findAllUser(department.getId(),name);
            List<DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo> listStaff = new ArrayList<>();
            userIds.forEach(id->{
                User user = userExposeService.findById(id);
                if (Objects.nonNull(user)) {
                    DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo staff = new DepartmentStaffStatisticsDetailsVo.DepartmentStaffVo();
                    staff.setUserId(user.getId());
                    staff.setUserName(user.getName());
                    staff.setType(user.getUserType());
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
        List<DepartmentAssetsStatisticsDetailsVo.AssetsDepartmentVo> assetsDepartmentVos = new ArrayList<>();
        departmentAssetsStatisticsDetailsVo.setAssetsDepartmentVos(assetsDepartmentVos);
        list.forEach(department -> {
            DepartmentAssetsStatisticsDetailsVo.AssetsDepartmentVo vo = new DepartmentAssetsStatisticsDetailsVo.AssetsDepartmentVo();
            vo.setDepartmentName(department.getName());
            vo.setDepartmentId(department.getId());
            departmentAssetsStatisticsDetailsVo.setAssetsCount(departmentAssetsStatisticsDetailsVo.getAssetsCount() + assetsExposeService.countByDepartmentId(department.getId(),null ));
            departmentAssetsStatisticsDetailsVo.setNormalAssetsCount(departmentAssetsStatisticsDetailsVo.getNormalAssetsCount() + assetsExposeService.countByDepartmentId(department.getId(), com.lion.manage.entity.enums.State.NORMAL));
            departmentAssetsStatisticsDetailsVo.setAbnormalAssetsCount(departmentAssetsStatisticsDetailsVo.getAbnormalAssetsCount() + assetsExposeService.countByDepartmentId(department.getId(), com.lion.manage.entity.enums.State.ALARM));
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
            assetsDepartmentVos.add(vo);
        });
        return departmentAssetsStatisticsDetailsVo;
    }

    @Override
    public DepartmentTagStatisticsDetailsVo departmentTagStatisticsDetails(String keyword) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        DepartmentTagStatisticsDetailsVo departmentTagStatisticsDetailsVo = new DepartmentTagStatisticsDetailsVo();
        List<DepartmentTagStatisticsDetailsVo.TagDepartmentVo> tagDepartmentVos = new ArrayList<>();
        departmentTagStatisticsDetailsVo.setTagDepartmentVos(tagDepartmentVos);
        list.forEach(department -> {
            DepartmentTagStatisticsDetailsVo.TagDepartmentVo tagDepartmentVo = new DepartmentTagStatisticsDetailsVo.TagDepartmentVo();
            tagDepartmentVo.setDepartmentName(department.getName());
            tagDepartmentVo.setDepartmentId(department.getId());
            departmentTagStatisticsDetailsVo.setTagCount(departmentTagStatisticsDetailsVo.getTagCount() + tagExposeService.countTag(department.getId(), TagPurpose.THERMOHYGROGRAPH,null ));
            departmentTagStatisticsDetailsVo.setNormalTagCount(departmentTagStatisticsDetailsVo.getNormalTagCount() +tagExposeService.countTag(department.getId(), TagPurpose.THERMOHYGROGRAPH, com.lion.device.entity.enums.State.NORMAL ));
            departmentTagStatisticsDetailsVo.setAbnormalTagCount(departmentTagStatisticsDetailsVo.getAbnormalTagCount() +tagExposeService.countTag(department.getId(), TagPurpose.THERMOHYGROGRAPH, com.lion.device.entity.enums.State.ALARM ));
            List<Tag> tagList = tagExposeService.find(department.getId(),TagPurpose.THERMOHYGROGRAPH,keyword);
            List<DepartmentTagStatisticsDetailsVo.TagVo> tagVos = new ArrayList<>();
            tagList.forEach(tag -> {
                DepartmentTagStatisticsDetailsVo.TagVo vo = new DepartmentTagStatisticsDetailsVo.TagVo();
                BeanUtils.copyProperties(tag,vo);
                HumitureRecord record = humitureRecordDao.find(tag.getId());
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
            tagDepartmentVo.setTagVos(tagVos);
            tagDepartmentVos.add(tagDepartmentVo);
        });
        return departmentTagStatisticsDetailsVo;
    }

    @Override
    public DepartmentPatientStatisticsDetailsVo departmentPatientStatisticsDetails(String name) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        DepartmentPatientStatisticsDetailsVo departmentPatientStatisticsDetailsVo = new DepartmentPatientStatisticsDetailsVo();
        List<DepartmentPatientStatisticsDetailsVo.PatientDepartmentVo> patientDepartmentVos = new ArrayList<>();
        departmentPatientStatisticsDetailsVo.setPatientDepartmentVos(patientDepartmentVos);
        list.forEach(department -> {
            DepartmentPatientStatisticsDetailsVo.PatientDepartmentVo patientDepartmentVo = new DepartmentPatientStatisticsDetailsVo.PatientDepartmentVo();
            patientDepartmentVo.setDepartmentName(department.getName());
            patientDepartmentVo.setDepartmentId(department.getId());
            departmentPatientStatisticsDetailsVo.setPatientCount(departmentPatientStatisticsDetailsVo.getPatientCount() + patientExposeService.count(department.getId(), null));
            departmentPatientStatisticsDetailsVo.setNormalPatientCount(departmentPatientStatisticsDetailsVo.getNormalPatientCount() + patientExposeService.count(department.getId(), State.NORMAL));
            departmentPatientStatisticsDetailsVo.setAbnormalPatientCount(departmentPatientStatisticsDetailsVo.getAbnormalPatientCount() +  patientExposeService.count(department.getId(), State.ALARM));
            List<DepartmentPatientStatisticsDetailsVo.PatientVo> patientVos = new ArrayList<>();
            List<Patient> patientList = patientExposeService.find(department.getId(),name);
            patientList.forEach(patient -> {
                DepartmentPatientStatisticsDetailsVo.PatientVo vo = new DepartmentPatientStatisticsDetailsVo.PatientVo();
                Tag tag = tagExposeService.find(patient.getTagCode());
                if (Objects.nonNull(tag)){
                    vo.setBattery(tag.getBattery());
                }
                WardRoomSickbed wardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
                if (Objects.nonNull(wardRoomSickbed)){
                    vo.setBedCode(wardRoomSickbed.getBedCode());
                }
                vo.setName(patient.getName());
                vo.setTagCode(patient.getTagCode());
                vo.setHeadPortrait(patient.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
                patientVos.add(vo);
            });
            patientDepartmentVo.setPatientVos(patientVos);
            patientDepartmentVos.add(patientDepartmentVo);

        });
        return departmentPatientStatisticsDetailsVo;
    }

    @Override
    public DepartmentTemporaryPersonStatisticsDetailsVo departmentTemporaryPersonStatisticsDetails(String name) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
        DepartmentTemporaryPersonStatisticsDetailsVo departmentTemporaryPersonStatisticsDetailsVo = new DepartmentTemporaryPersonStatisticsDetailsVo();
        List<DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonDepartmentVo> temporaryPersonDepartmentVos = new ArrayList<>();
        departmentTemporaryPersonStatisticsDetailsVo.setTemporaryPersonDepartmentVos(temporaryPersonDepartmentVos);
        list.forEach(department -> {
            DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonDepartmentVo temporaryPersonDepartmentVo = new DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonDepartmentVo();
            temporaryPersonDepartmentVo.setDepartmentName(department.getName());
            temporaryPersonDepartmentVo.setDepartmentId(department.getId());
            departmentTemporaryPersonStatisticsDetailsVo.setTemporaryPersonCount(departmentTemporaryPersonStatisticsDetailsVo.getTemporaryPersonCount() + temporaryPersonExposeService.count(department.getId(), null));
            departmentTemporaryPersonStatisticsDetailsVo.setNormalTemporaryPersonCount(departmentTemporaryPersonStatisticsDetailsVo.getNormalTemporaryPersonCount() + temporaryPersonExposeService.count(department.getId(), State.NORMAL));
            departmentTemporaryPersonStatisticsDetailsVo.setAbnormalTemporaryPersonCount(departmentTemporaryPersonStatisticsDetailsVo.getAbnormalTemporaryPersonCount() +  temporaryPersonExposeService.count(department.getId(), State.ALARM));
            List<DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo> temporaryPersonVos = new ArrayList<>();
            List<TemporaryPerson> temporaryPersonList = temporaryPersonExposeService.find(department.getId(),name);
            temporaryPersonList.forEach(temporaryPerson -> {
                DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo vo = new DepartmentTemporaryPersonStatisticsDetailsVo.TemporaryPersonVo();
                Tag tag = tagExposeService.find(temporaryPerson.getTagCode());
                if (Objects.nonNull(tag)){
                    vo.setBattery(tag.getBattery());
                }
                vo.setName(temporaryPerson.getName());
                vo.setTagCode(temporaryPerson.getTagCode());
                vo.setHeadPortrait(temporaryPerson.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
                temporaryPersonVos.add(vo);
            });
            temporaryPersonDepartmentVo.setTemporaryPersonVos(temporaryPersonVos);
            temporaryPersonDepartmentVos.add(temporaryPersonDepartmentVo);
        });
        return departmentTemporaryPersonStatisticsDetailsVo;
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
//        staffDetailsVo.setPositions(positionService.findUserId(userId,LocalDateTime.of(now.toLocalDate(), LocalTime.MIN),now));
        staffDetailsVo.setCurrentRegionVo(userCurrentRegion(userId));
//        staffDetailsVo.setSystemAlarms(systemAlarmService.find(userId,false,LocalDateTime.of(now.toLocalDate(), LocalTime.MIN),now));
        return staffDetailsVo;
    }

    @Override
    public CurrentRegionVo userCurrentRegion(Long userId) {
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
            CurrentRegionVo vo = convertVo(userCurrentRegionDto);
            vo.setFirstEntryTime(userCurrentRegionDto.getFirstEntryTime());
            return vo;
        }
        return null;
    }

    @Override
    public AssetsDetailsVo assetsDetails(Long assetsId) {
        Assets assets = assetsExposeService.findById(assetsId);
        if (Objects.isNull(assets)){
            return null;
        }
        AssetsDetailsVo assetsDetailsVo = new AssetsDetailsVo();
        BeanUtils.copyProperties(assets,assetsDetailsVo);
        Region region = regionExposeService.findById(assetsDetailsVo.getRegionId());
        if (Objects.nonNull(region)) {
            assetsDetailsVo.setRegionId(region.getId());
            assetsDetailsVo.setRegionName(region.getName());
            Build build = buildExposeService.findById(region.getBuildId());
            if (Objects.nonNull(build)) {
                assetsDetailsVo.setBuildId(build.getId());
                assetsDetailsVo.setBuildName(build.getName());
            }
            BuildFloor buildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
            if (Objects.nonNull(buildFloor)) {
                assetsDetailsVo.setBuildFloorId(buildFloor.getId());
                assetsDetailsVo.setBuildFloorName(buildFloor.getName());
            }
            Department department =  departmentExposeService.findById(region.getDepartmentId());
            if (Objects.nonNull(department)) {
                assetsDetailsVo.setDepartmentId(department.getId());
                assetsDetailsVo.setDepartmentName(department.getName());
            }
        }
        TagAssets tagAssets = tagAssetsExposeService.find(assetsId);
        if (Objects.nonNull(tagAssets)) {
            Tag tag = tagExposeService.findById(tagAssets.getTagId());
            if (Objects.nonNull(tag)){
                assetsDetailsVo.setBattery(tag.getBattery());
            }
        }
        LocalDateTime now = LocalDateTime.now();
        assetsDetailsVo.setPositions(positionService.findByAssetsId(assets.getId(),now.minusDays(30),now));
        assetsDetailsVo.setFaultRecord(faultExposeService.findLast(assets.getId()));
        return assetsDetailsVo;
    }

    @Override
    public PatientDetailsVo patientDetails(Long patientId) {
        PatientDetailsVo vo = new PatientDetailsVo();
        Patient patient = patientExposeService.findById(patientId);
        BeanUtils.copyProperties(patient,vo);
        Tag tag = tagExposeService.findById(patient.getTagCode());
        if (Objects.nonNull(tag)) {
            vo.setBattery(tag.getBattery());
        }
        WardRoomSickbed wardRoomSickbed = wardRoomSickbedExposeService.findById(patient.getSickbedId());
        if (Objects.nonNull(wardRoomSickbed)) {
            vo.setBedCode(wardRoomSickbed.getBedCode());
        }
        CurrentRegionDto currentRegion = (CurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.PATIENT_CURRENT_REGION+patientId);
        vo.setCurrentRegionVo(convertVo(currentRegion));
        List<PatientNurse> patientNurses = patientNurseExposeService.find(patient.getId());
        List<PatientDetailsVo.NurseVo> nurseVos = new ArrayList<>();
        patientNurses.forEach(patientNurse -> {
            User nurse = userExposeService.findById(patientNurse.getNurseId());
            PatientDetailsVo.NurseVo nurseVo = new PatientDetailsVo.NurseVo();
            if (Objects.nonNull(nurse)){
                nurseVo.setNurseName(nurse.getName());
                nurseVo.setNurseHeadPortrait(nurse.getHeadPortrait());
                nurseVo.setNurseHeadPortraitUrl(fileExposeService.getUrl(nurse.getHeadPortrait()));
                nurseVos.add(nurseVo);
            }
        });
        vo.setNurseVos(nurseVos);
        List<PatientDoctor> patientDoctors = patientDoctorExposeService.find(patient.getId());
        List<PatientDetailsVo.DoctorVo> doctorVos = new ArrayList<>();
        patientDoctors.forEach(patientDoctor -> {
            User doctor = userExposeService.findById(patientDoctor.getDoctorId());
            PatientDetailsVo.DoctorVo doctorVo = new PatientDetailsVo.DoctorVo();
            if (Objects.nonNull(doctor)){
                doctorVo.setDoctorName(doctor.getName());
                doctorVo.setDoctorHeadPortrait(doctor.getHeadPortrait());
                doctorVo.setDoctorHeadPortraitUrl(fileExposeService.getUrl(doctor.getHeadPortrait()));
                doctorVos.add(doctorVo);
            }
        });
        vo.setDoctorVos(doctorVos);
        return vo;
    }

    @Override
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList(Boolean isAll, Boolean isUa, Long ri, Long di, Type alarmType, TagType tagType, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        LocalDateTime now = LocalDateTime.now();
        List<Long> departmentIds = new ArrayList<>();
        if (!isAll) {
            Long userId = CurrentUserUtil.getCurrentUserId();
            List<Department> list = departmentResponsibleUserExposeService.findDepartment(userId);
            list.forEach(department -> {
                departmentIds.add(department.getId());
            });
        }
        if (Objects.nonNull(di)){
            departmentIds.add(di);
        }
//        if (Objects.isNull(startDateTime)) {
//            startDateTime = LocalDateTime.of(now.toLocalDate(), LocalTime.MIN);
//        }if (Objects.isNull(endDateTime)) {
//            endDateTime = now;
//        }
        List<Long> tagIds = Collections.EMPTY_LIST;
        if (Objects.nonNull(tagType)){
            tagIds.addAll(tagExposeService.find(tagType));
        }
        if (StringUtils.hasText(tagCode)){
            tagIds.clear();
            Tag tag = tagExposeService.find(tagCode);
            if (Objects.nonNull(tag)) {
                tagIds.add(tag.getId());
            }
        }

        return systemAlarmService.list(lionPage,departmentIds, isUa,ri, alarmType,tagIds,startDateTime,endDateTime);
    }

    @Override
    public void systemAlarmListExport(Boolean isAll, Boolean isUa, Long ri, Long di, Type alarmType, TagType tagType, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException {
        IPageResultData<List<SystemAlarmVo>> pageResultData = systemAlarmList(isAll,isUa,ri,di,alarmType,tagType,tagCode,startDateTime,endDateTime,lionPage);
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
            table.addCell(new Paragraph(systemAlarmVo.getType().getDesc(), fontChinese));
            Department department = departmentExposeService.findById(systemAlarmVo.getDi());
            table.addCell(new Paragraph(Objects.isNull(department)?"":department.getName(), fontChinese));
            table.addCell(new Paragraph(dateTimeFormatter.format(systemAlarmVo.getDt()), fontChinese));
			table.addCell(new Paragraph(systemAlarmVo.getRn(), fontChinese));
			table.addCell(new Paragraph(systemAlarmVo.getAlarmContent(), fontChinese));
            table.addCell(new Paragraph(Objects.equals(systemAlarmVo.getUa(),1)?"已处理":"未处理", fontChinese));
        }
        document.add(table);
        document.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    private CurrentRegionVo convertVo(CurrentRegionDto currentRegionDto){
        CurrentRegionVo vo = new CurrentRegionVo();
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
}
