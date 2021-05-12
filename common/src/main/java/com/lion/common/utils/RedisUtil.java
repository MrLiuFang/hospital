package com.lion.common.utils;

import com.lion.common.constants.RedisConstants;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.rule.AlarmExposeService;
import com.lion.manage.expose.rule.WashDeviceExposeService;
import com.lion.manage.expose.rule.WashExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午4:55
 **/
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private DeviceGroupDeviceExposeService deviceGroupDeviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private TagUserExposeService tagUserExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private WashExposeService washExposeService;

    @DubboReference
    private WashDeviceExposeService washDeviceExposeService;

    @DubboReference
    private AlarmExposeService alarmExposeService;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

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
        Object obj = redisTemplate.opsForValue().get(RedisConstants.DEVICE_REGION+deviceId);
        Long regionId = null;
        Region region =null;

        if (Objects.nonNull(obj) && !(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.DEVICE_REGION + deviceId);
        }else if (Objects.nonNull(obj)){
            regionId= (Long) obj;
        }

        if (Objects.nonNull(regionId)){
            region = (Region) redisTemplate.opsForValue().get(RedisConstants.REGION+regionId);
            if (Objects.isNull(region)){
                region = regionExposeService.findById(regionId);
                if (Objects.nonNull(region)){
                    redisTemplate.opsForValue().set(RedisConstants.REGION+region.getId(),region, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }
        if (Objects.isNull(region)){
            DeviceGroupDevice deviceGroupDevice = deviceGroupDeviceExposeService.findByDeviceId(deviceId);
            if (Objects.nonNull(deviceGroupDevice)){
                region = regionExposeService.find(deviceGroupDevice.getDeviceGroupId());
                if (Objects.nonNull(region)){
                    redisTemplate.opsForValue().set(RedisConstants.DEVICE_REGION+deviceId,region.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    redisTemplate.opsForValue().set(RedisConstants.REGION+region.getId(),region, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.delete(RedisConstants.DEVICE_REGION+deviceId);
                    redisTemplate.delete(RedisConstants.REGION+region.getId());
                }
            }
        }
        return region;
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
        }else if (Objects.nonNull(obj)){
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
                    redisTemplate.opsForValue().set(RedisConstants.USER + user.getId(), user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.delete(RedisConstants.TAG_USER + tagId);
//                    redisTemplate.delete(RedisConstants.USER + userId);
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
        }else if (Objects.nonNull(obj)){
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
        }else if (Objects.nonNull(obj)) {
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

    public Tag getTag(String tagCode) {
        if (!StringUtils.hasText(tagCode)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG_CODE+tagCode);
        Tag tag = null;

        if (Objects.nonNull(obj) && !(obj instanceof Tag)){
            redisTemplate.delete(RedisConstants.TAG_CODE+tagCode);
        }else if (Objects.nonNull(obj)) {
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

    //    private List<Wash> getLoopWash(Long userId){
//        List<Wash> list = (List<Wash>) redisTemplate.opsForList().range(RedisConstants.USER_LOOP_WASH+userId,0,-1);
//        if (Objects.isNull(list) || list.size() <=0 ){
//            list = washExposeService.findLoopWash(userId);
//            if (Objects.nonNull(list) && list.size()>0) {
//                redisTemplate.opsForList().leftPushAll(RedisConstants.USER_LOOP_WASH + userId, list);
//                redisTemplate.expire(RedisConstants.USER_LOOP_WASH + userId,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
//            }
//        }
//        return list;
//    }
//

    public List<Device> getWashDevice(Long washId) {
        if (Objects.isNull(washId)){
            return null;
        }
        List<Object> objectList = redisTemplate.opsForList().range(RedisConstants.WASH_DEVICE+washId,0,-1);
        if (Objects.nonNull(objectList) && objectList.size()>0){
            objectList.forEach(o -> {
                if (!(o instanceof Long)){
                    redisTemplate.delete(RedisConstants.WASH_DEVICE+washId);
                }
            });
        }

        List<Long> washDeviceId = redisTemplate.opsForList().range(RedisConstants.WASH_DEVICE+washId,0,-1);
        List<Device> deviceList = new ArrayList<Device>();
        if (Objects.isNull(washDeviceId) || washDeviceId.size()<=0){
            washDeviceId = new ArrayList<Long>();
            List<WashDevice> list = washDeviceExposeService.find(washId);
            if (Objects.nonNull(list) && list.size()>0){
                for (WashDevice washDevice : list){
                    washDeviceId.add(washDevice.getDeviceId());
                }
            }
            if (washDeviceId.size()>0) {
                redisTemplate.opsForList().leftPushAll(RedisConstants.WASH_DEVICE+washId,washDeviceId);
                redisTemplate.expire(RedisConstants.WASH_DEVICE+washId,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        washDeviceId.forEach(id->{
            Device device = deviceExposeService.findById(id);
            if (Objects.nonNull(device)){
                deviceList.add(device);
            }
        });
        return deviceList;
    }

    public List<Wash> getWash(Long regionId){
        if (Objects.isNull(regionId)){
            return null;
        }
        List<Object> objList = redisTemplate.opsForList().range(RedisConstants.REGION_WASH+regionId,0,-1);
        if (Objects.nonNull(objList) && objList.size()>0){
            objList.forEach(o -> {
                if (!(o instanceof Long)){
                    redisTemplate.delete(RedisConstants.REGION_WASH+regionId);
                }
            });
        }
        List<Long> list = redisTemplate.opsForList().range(RedisConstants.REGION_WASH+regionId,0,-1);
        List<Wash> washList = new ArrayList<Wash>();
        if (Objects.nonNull(list) || list.size() >0 ) {
            for (Long id : list) {
                Object object = redisTemplate.opsForValue().get(RedisConstants.WASH + id);
                Wash wash = null;
                if (Objects.nonNull(object) && !(object instanceof Wash )){
                    redisTemplate.delete(RedisConstants.WASH + id);
                }else if (Objects.nonNull(object)){
                    wash = (Wash) object;
                }
                if (Objects.isNull(wash)) {
                    wash = washExposeService.findById(id);
                    if (Objects.nonNull(wash)) {
                        redisTemplate.opsForValue().set(RedisConstants.WASH + id, wash, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                        washList.add(wash);
                    }
                }
            }
        }
        list.clear();
        redisTemplate.delete(RedisConstants.REGION_WASH+regionId);
        if (washList.size()<=0){
            List<Wash> washRegionList = washExposeService.find(regionId);
            washList = washRegionList;
            washRegionList.forEach(wash -> {
                list.add(wash.getId());
            });
            if (list.size()>0){
                redisTemplate.opsForList().leftPushAll(RedisConstants.REGION_WASH+regionId,list,RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return washList;
    }

    public Wash getWash(Long regionId,Long userId){
        if (Objects.isNull(regionId) || Objects.isNull(userId)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.REGION_USER_WASH+regionId+userId);
        Long washId = null;
        if (Objects.nonNull(obj) && !(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.REGION_USER_WASH+regionId+userId);
        }else if (Objects.nonNull(obj)){
            washId = (Long) obj;
        }

        Wash wash = null;
        if (Objects.nonNull(washId)) {
            wash = (Wash) redisTemplate.opsForValue().get(RedisConstants.WASH+washId);
            if (Objects.isNull(wash)){
                wash = washExposeService.findById(washId);
                if (Objects.nonNull(wash)){
                    redisTemplate.opsForValue().set(RedisConstants.WASH+washId,wash,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }

        if (Objects.isNull(wash)){
            wash = washExposeService.find(regionId,userId);
            if (Objects.nonNull(wash)) {
                redisTemplate.opsForValue().set(RedisConstants.REGION_USER_WASH+regionId+userId,wash.getId(),RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                redisTemplate.opsForValue().set(RedisConstants.WASH+wash.getId(),wash,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return wash;
    }

    public Alarm getAlarm(AlarmClassify alarmClassify){
        if (Objects.isNull(alarmClassify)){
            return null;
        }
        Object obj = redisTemplate.opsForValue().get(RedisConstants.ALARM_CLASSIFY+alarmClassify.toString());
        Long id =null;
        Alarm alarm = null;
        if (Objects.nonNull(obj) && !(obj instanceof Long )){
            redisTemplate.delete(RedisConstants.ALARM_CLASSIFY+alarmClassify.toString());
        }else if (Objects.nonNull(obj) ) {
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
            alarm = alarmExposeService.find(alarmClassify);
            if (Objects.nonNull(alarm)){
                redisTemplate.opsForValue().set(RedisConstants.ALARM+alarm.getId(),alarm, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                if (Objects.isNull(alarm.getLevel())){
                    redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY+alarm.getClassify().toString(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY+alarm.getClassify().toString()+alarm.getLevel(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }

        return alarm;

    }


}
