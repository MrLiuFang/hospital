package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.RedisConstants;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.constant.TopicConstants;
import com.lion.event.dto.EventDto;
import com.lion.event.dto.RegionWashDelayDto;
import com.lion.event.dto.UserCurrentRegionDto;
import com.lion.event.dto.UserLastWashDto;
import com.lion.event.entity.Event;
import com.lion.event.service.EventService;
import com.lion.event.utils.RedisUtil;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Wash;
import com.lion.upms.entity.user.User;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 **/

@Component
@RocketMQMessageListener(topic = TopicConstants.EVENT,selectorExpression="*",consumerGroup = TopicConstants.EVENT)
@Log
public class EventConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private EventService eventService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @Autowired
    private RedisUtil redisUtil;

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
                monitor = redisUtil.getDevice(eventDto.getMonitorId());
            }
            if (Objects.nonNull(eventDto.getStarId())) {
                star = redisUtil.getDevice(eventDto.getStarId());
            }
            if (Objects.nonNull(eventDto.getTagId())) {
                tag = redisUtil.getTag(eventDto.getTagId());
            }
            if (Objects.nonNull(tag)){
                user = redisUtil.getUser(tag.getId());
            }
            if (Objects.nonNull(user)){
                userEevent(eventDto,monitor,star,tag,user);
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

            if (Objects.isNull(user)) {

            }
        }catch (Exception exception){
            exception.printStackTrace();
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
            monitorRegion = redisUtil.getRegion(monitor.getId());
        }
        if (Objects.nonNull(star) && Objects.nonNull(star.getId())) {
            starRegion = redisUtil.getRegion(star.getId());
        }
        userEevent(user,monitor,star,eventDto);
        UserCurrentRegionDto userCurrentRegionDto = saveUserCurrentRegion(user,monitorRegion,starRegion,eventDto);
        if (Objects.isNull(userCurrentRegionDto.getPreviousRegionId()) || !Objects.equals(userCurrentRegionDto.getRegionId(),userCurrentRegionDto.getPreviousRegionId())) {
            List<Wash> list = redisUtil.getWash(userCurrentRegionDto.getRegionId());
            RegionWashDelayDto regionWashDelayDto = new RegionWashDelayDto();
            regionWashDelayDto.setUserId(user.getId());
            regionWashDelayDto.setRegionId(userCurrentRegionDto.getRegionId());
            if (Objects.nonNull(list) && list.size() > 0) {
                list.forEach(wash -> {
                    if (wash.getIsAllUser()) {
                        try {
                            rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDelayDto)).build());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Wash wash1 = redisUtil.getWash(userCurrentRegionDto.getRegionId(), user.getId());
                        if (Objects.nonNull(wash1)) {
                            try {
                                rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDelayDto)).build());
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
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
                userCurrentRegionDto.setPreviousRegionId(userCurrentRegionDto.getRegionId());
                userCurrentRegionDto.setRegionId(region.getId());
                userCurrentRegionDto.setFirstEntryTime(eventDto.getTime());
                userCurrentRegionDto.setWashRecord(null);
            }else {
                UserCurrentRegionDto.WashRecord washRecord = new UserCurrentRegionDto.WashRecord();
                UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
                if (Objects.nonNull(userLastWashDto)){
                    washRecord.setDateTime(userLastWashDto.getDateTime());
                    washRecord.setDeviceId(Objects.isNull(userLastWashDto.getMonitorId())?userLastWashDto.getStarId():userLastWashDto.getMonitorId());
                    userCurrentRegionDto.setWashRecord(washRecord);
                }
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
            userLastWashDto.setMonitorId(monitor.getId());
            userLastWashDto.setStarId(star.getId());
            userLastWashDto.setDateTime(eventDto.getTime());
            redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }else {
            UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
            if (Objects.nonNull(userLastWashDto)){
                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
                userLastWashDto.setTime(Long.valueOf(duration.toMillis()).intValue()/1000);
                redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
    }
}
