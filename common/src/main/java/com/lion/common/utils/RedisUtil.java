package com.lion.common.utils;

import com.lion.common.constants.RedisConstants;
import com.lion.common.enums.Type;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.*;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.*;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionDevice;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.WashTemplate;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.entity.rule.vo.DetailsWashTemplateVo;
import com.lion.manage.entity.rule.vo.ListWashTemplateItemVo;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.region.RegionDeviceExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.rule.AlarmExposeService;
import com.lion.manage.expose.rule.WashDeviceTypeExposeService;
import com.lion.manage.expose.rule.WashTemplateExposeService;
import com.lion.manage.expose.rule.WashTemplateItemExposeService;
import com.lion.manage.expose.ward.WardRoomExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.enums.AlarmMode;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/4/25 下午4:55
 **/
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private DeviceExposeService deviceExposeService;

//    @DubboReference
//    private DeviceGroupDeviceExposeService deviceGroupDeviceExposeService;

    @DubboReference
    private RegionDeviceExposeService regionDeviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private TagUserExposeService tagUserExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

//    @DubboReference
//    private WashExposeService washExposeService;
//
//    @DubboReference
//    private WashDeviceExposeService washDeviceExposeService;

    @DubboReference
    private AlarmExposeService alarmExposeService;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private WashDeviceTypeExposeService washDeviceTypeExposeService;

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TagPatientExposeService tagPatientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @DubboReference
    private TagPostdocsExposeService tagPostdocsExposeService;

    @DubboReference
    private TagRuleExposeService tagRuleExposeService;

    @DubboReference
    private WardRoomSickbedExposeService wardRoomSickbedExposeService;

    @DubboReference
    private WardRoomExposeService wardRoomExposeService;

    @DubboReference
    private WashTemplateExposeService washTemplateExposeService;

    @DubboReference
    private WashTemplateItemExposeService washTemplateItemExposeService;

    public TemporaryPerson getTemporaryPerson(Long temporaryPersonId) {
        if (Objects.isNull(temporaryPersonId)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.TEMPORARY_PERSON+temporaryPersonId);
        TemporaryPerson temporaryPerson = null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof TemporaryPerson)){
                redisTemplate.delete(RedisConstants.TEMPORARY_PERSON+temporaryPersonId);
            }else {
                temporaryPerson = (TemporaryPerson) object;
            }
        }

        if (Objects.isNull(temporaryPerson)){
            temporaryPerson = temporaryPersonExposeService.findById(temporaryPersonId);
            if (Objects.nonNull(temporaryPerson)){
                redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON+temporaryPerson.getId(),temporaryPerson,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return temporaryPerson;
    }

    public TemporaryPerson getTemporaryPersonByTagId(Long tagId) {
        if (Objects.isNull(tagId)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG_TEMPORARY_PERSON+tagId);
        Long temporaryPersonId = null;
        TemporaryPerson temporaryPerson = null;

        if (Objects.nonNull(obj) && !(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.TAG_TEMPORARY_PERSON + tagId);
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagId);
            obj = null;
        }
        if (Objects.nonNull(obj)){
            temporaryPersonId = (Long) obj;
        }

        if (Objects.nonNull(temporaryPersonId)){
            Object object = redisTemplate.opsForValue().get(RedisConstants.TEMPORARY_PERSON+temporaryPersonId);
            if (Objects.nonNull(object) && !(object instanceof  TemporaryPerson)){
                redisTemplate.delete(RedisConstants.TEMPORARY_PERSON+temporaryPersonId);
                object = null;
            }
            if (Objects.nonNull(object)){
                temporaryPerson = (TemporaryPerson) object;
            }

            if (Objects.isNull(temporaryPerson)){
                temporaryPerson =temporaryPersonExposeService.findById(temporaryPersonId);
                if (Objects.nonNull(temporaryPerson)){
                    redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON+temporaryPersonId,temporaryPerson, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }

        if (Objects.isNull(temporaryPerson)){
            TagPostdocs tagPostdocs = tagPostdocsExposeService.find(tagId);
            if (Objects.nonNull(tagPostdocs)){
                temporaryPerson = temporaryPersonExposeService.findById(tagPostdocs.getPostdocsId());
                if (Objects.nonNull(temporaryPerson)) {
                    redisTemplate.opsForValue().set(RedisConstants.TAG_TEMPORARY_PERSON + tagId, temporaryPerson.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON_TAG + temporaryPerson.getId(), tagId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON + temporaryPerson.getId(), temporaryPerson, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.TAG_BIND_TYPE+tagId, Type.MIGRANT, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.delete(RedisConstants.TAG_TEMPORARY_PERSON + tagId);
                    redisTemplate.delete(RedisConstants.TEMPORARY_PERSON_TAG +  tagPostdocs.getPostdocsId());
                    redisTemplate.delete(RedisConstants.TEMPORARY_PERSON + tagPostdocs.getPostdocsId());
                    redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagId);
                }
            }
        }
        return temporaryPerson;
    }

    public Patient getPatientByTagId(Long tagId){
        if (Objects.isNull(tagId)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG_PATIENT+tagId);
        Long patientId = null;
        Patient patient = null;

        if (Objects.nonNull(obj) && !(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.TAG_PATIENT + tagId);
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagId);
            obj = null;
        }
        if (Objects.nonNull(obj)){
            patientId = (Long) obj;
        }

        if (Objects.nonNull(patientId)){
            Object object = redisTemplate.opsForValue().get(RedisConstants.PATIENT+patientId);
            if (Objects.nonNull(object) && !(object instanceof  Patient)){
                redisTemplate.delete(RedisConstants.PATIENT+patientId);
                object = null;
            }
            if (Objects.nonNull(object)){
                patient = (Patient) object;
            }

            if (Objects.isNull(patient)){
                patient = patientExposeService.findById(patientId);
                if (Objects.nonNull(patient)){
                    redisTemplate.opsForValue().set(RedisConstants.PATIENT+patientId,patient, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }

        if (Objects.isNull(patient)){
            TagPatient tagPatient = tagPatientExposeService.find(tagId);
            if (Objects.nonNull(tagPatient)){
                patient = patientExposeService.findById(tagPatient.getPatientId());
                if (Objects.nonNull(patient)) {
                    redisTemplate.opsForValue().set(RedisConstants.TAG_PATIENT + tagId, patient.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.PATIENT_TAG + patient.getId(), tagId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.PATIENT + patient.getId(), patient, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.TAG_BIND_TYPE+tagId, Type.PATIENT, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.delete(RedisConstants.TAG_PATIENT + tagId);
                    redisTemplate.delete(RedisConstants.PATIENT_TAG +  tagPatient.getPatientId());
                    redisTemplate.delete(RedisConstants.PATIENT + tagPatient.getPatientId());
                    redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagId);
                }
            }
        }
        return patient;
    }

    public Patient getPatient(Long id){
        if (Objects.isNull(id)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.PATIENT+id);
        Patient patient = null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof Patient)){
                redisTemplate.delete(RedisConstants.PATIENT+id);
            }else {
                patient = (Patient) object;
            }
        }

        if (Objects.isNull(patient)){
            patient = patientExposeService.findById(id);
            if (Objects.nonNull(patient)){
                redisTemplate.opsForValue().set(RedisConstants.PATIENT+patient.getId(),patient,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return patient;
    }
    public Department getDepartmentByDeviceId(Long deviceId) {
        if (Objects.isNull(deviceId)){
            return null;
        }
        RegionDevice regionDevice = regionDeviceExposeService.find(deviceId);
        if (Objects.nonNull(regionDevice)) {
            Region region = regionExposeService.findById(regionDevice.getRegionId());
            if (Objects.nonNull(region)) {
                Department department = departmentExposeService.findById(region.getDepartmentId());
                return department;
            }
        }
        return null;
    }

    public Department getDepartmentByUserId(Long userId) {
        if (Objects.isNull(userId)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.USER_DEPARTMENT+userId);
        Long departmentId = null;
        Department department = null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof Long)){
                redisTemplate.delete(RedisConstants.USER_DEPARTMENT+userId);
            }else {
                departmentId = (Long) object;
            }
        }

        if (Objects.nonNull(departmentId)) {
            department = getDepartment(departmentId);
        }

        if (Objects.isNull(department)){
            department = departmentUserExposeService.findDepartment(userId);
            if (Objects.nonNull(department)){
                redisTemplate.opsForValue().set(RedisConstants.USER_DEPARTMENT+userId,department.getId(),RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
                redisTemplate.opsForValue().set(RedisConstants.DEPARTMENT+department.getId(),department,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return department;

    }
    public Department getDepartment(Long departmentId) {
        if (Objects.isNull(departmentId)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.DEPARTMENT+departmentId);
        Department department = null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof Department)){
                redisTemplate.delete(RedisConstants.DEPARTMENT+departmentId);
            }else {
                department = (Department) object;
            }
        }

        if (Objects.isNull(department)){
            department = departmentExposeService.findById(departmentId);
            if (Objects.nonNull(department)){
                redisTemplate.opsForValue().set(RedisConstants.DEPARTMENT+department.getId(),department,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return department;
    }

    public BuildFloor getBuildFloor(Long buildFloorId) {
        if (Objects.isNull(buildFloorId)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.BUILD_FLOOR+buildFloorId);
        BuildFloor buildFloor = null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof BuildFloor)){
                redisTemplate.delete(RedisConstants.BUILD_FLOOR+buildFloorId);
            }else {
                buildFloor = (BuildFloor) object;
            }
        }

        if (Objects.isNull(buildFloor)){
            buildFloor = buildFloorExposeService.findById(buildFloorId);
            if (Objects.nonNull(buildFloor)){
                redisTemplate.opsForValue().set(RedisConstants.BUILD_FLOOR+buildFloor.getId(),buildFloor,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return buildFloor;
    }

    public Build getBuild(Long buildId){
        if (Objects.isNull(buildId)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.BUILD+buildId);
        Build build = null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof Build)){
                redisTemplate.delete(RedisConstants.BUILD+buildId);
            }else {
                build = (Build) object;
            }
        }

        if (Objects.isNull(build)){
            build = buildExposeService.findById(buildId);
            if (Objects.nonNull(build)){
                redisTemplate.opsForValue().set(RedisConstants.BUILD+build.getId(),build,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return build;
    }

    public Region getRegionById(Long regionId){
        if (Objects.isNull(regionId)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.REGION+regionId);
        Region region= null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof Region)){
                redisTemplate.delete(RedisConstants.REGION+regionId);
            }else {
                region = (Region) object;
            }
        }

        if (Objects.isNull(region)){
            region = regionExposeService.findById(regionId);
            if (Objects.nonNull(region)){
                redisTemplate.opsForValue().set(RedisConstants.REGION+region.getId(),region,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return region;
    }

    public Region getRegion(Long deviceId){
        if (Objects.isNull(deviceId)){
            return null;
        }
//        Object obj = redisTemplate.opsForValue().get(RedisConstants.DEVICE_REGION+deviceId);
//        Long regionId = null;
//        Region region =null;
//
//        if (Objects.nonNull(obj) && !(obj instanceof Long)){
//            redisTemplate.delete(RedisConstants.DEVICE_REGION + deviceId);
//            obj = null;
//        }
//        if (Objects.nonNull(obj)){
//            regionId= (Long) obj;
//        }
//
//        if (Objects.nonNull(regionId)){
//            region = (Region) redisTemplate.opsForValue().get(RedisConstants.REGION+regionId);
//            if (Objects.isNull(region)){
//                region = regionExposeService.findById(regionId);
//                if (Objects.nonNull(region)){
//                    redisTemplate.opsForValue().set(RedisConstants.REGION+region.getId(),region, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                }
//            }
//        }
//        if (Objects.isNull(region)){
//            RegionDevice regionDevice = regionDeviceExposeService.find(deviceId);
//            if (Objects.nonNull(regionDevice)){
//                region = regionExposeService.find(regionDevice.getRegionId());
//                if (Objects.nonNull(region)){
//                    redisTemplate.opsForValue().set(RedisConstants.DEVICE_REGION+deviceId,region.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                    redisTemplate.opsForValue().set(RedisConstants.REGION+region.getId(),region, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                }else {
//                    redisTemplate.delete(RedisConstants.DEVICE_REGION+deviceId);
//                    redisTemplate.delete(RedisConstants.REGION+regionId);
//                }
//            }
//        }
        Device device = this.getDevice(deviceId);
        if (Objects.isNull(device)) {
            return null;
        }
        if (Objects.nonNull(device.getRegionId())) {
                Region region = this.getRegionById(device.getRegionId());
                if (Objects.nonNull(region)) {
                    return region;
                }
        }
        return null;
    }
    public User getUserById(Long userId){
        if (Objects.isNull(userId)){
            return null;
        }
        Object object = redisTemplate.opsForValue().get(RedisConstants.USER+userId);
        User user = null;
        if (Objects.nonNull(object)) {
            if (!(object instanceof User)){
                redisTemplate.delete(RedisConstants.USER+userId);
            }else {
                user = (User) object;
            }
        }

        if (Objects.isNull(user)){
            user = userExposeService.findById(userId);
            if (Objects.nonNull(user)){
                redisTemplate.opsForValue().set(RedisConstants.USER+user.getId(),user,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return user;

    }
    public User getUser(Long tagId){
        if (Objects.isNull(tagId)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG_USER+tagId);
        Long userId = null;
        User user = null;

        if (Objects.nonNull(obj) && !(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.TAG_USER + tagId);
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagId);
            obj = null;
        }
        if (Objects.nonNull(obj)){
            userId = (Long) obj;
        }

        if (Objects.nonNull(userId)){
            Object object = redisTemplate.opsForValue().get(RedisConstants.USER+userId);
            if (Objects.nonNull(object) && !(object instanceof  User)){
                redisTemplate.delete(RedisConstants.USER+userId);
            }else if (Objects.nonNull(object)){
                user = (User) object;
            }

            if (Objects.isNull(user)){
                user = userExposeService.findById(userId);
                if (Objects.nonNull(user)){
                    redisTemplate.opsForValue().set(RedisConstants.USER+userId,user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }

        if (Objects.isNull(user)){
            TagUser tagUser = tagUserExposeService.find(tagId);
            if (Objects.nonNull(tagUser)){
                user = userExposeService.findById(tagUser.getUserId());
                if (Objects.nonNull(user)) {
                    redisTemplate.opsForValue().set(RedisConstants.TAG_USER + tagId, user.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.USER_TAG + user.getId(), tagId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.USER + user.getId(), user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.TAG_BIND_TYPE+tagId, Type.ASSET, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.delete(RedisConstants.TAG_USER + tagId);
                    redisTemplate.delete(RedisConstants.USER_TAG + userId);
                    redisTemplate.delete(RedisConstants.USER + userId);
                    redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagId);
                }
            }
        }
        return user;
    }

    public Device getDevice(Long id) {
        if (Objects.isNull(id)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.DEVICE+id);
        Device device = null;

        if (Objects.nonNull(obj) && !(obj instanceof Device)){
            redisTemplate.delete(RedisConstants.DEVICE+id);
            obj = null;
        }
        if (Objects.nonNull(obj)){
            device = (Device) obj;
        }

        if (Objects.isNull(device)){
            device = deviceExposeService.findById(id);
            if (Objects.nonNull(device)){
                redisTemplate.opsForValue().set(RedisConstants.DEVICE+id,device, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return device;
    }

    public Device getDevice(String code) {
        if (!StringUtils.hasText(code)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.DEVICE_CODE+code);
        Device device = null;
        if (Objects.nonNull(obj) && !(obj instanceof Device)){
            redisTemplate.delete(RedisConstants.DEVICE_CODE+code);
            obj = null;
        }
        if (Objects.nonNull(obj)) {
            device = (Device) obj;
        }

        if (Objects.isNull(device)){
            device = deviceExposeService.find(code);
            if (Objects.nonNull(device)){
                redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+code,device, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return device;
    }

    public Tag getTagById(Long id){
        if (Objects.isNull(id)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG+id);
        Tag tag = null;

        if (Objects.nonNull(obj) && !(obj instanceof Tag)){
            redisTemplate.delete(RedisConstants.TAG+id);
            obj = null;
        }
        if (Objects.nonNull(obj)){
            tag = (Tag) obj;
        }

        if (Objects.isNull(tag)){
            tag = tagExposeService.findById(id);
            if (Objects.nonNull(tag)){
                redisTemplate.opsForValue().set(RedisConstants.TAG+id,tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return tag;
    }

    public Tag getTag(String tagCode) {
        if (!StringUtils.hasText(tagCode)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG_CODE+tagCode);
        Tag tag = null;

        if (Objects.nonNull(obj) && !(obj instanceof Tag)){
            redisTemplate.delete(RedisConstants.TAG_CODE+tagCode);
            obj = null;
        }
        if (Objects.nonNull(obj)) {
            tag = (Tag) obj;
        }

        if (Objects.isNull(tag)){
            tag = tagExposeService.find(tagCode);
            if (Objects.nonNull(tag)) {
                redisTemplate.opsForValue().set(RedisConstants.TAG_CODE + tagCode, tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return tag;
    }

    public List<WashDeviceType> getWashDeviceType(Long washId) {
        List<Object> list = redisTemplate.opsForList().range(RedisConstants.WASH_DEVICE_TYPE+washId,0,-1);
        list.forEach(o -> {
            if (!(o instanceof WashDeviceType)) {
                redisTemplate.delete(RedisConstants.WASH_DEVICE_TYPE+washId);
            }
        });
        List<WashDeviceType> washDeviceTypes = redisTemplate.opsForList().range(RedisConstants.WASH_DEVICE_TYPE+washId,0,-1);
        if (Objects.isNull(washDeviceTypes) || washDeviceTypes.size()<=0){
            washDeviceTypes = washDeviceTypeExposeService.find(washId);
            if (Objects.nonNull(washDeviceTypes) && washDeviceTypes.size()>0) {
                redisTemplate.delete(RedisConstants.WASH_DEVICE_TYPE+washId);
                redisTemplate.opsForList().leftPushAll(RedisConstants.WASH_DEVICE_TYPE+washId,washDeviceTypes);
                redisTemplate.expire(RedisConstants.WASH_DEVICE_TYPE+washId,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return washDeviceTypes;
    }

//    public List<Device> getWashDevice(Long washId) {
//        if (Objects.isNull(washId)){
//            return null;
//        }
//        List<Object> objectList = redisTemplate.opsForList().range(RedisConstants.WASH_DEVICE+washId,0,-1);
//        if (Objects.nonNull(objectList) && objectList.size()>0){
//            objectList.forEach(o -> {
//                if (!(o instanceof Long)){
//                    redisTemplate.delete(RedisConstants.WASH_DEVICE+washId);
//                }
//            });
//        }
//
//        List<Long> washDeviceId = redisTemplate.opsForList().range(RedisConstants.WASH_DEVICE+washId,0,-1);
//        List<Device> deviceList = new ArrayList<Device>();
//        if (Objects.isNull(washDeviceId) || washDeviceId.size()<=0){
//            washDeviceId = new ArrayList<Long>();
//            List<WashDevice> list = washDeviceExposeService.find(washId);
//            if (Objects.nonNull(list) && list.size()>0){
//                for (WashDevice washDevice : list){
//                    washDeviceId.add(washDevice.getDeviceId());
//                }
//            }
//            if (washDeviceId.size()>0) {
//                redisTemplate.opsForList().leftPushAll(RedisConstants.WASH_DEVICE+washId,washDeviceId);
//                redisTemplate.expire(RedisConstants.WASH_DEVICE+washId,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
//            }
//        }
//        washDeviceId.forEach(id->{
//            Device device = deviceExposeService.findById(id);
//            if (Objects.nonNull(device)){
//                deviceList.add(device);
//            }
//        });
//        return deviceList;
//    }

//    public List<Wash> getLoopWash(){
//        List<Long> objList = redisTemplate.opsForList().range(RedisConstants.ALL_USER_LOOP_WASH,0,-1);
//        if (Objects.nonNull(objList) && objList.size()>0){
//            objList.forEach(o -> {
//                if (!(o instanceof Long)){
//                    redisTemplate.delete(RedisConstants.ALL_USER_LOOP_WASH);
//                }
//            });
//        }
//        List<Long> list = redisTemplate.opsForList().range(RedisConstants.ALL_USER_LOOP_WASH,0,-1);
//        List<Wash> washList = new ArrayList<Wash>();
//        if (Objects.nonNull(list) && list.size()>0){
//            for (Long id  : list){
//                Wash wash = getWashById(id);
//                if (Objects.nonNull(wash)) {
//                    washList.add(wash);
//                }
//            };
//        }
//        list.clear();
//        redisTemplate.delete(RedisConstants.ALL_USER_LOOP_WASH);
//        if (Objects.nonNull(washList) && washList.size()<=0){
//            washList = washExposeService.findLoopWash(true);
//            washList.forEach(wash -> {
//                list.add(wash.getId());
//                redisTemplate.opsForValue().set(RedisConstants.WASH + wash.getId(), wash, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//            });
//            if (Objects.nonNull(list) && list.size()>0) {
//                redisTemplate.opsForList().leftPushAll(RedisConstants.ALL_USER_LOOP_WASH, list);
//            }
//        }
//
//        return washList;
//    }
//
//    public Wash getWashById(Long washId) {
//        Object object = redisTemplate.opsForValue().get(RedisConstants.WASH + washId);
//        Wash wash = null;
//        if (Objects.nonNull(object) && !(object instanceof Wash )){
//            redisTemplate.delete(RedisConstants.WASH + washId);
//            wash = null;
//        }
//        if (Objects.nonNull(object)){
//            wash = (Wash) object;
//        }
//        if (Objects.isNull(wash)) {
//            wash = washExposeService.findById(washId);
//            if (Objects.nonNull(wash)) {
//                redisTemplate.opsForValue().set(RedisConstants.WASH + washId, wash, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//            }
//        }
//        return wash;
//    }
//
//    public List<Wash> getLoopWashByUserId(Long userId){
//        List<Object> objList = redisTemplate.opsForList().range(RedisConstants.USER_LOOP_WASH+userId,0,-1);
//        if (Objects.nonNull(objList) && objList.size()>0){
//            objList.forEach(o -> {
//                if (!(o instanceof Long)){
//                    redisTemplate.delete(RedisConstants.USER_LOOP_WASH+userId);
//                }
//            });
//        }
//
//        List<Long> list = redisTemplate.opsForList().range(RedisConstants.USER_LOOP_WASH+userId,0,-1);
//        List<Wash> washList = new ArrayList<Wash>();
//        if (Objects.nonNull(list) || list.size() >0 ) {
//            for (Long id : list) {
//                Wash wash = getWashById(id);
//                if (Objects.nonNull(wash)) {
//                    washList.add(wash);
//                }
//            }
//        }
//        list.clear();
//        if (washList.size()<=0){
//            washList= washExposeService.findLoopWash(userId);
//            washList.forEach(wash -> {
//                redisTemplate.opsForValue().set(RedisConstants.WASH+wash.getId(),wash,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                list.add(wash.getId());
//            });
//            if (list.size()>0){
//                redisTemplate.opsForList().leftPushAll(RedisConstants.USER_LOOP_WASH+userId,list);
//            }
//        }
//        return washList;
//    }

    public ListWashTemplateItemVo getWashTemplate(Long washTemplateId) {
        Object obj = redisTemplate.opsForValue().get(RedisConstants.WASH_TEMPLATE+ washTemplateId);
        DetailsWashTemplateVo detailsWashTemplateVo = null;
        if (!(obj instanceof DetailsWashTemplateVo)) {
            redisTemplate.delete(RedisConstants.WASH_TEMPLATE+ washTemplateId);
        }else {
            detailsWashTemplateVo = (DetailsWashTemplateVo) obj;
        }
        if (Objects.isNull(detailsWashTemplateVo)) {
            WashTemplate washTemplate = washTemplateExposeService.findById(washTemplateId);
            BeanUtils.copyProperties(washTemplate,detailsWashTemplateVo);
            List<WashTemplateItem> list = washTemplateItemExposeService.findByWashTemplateId(washTemplate.getId());
            List<ListWashTemplateItemVo> listWashTemplateItemVos = new ArrayList<ListWashTemplateItemVo>();
            list.forEach(washTemplateItem -> {
                ListWashTemplateItemVo listWashTemplateItemVo = new ListWashTemplateItemVo();
                BeanUtils.copyProperties(washTemplateItem,listWashTemplateItemVo);
                List<com.lion.manage.entity.enums.WashDeviceType> deviceTypes = washDeviceTypeExposeService.find(washTemplateItem.getId());
                listWashTemplateItemVo.setWashDeviceTypes(deviceTypes);
                listWashTemplateItemVos.add(listWashTemplateItemVo);
            });
            detailsWashTemplateVo.setListWashTemplateItemVos(listWashTemplateItemVos);
        }

        AlarmMode alarmMode = (AlarmMode) redisTemplate.opsForValue().get(RedisConstants.ALARM_MODE);
        List<ListWashTemplateItemVo> listWashTemplateItemVos = detailsWashTemplateVo.getListWashTemplateItemVos();
        AtomicReference<ListWashTemplateItemVo> washTemplateItemVo = new AtomicReference<>(null);
        listWashTemplateItemVos.forEach(listWashTemplateItemVo -> {
            if (Objects.equals(alarmMode, AlarmMode.URGENT) && Objects.equals(true, listWashTemplateItemVo.getIsUrgent())) {
                washTemplateItemVo.set(listWashTemplateItemVo);
            } else if (Objects.equals(alarmMode, AlarmMode.STANDARD) && Objects.equals(false, listWashTemplateItemVo.getIsUrgent())) {
                washTemplateItemVo.set(listWashTemplateItemVo);
            }
        });
        redisTemplate.opsForValue().set(RedisConstants.WASH_TEMPLATE,detailsWashTemplateVo,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
        return washTemplateItemVo.get();
    }

//    public List<Wash> getWash(Long regionId){
//        if (Objects.isNull(regionId)){
//            return null;
//        }
//        List<Object> objList = redisTemplate.opsForList().range(RedisConstants.REGION_WASH+regionId,0,-1);
//        if (Objects.nonNull(objList) && objList.size()>0){
//            objList.forEach(o -> {
//                if (!(o instanceof Long)){
//                    redisTemplate.delete(RedisConstants.REGION_WASH+regionId);
//                }
//            });
//        }
//        List<Long> list = redisTemplate.opsForList().range(RedisConstants.REGION_WASH+regionId,0,-1);
//        List<Wash> washList = new ArrayList<Wash>();
//        if (Objects.nonNull(list) || list.size() >0 ) {
//            for (Long id : list) {
//                Wash wash = getWashById(id);
//                if (Objects.nonNull(wash)) {
//                    washList.add(wash);
//                }
//            }
//        }
//        list.clear();
//        redisTemplate.delete(RedisConstants.REGION_WASH+regionId);
//        if (washList.size()<=0){
//            washList = washExposeService.find(regionId);
//            washList.forEach(wash -> {
//                redisTemplate.opsForValue().set(RedisConstants.WASH+wash.getId(),wash,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                list.add(wash.getId());
//            });
//            if (list.size()>0){
//                redisTemplate.opsForList().leftPushAll(RedisConstants.REGION_WASH+regionId,list);
//            }
//        }
//        return washList;
//    }
//
//    public Wash getWash(Long regionId,Long userId){
//        if (Objects.isNull(regionId) || Objects.isNull(userId)){
//            return null;
//        }
//        Object obj = redisTemplate.opsForValue().get(RedisConstants.REGION_USER_WASH+regionId+userId);
//        Long washId = null;
//        if (Objects.nonNull(obj) && !(obj instanceof Long)){
//            redisTemplate.delete(RedisConstants.REGION_USER_WASH+regionId+userId);
//            obj = null;
//        }
//        if (Objects.nonNull(obj)){
//            washId = (Long) obj;
//        }
//
//        Wash wash = null;
//        if (Objects.nonNull(washId)) {
//            wash = getWashById(washId);
//        }
//
//        if (Objects.isNull(wash)){
//            wash = washExposeService.find(regionId,userId);
//            if (Objects.nonNull(wash)) {
//                redisTemplate.opsForValue().set(RedisConstants.REGION_USER_WASH+regionId+userId,wash.getId(),RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                redisTemplate.opsForValue().set(RedisConstants.WASH+wash.getId(),wash,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//            }
//        }
//        return wash;
//    }

    public Alarm getAlarm(AlarmClassify alarmClassify, SystemAlarmType code, Integer level){
        if (Objects.isNull(alarmClassify) || Objects.isNull(code)){
            return null;
        }
        final String key = RedisConstants.ALARM_CLASSIFY_CODE + alarmClassify.toString() + code.getKey() + (Objects.nonNull(level) ? level : "");
        Object obj = redisTemplate.opsForValue().get(key);
        Long id =null;
        Alarm alarm = null;
        if (Objects.nonNull(obj) && !(obj instanceof Long )){
            redisTemplate.delete(key);
            obj = null;
        }
        if (Objects.nonNull(obj) ) {
            id = (Long) obj;
        }

        if (Objects.nonNull(id)){
            Object object = redisTemplate.opsForValue().get(RedisConstants.ALARM+id);
            if (Objects.nonNull(object) && !(object instanceof Alarm)){
                redisTemplate.delete(RedisConstants.ALARM+id);
            }else if (Objects.nonNull(object)){
                alarm = (Alarm) object;
            }
        }

        if (Objects.isNull(alarm)){
            alarm = alarmExposeService.find(alarmClassify, code);
            if (Objects.nonNull(alarm)){
                redisTemplate.opsForValue().set(RedisConstants.ALARM+alarm.getId(),alarm, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                if (Objects.isNull(alarm.getLevel())){
                    redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY_CODE+alarm.getClassify().toString()+alarm.getCode().getKey(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY_CODE+alarm.getClassify().toString()+alarm.getCode().getKey()+alarm.getLevel(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }
        return alarm;
    }

    public Assets getAssets(String tagCode){
        if (Objects.isNull(tagCode)) {
            return null;
        }
        Tag tag = this.getTag(tagCode);
        if (Objects.isNull(tag)){
            return null;
        }
        return getAssets(tag.getId());
    }

    public Assets getAssets(Long tagId){
        if (Objects.isNull(tagId)) {
            return null;
        }

        Object assetsId = redisTemplate.opsForValue().get(RedisConstants.TAG_ASSETS+tagId);
        Assets assets = null;

        if (Objects.nonNull(assetsId) && !(assetsId instanceof Long)){
            redisTemplate.delete(RedisConstants.TAG_ASSETS+tagId);
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE+tagId);
            assetsId = null;
        }
        if (Objects.nonNull(assetsId)) {
            assets = (Assets) redisTemplate.opsForValue().get(RedisConstants.ASSETS+(Long)assetsId);
        }

        if (Objects.isNull(assets)){
            assets = assetsExposeService.find(tagId);
            if (Objects.nonNull(assets)) {
                redisTemplate.opsForValue().set(RedisConstants.TAG_ASSETS + tagId, assets.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                redisTemplate.opsForValue().set(RedisConstants.ASSETS + assets.getId(), assets, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                redisTemplate.opsForValue().set(RedisConstants.TAG_BIND_TYPE+tagId, Type.ASSET, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return assets;
    }


    public TagRule getTagRule(Long userId) {
        if (Objects.isNull(userId)) {
            return null;
        }

        Object tagRuleId = redisTemplate.opsForValue().get(RedisConstants.USER_TAG_RULE+userId);
        TagRule tagRule = null;

        if (Objects.nonNull(tagRuleId) && !(tagRuleId instanceof Long)){
            redisTemplate.delete(RedisConstants.USER_TAG_RULE+userId);
            tagRuleId = null;
        }
        if (Objects.nonNull(tagRuleId)) {
            tagRule = (TagRule) redisTemplate.opsForValue().get(RedisConstants.TAG_RULE+(Long)tagRuleId);
        }

        if (Objects.isNull(tagRule)){
            tagRule = tagRuleExposeService.find(userId);
            if (Objects.nonNull(tagRule)) {
                redisTemplate.opsForValue().set(RedisConstants.USER_TAG_RULE + userId, tagRule.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                redisTemplate.opsForValue().set(RedisConstants.TAG_RULE + tagRule.getId(), tagRule, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return tagRule;
    }

    public WardRoomSickbed getWardRoomSickbed(Long id) {
        Object obj = redisTemplate.opsForValue().get(RedisConstants.WARD_ROOM_SICKBED+id);
        if (Objects.nonNull(obj)) {
            if (obj instanceof  WardRoomSickbed) {
                return (WardRoomSickbed) obj;
            }else {
                redisTemplate.delete(RedisConstants.WARD_ROOM_SICKBED+id);
            }
        }
        WardRoomSickbed wardRoomSickbed = wardRoomSickbedExposeService.findById(id);
        if (Objects.nonNull(wardRoomSickbed)) {
            redisTemplate.opsForValue().set(RedisConstants.WARD_ROOM_SICKBED+id,wardRoomSickbed,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
        }
        return wardRoomSickbed;
    }

    public WardRoom getWardRoom(Long id) {
        Object obj = redisTemplate.opsForValue().get(RedisConstants.WARD_ROOM+id);
        if (Objects.nonNull(obj)) {
            if (obj instanceof  WardRoom) {
                return (WardRoom) obj;
            }else {
                redisTemplate.delete(RedisConstants.WARD_ROOM+id);
            }
        }
        WardRoom wardRoom = wardRoomExposeService.findById(id);
        if (Objects.nonNull(wardRoom)) {
            redisTemplate.opsForValue().set(RedisConstants.WARD_ROOM+id,wardRoom,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
        }
        return wardRoom;
    }

    /**
     * 获取宾人所在的区域
     * @param patientId
     * @return
     */
    public Region getPatientRegion(Long patientId) {
        Patient patient = this.getPatient(patientId);
        if (Objects.isNull(patient)) {
            return null;
        }
        WardRoomSickbed wardRoomSickbed = this.getWardRoomSickbed(patient.getSickbedId());
        if (Objects.isNull(wardRoomSickbed)) {
            return null;
        }
        if (Objects.nonNull(wardRoomSickbed.getRegionId())) {
            Region region = this.getRegion(wardRoomSickbed.getRegionId());
            if (Objects.nonNull(region)) {
                return region;
            }
        }

        WardRoom wardRoom = this.getWardRoom(wardRoomSickbed.getWardRoomId());
        if (Objects.isNull(wardRoom)) {
            return null;
        }
        if (Objects.nonNull(wardRoom.getRegionId())) {
            Region region = this.getRegion(wardRoom.getRegionId());
            if (Objects.nonNull(region)) {
                return region;
            }
        }
        return null;
    }


}
