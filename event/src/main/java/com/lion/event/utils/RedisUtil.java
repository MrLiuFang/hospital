package com.lion.event.utils;

import com.lion.common.RedisConstants;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.expose.region.impl.RegionExposeService;
import com.lion.manage.expose.rule.WashExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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

    public Region getRegion(Long deviceId){
        Object obj = redisTemplate.opsForValue().get(RedisConstants.DEVICE_REGION+deviceId);
        if (Objects.nonNull(obj) && !(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.DEVICE_REGION + deviceId);
        }

        Region region =null;
        Long regionId = (Long) redisTemplate.opsForValue().get(RedisConstants.DEVICE_REGION+deviceId);
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

    public User getUser(Long tagId){
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG_USER+tagId);
        if (Objects.nonNull(obj) && !(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.TAG_USER + tagId);
        }
        Long userId = (Long) redisTemplate.opsForValue().get(RedisConstants.TAG_USER+tagId);
        User user = null;

        if (Objects.nonNull(userId)){
            user = (User) redisTemplate.opsForValue().get(RedisConstants.USER+userId);
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
                    redisTemplate.opsForValue().set(RedisConstants.USER + userId, user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }else {
                    redisTemplate.delete(RedisConstants.TAG_USER + tagId);
                    redisTemplate.delete(RedisConstants.USER + userId);
                }
            }
        }
        return user;
    }

    public Device getDevice(String code) {
        Object obj = redisTemplate.opsForValue().get(RedisConstants.DEVICE_CODE+code);
        if (Objects.nonNull(obj) && !(obj instanceof Device)){
            redisTemplate.delete(RedisConstants.DEVICE_CODE+code);
        }
        Device device = (Device) redisTemplate.opsForValue().get(RedisConstants.DEVICE_CODE+code);
        if (Objects.isNull(device)){
            device = deviceExposeService.find(code);
            if (Objects.nonNull(device)){
                redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+code,device, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return device;
    }

    public Tag getTag(String tagCode) {
        Object obj = redisTemplate.opsForValue().get(RedisConstants.TAG_CODE+tagCode);
        if (Objects.nonNull(obj) && !(obj instanceof Tag)){
            redisTemplate.delete(RedisConstants.TAG_CODE+tagCode);
        }

        Tag tag = (Tag) redisTemplate.opsForValue().get(RedisConstants.TAG_CODE+tagCode);
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
    public List<Wash> getWash(Long regionId){
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
                Wash wash = (Wash) redisTemplate.opsForValue().get(RedisConstants.WASH + id);
                if (Objects.isNull(wash)) {
                    wash = washExposeService.findById(id);
                    if (Objects.nonNull(wash)) {
                        redisTemplate.opsForValue().set(RedisConstants.WASH + id, wash, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                        washList.add(wash);
                    }
                }
            }
        }
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
        Object obj = redisTemplate.opsForValue().get(RedisConstants.REGION_USER_WASH+regionId+userId);
        if (!(obj instanceof Long)){
            redisTemplate.delete(RedisConstants.REGION_USER_WASH+regionId+userId);
        }
        Long washId = (Long) redisTemplate.opsForValue().get(RedisConstants.REGION_USER_WASH+regionId+userId);
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
                redisTemplate.opsForValue().set(RedisConstants.WASH+washId,wash,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return wash;
    }
}
