package com.lion.event.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.RedisConstants;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.event.dto.EventDto;
import com.lion.event.dto.UserCurrentRegionDto;
import com.lion.event.dto.UserLastWashDto;
import com.lion.event.entity.Event;
import com.lion.event.service.EventService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.WashRegion;
import com.lion.manage.expose.region.impl.RegionExposeService;
import com.lion.manage.expose.rule.WashExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 **/

@Component
@RocketMQMessageListener(topic = "topic",selectorExpression="*",consumerGroup = "event_consumer")
@Log
public class EventConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private EventService eventService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

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

    @SneakyThrows
    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            EventDto eventDto = jacksonObjectMapper.readValue(msg, EventDto.class);
            Device monitor = null;
            Device star = null;
            Tag tag = null;
            User user = null;
            if (Objects.nonNull(eventDto.getMonitorId())) {
                monitor = getDevice(eventDto.getMonitorId());
            }
            if (Objects.nonNull(eventDto.getStarId())) {
                star = getDevice(eventDto.getStarId());
            }
            if (Objects.nonNull(eventDto.getTagId())) {
                tag = getTag(eventDto.getTagId());
            }
            if (Objects.nonNull(tag)){
                user = getUser(tag.getId());
            }
            if (Objects.nonNull(user)){
                userEevent(eventDto,monitor,star,tag,user);
                // TODO: 2021/4/24 将用户放入定时器处理队列
            }

            Event event = new Event();
            event.setUi(Objects.nonNull(user)?user.getId():null);
            event.setBi(eventDto.getButtonId());
            event.setMi(eventDto.getMonitorId());
            event.setMb(eventDto.getMonitorBattery());
            event.setTb(eventDto.getTagBattery());
            event.setSi(eventDto.getStarId());
            event.setTi(eventDto.getTagId());
            event.setW(eventDto.getWarning());
            event.setT(eventDto.getTemperature());
            event.setH(eventDto.getHumidity());
            event.setDt(eventDto.getTime());
            eventService.save(event);

            // 更新设备的电量
            updateDeviceBattery(monitor,eventDto.getMonitorBattery());
            updateTagBattery(tag,eventDto.getTagBattery());

            // TODO: 2021/4/24 处理该事件的tag绑定在患者/资产……的警告
            if (Objects.isNull(user)) {

            }
        }catch (Exception exception){

        }
    }

    /**
     * 用户事件处理
     * @param eventDto
     * @param monitor
     * @param star
     * @param tag
     * @param user
     */
    private void userEevent(EventDto eventDto, Device monitor, Device star, Tag tag, User user){
        Region monitorRegion = null;
        Region starRegion = null;
        if (Objects.nonNull(monitor) && Objects.nonNull(monitor.getId())) {
            monitorRegion = getRegion(monitor.getId());
        }
        if (Objects.nonNull(star) && Objects.nonNull(star.getId())) {
            starRegion = getRegion(star.getId());
        }
        userEevent(user,monitor,star,eventDto);
        UserCurrentRegionDto userCurrentRegionDto = saveUserCurrentRegion(user,monitorRegion,starRegion,eventDto);
        List<Wash> list = getWash(userCurrentRegionDto.getRegionId());
        // TODO: 2021/4/25 删除用户在区域警报循环队列
        if (Objects.nonNull(list) && list.size()>0){
            list.forEach(wash -> {
                if (wash.getIsAllUser()){
                    // TODO: 2021/4/25 将用户推入区域警报循环队列
                }else {
                    Wash wash1 = getWash(userCurrentRegionDto.getRegionId(),user.getId());
                    if (Objects.nonNull(wash1)){
                        // TODO: 2021/4/25 将用户推入区域警报循环队列
                    }
                }
            });
        }
    }

    private void updateDeviceBattery(Device device,Integer battery){
        if (Objects.nonNull(device)){
            if (!Objects.equals(device.getBattery(),battery)){
                deviceExposeService.updateBattery(device.getId(),battery);
            }
        }
    }

    private void updateTagBattery(Tag tag,Integer battery){
        if (Objects.nonNull(tag)){
            if (!Objects.equals(tag.getBattery(),battery)){
                tagExposeService.updateBattery(tag.getId(),battery);
            }
        }
    }

    private UserCurrentRegionDto saveUserCurrentRegion(User user,Region monitorRegion,Region starRegion,EventDto eventDto){
        Region region = Objects.isNull(monitorRegion)?starRegion:monitorRegion;
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+user.getId());
        if (Objects.isNull(userCurrentRegionDto)){
            userCurrentRegionDto  = new UserCurrentRegionDto();
            userCurrentRegionDto.setUserId(user.getId());
            userCurrentRegionDto.setRegionId(region.getId());
            userCurrentRegionDto.setFirstEntryTime(eventDto.getTime());
        }else {
            if (!Objects.equals(region.getId(),userCurrentRegionDto.getRegionId())) {
                userCurrentRegionDto.setRegionId(region.getId());
                userCurrentRegionDto.setFirstEntryTime(eventDto.getTime());
            }
        }
        redisTemplate.opsForValue().set(RedisConstants.USER_CURRENT_REGION+user.getId(),userCurrentRegionDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        return userCurrentRegionDto;
    }

    /**
     * @param user
     * @param monitor
     * @param star
     * @param eventDto
     */
    private void userEevent(User user,Device monitor, Device star,EventDto eventDto){
        Device device = Objects.isNull(monitor)?star:monitor;
//        DeviceClassify deviceClassify = device.getDeviceClassify();
        DeviceType deviceType = device.getDeviceType();
        if (Objects.equals(deviceType,DeviceType.ALCOHOL) || Objects.equals(deviceType,DeviceType.DISINFECTANT_GEL)
                || Objects.equals(deviceType,DeviceType.LIQUID_SOAP) || Objects.equals(deviceType,DeviceType.WASHING_FOAM)
                || Objects.equals(deviceType,DeviceType.WATER) ) {
            UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
            if (Objects.isNull(userLastWashDto)) {
                userLastWashDto = new UserLastWashDto();
            }else {
                UserLastWashDto previous = new UserLastWashDto();
                BeanUtils.copyProperties(userLastWashDto,previous);
                previous.setPrevious(null);
                userLastWashDto.setPrevious(previous);
            }
            userLastWashDto.setUserId(user.getId());
            userLastWashDto.setMonitor(monitor);
            userLastWashDto.setStar(star);
            userLastWashDto.setDateTime(eventDto.getTime());
            redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }else {
            UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
            if (Objects.nonNull(userLastWashDto)){
                Duration duration = Duration.between(LocalDateTime.now(),userLastWashDto.getDateTime());
                userLastWashDto.setTime(Long.valueOf(duration.toMillis()).intValue()/1000);
                redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
    }

    private Region getRegion(Long deviceId){
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

    private User getUser(Long tagId){
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

    private Device getDevice(String code) {
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

    private Tag getTag(String tagCode) {
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
    private List<Wash> getWash(Long regionId){
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

    private Wash getWash(Long regionId,Long userId){
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
