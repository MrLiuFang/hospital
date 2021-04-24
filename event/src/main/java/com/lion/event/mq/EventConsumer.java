package com.lion.event.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.ResdisConstants;
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
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.Wash;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
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
            Event event = new Event();
            event.setUi(123456789123456789L);
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
            Duration duration = Duration.between(LocalDateTime.now(),event.getDt());
            if (duration.toMinutes()>=10){
                return;
            }

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
            if (Objects.nonNull(tag)){
                user = getUser(tag.getId());
            }
            if (Objects.nonNull(user)){
                userEevent(eventDto,monitor,star,tag,user);
                // TODO: 2021/4/24 将用户放入定时器处理队列
            }

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
        saveUserCurrentRegion(user,monitorRegion,starRegion,eventDto);
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

    private void saveUserCurrentRegion(User user,Region monitorRegion,Region starRegion,EventDto eventDto){
        Region region = Objects.isNull(monitorRegion)?starRegion:monitorRegion;
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(ResdisConstants.USER_CURRENT_REGION+user.getId());
        if (Objects.isNull(userCurrentRegionDto)){
            userCurrentRegionDto  = new UserCurrentRegionDto();
            userCurrentRegionDto.setUserId(user.getId());
            userCurrentRegionDto.setRegion(region);
            userCurrentRegionDto.setFirstEntryTime(eventDto.getTime());
        }else {
            if (!Objects.equals(region,userCurrentRegionDto.getRegion())) {
                userCurrentRegionDto.setRegion(region);
                userCurrentRegionDto.setFirstEntryTime(eventDto.getTime());
            }
        }
        redisTemplate.opsForValue().set(ResdisConstants.USER_CURRENT_REGION+user.getId(),userCurrentRegionDto,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    /**
     * @param user
     * @param monitor
     * @param star
     * @param eventDto
     */
    private void userEevent(User user,Device monitor, Device star,EventDto eventDto){
        Device device = Objects.isNull(monitor)?star:monitor;
        DeviceClassify deviceClassify = device.getDeviceClassify();
        DeviceType deviceType = device.getDeviceType();
        if (Objects.equals(deviceType,DeviceType.ALCOHOL) || Objects.equals(deviceType,DeviceType.DISINFECTANT_GEL)
                || Objects.equals(deviceType,DeviceType.LIQUID_SOAP) || Objects.equals(deviceType,DeviceType.WASHING_FOAM)
                || Objects.equals(deviceType,DeviceType.WATER) ) {
            UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(ResdisConstants.USER_LAST_WASH+user.getId());
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
            redisTemplate.opsForValue().set(ResdisConstants.USER_LAST_WASH+user.getId(),userLastWashDto,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }else {
            UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(ResdisConstants.USER_LAST_WASH+user.getId());
            if (Objects.nonNull(userLastWashDto)){
                Duration duration = Duration.between(LocalDateTime.now(),userLastWashDto.getDateTime());
                userLastWashDto.setTime(Long.valueOf(duration.toMillis()).intValue()/1000);
                redisTemplate.opsForValue().set(ResdisConstants.USER_LAST_WASH+user.getId(),userLastWashDto,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
    }

    private Region getRegion(Long deviceId){
        Region region = (Region) redisTemplate.opsForValue().get(ResdisConstants.DEVICE_REGION+deviceId);
        if (Objects.isNull(region)){
            DeviceGroupDevice deviceGroupDevice = deviceGroupDeviceExposeService.findByDeviceId(deviceId);
            if (Objects.nonNull(deviceGroupDevice)){
                region = regionExposeService.find(deviceGroupDevice.getId());
                if (Objects.nonNull(region)){
                    redisTemplate.opsForValue().set(ResdisConstants.DEVICE_REGION+deviceId,region,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            }
        }
        return region;
    }

    private User getUser(Long tagId){
        User user = (User) redisTemplate.opsForValue().get(ResdisConstants.TAG_USER+tagId);
        if (Objects.isNull(user)){
            TagUser tagUser = tagUserExposeService.find(tagId);
            if (Objects.nonNull(tagUser)){
                user = userExposeService.findById(tagUser.getUserId());
                redisTemplate.opsForValue().set(ResdisConstants.TAG_USER+tagId,user,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return user;
    }

    private Device getDevice(String code) {
        Device device = (Device) redisTemplate.opsForValue().get(ResdisConstants.DEVICE_CODE+code);
        if (Objects.isNull(device)){
            device = deviceExposeService.find(code);
            redisTemplate.opsForValue().set(ResdisConstants.DEVICE_CODE+code,device,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
        return device;
    }

    private Tag getTag(String code) {
        Tag tag = (Tag) redisTemplate.opsForValue().get(ResdisConstants.TAG_CODE+code);
        if (Objects.isNull(tag)){
            tag = tagExposeService.find(code);
            redisTemplate.opsForValue().set(ResdisConstants.TAG_CODE+code,tag,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
        return tag;
    }

    private List<Wash> getLoopWash(Long userId){
        List<Wash> list = (List<Wash>) redisTemplate.opsForList().range(ResdisConstants.USER_LOOP_WASH+userId,0,-1);
        if (Objects.isNull(list) || list.size() <=0 ){
            list = washExposeService.findLoopWash(userId);
            if (Objects.nonNull(list) && list.size()>0) {
                redisTemplate.opsForList().leftPushAll(ResdisConstants.USER_LOOP_WASH + userId, list);
                redisTemplate.expire(ResdisConstants.USER_LOOP_WASH + userId,ResdisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return list;
    }

    private List<Wash> getWash(Long regionId){
        List<Wash> list = (List<Wash>) redisTemplate.opsForList().range(ResdisConstants.REGION_WASH+regionId,0,-1);
        if (Objects.isNull(list) || list.size() <=0 ){
            list = washExposeService.find(regionId);
            if (Objects.nonNull(list) && list.size()>0) {
                redisTemplate.opsForList().leftPushAll(ResdisConstants.REGION_WASH + regionId, list);
                redisTemplate.expire(ResdisConstants.REGION_WASH + regionId,ResdisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            }
        }
        return list;
    }

    private Wash getWash(Long regionId,Long userId){
        Wash wash = (Wash) redisTemplate.opsForValue().get(ResdisConstants.REGION_USER_WASH+regionId+userId);
        if (Objects.isNull(wash)){
            wash = washExposeService.find(regionId,userId);
            if (Objects.nonNull(wash)) {
                redisTemplate.opsForValue().set(ResdisConstants.REGION_USER_WASH+regionId+userId,wash,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
        return wash;
    }
}