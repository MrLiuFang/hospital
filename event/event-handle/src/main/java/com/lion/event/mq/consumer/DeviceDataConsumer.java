package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.utils.RedisUtil;
import com.lion.common.constants.TopicConstants;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dto.RegionWashDelayDto;
import com.lion.event.dto.UserCurrentRegionDto;
import com.lion.event.dto.UserLastWashDto;
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
@RocketMQMessageListener(topic = TopicConstants.EVENT,selectorExpression="*",consumerGroup = TopicConstants.EVENT_GROUP)
@Log
public class DeviceDataConsumer implements RocketMQListener<MessageExt> {


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
            DeviceDataDto deviceDataDto = jacksonObjectMapper.readValue(msg, DeviceDataDto.class);
            Device monitor = null;
            Device star = null;
            Tag tag = null;
            User user = null;
            if (Objects.nonNull(deviceDataDto.getMonitorId())) {
                monitor = redisUtil.getDevice(deviceDataDto.getMonitorId());
            }
            if (Objects.nonNull(deviceDataDto.getStarId())) {
                star = redisUtil.getDevice(deviceDataDto.getStarId());
            }
            if (Objects.nonNull(deviceDataDto.getTagId())) {
                tag = redisUtil.getTag(deviceDataDto.getTagId());
            }
            if (Objects.nonNull(tag)){
                user = redisUtil.getUser(tag.getId());
            }
            if (Objects.nonNull(user)){ //如果根据标签查出用户，进行洗手事件处理
                userWashEevent(deviceDataDto,monitor,star,tag,user);
            }



            if (Objects.isNull(user)) {

            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /**
     * 用户洗手事件处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     * @param user
     */
    private void userWashEevent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, User user){
        Region monitorRegion = null;
        Region starRegion = null;
        if (Objects.nonNull(monitor) && Objects.nonNull(monitor.getId())) {
            monitorRegion = redisUtil.getRegion(monitor.getId());
        }
        if (Objects.nonNull(star) && Objects.nonNull(star.getId())) {
            starRegion = redisUtil.getRegion(star.getId());
        }
        //记录当前用户所在区域
        UserCurrentRegionDto userCurrentRegionDto = recordUserCurrentRegion(user,monitorRegion,starRegion, deviceDataDto);
        userWashEevent(user,monitor,star, deviceDataDto,userCurrentRegionDto);

        //判断是否从X区域进入X区域，如果是就进行新的洗手事件监控
        if (Objects.nonNull(userCurrentRegionDto) && userCurrentRegionDto.getCurrentRegionEvent()==1 && !Objects.equals(userCurrentRegionDto.getRegionId(),userCurrentRegionDto.getPreviousRegionId())) {
            List<Wash> list = redisUtil.getWash(userCurrentRegionDto.getRegionId());
            RegionWashDelayDto regionWashDelayDto = new RegionWashDelayDto();
            regionWashDelayDto.setUserId(user.getId());
            regionWashDelayDto.setRegionId(userCurrentRegionDto.getRegionId());
            if (Objects.nonNull(list) && list.size() > 0) {
                list.forEach(wash -> {
                    //如果是全部用户
                    if (wash.getIsAllUser()) {
                        try {
//                            log.info("推送延迟检测命令");
                            rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashDelayDto)).build());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Wash wash1 = redisUtil.getWash(userCurrentRegionDto.getRegionId(), user.getId());
                        if (Objects.nonNull(wash1)) {
                            try {
//                                log.info("推送延迟检测命令");
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

    private UserCurrentRegionDto recordUserCurrentRegion(User user, Region monitorRegion, Region starRegion, DeviceDataDto deviceDataDto){
        Region region = Objects.isNull(monitorRegion)?starRegion:monitorRegion;
        if (Objects.isNull(region)){
            return null;
        }
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+user.getId());
        if (Objects.isNull(userCurrentRegionDto)){
            userCurrentRegionDto  = new UserCurrentRegionDto();
            userCurrentRegionDto.setFirstEntryTime(deviceDataDto.getTime());
        }else  if (Objects.nonNull(region) && !Objects.equals(region.getId(),userCurrentRegionDto.getRegionId())) {//判断是否从X区域进入X区域
            userCurrentRegionDto.setFirstEntryTime(deviceDataDto.getTime());
            userCurrentRegionDto.setPreviousRegionId(userCurrentRegionDto.getRegionId());
            userCurrentRegionDto.setWashRecord(null);
            userCurrentRegionDto.setCurrentRegionEvent(0);
        }
        userCurrentRegionDto.setCurrentRegionEvent(userCurrentRegionDto.getCurrentRegionEvent()+1);
        userCurrentRegionDto.setUserId(user.getId());
        userCurrentRegionDto.setRegionId(region.getId());
        redisTemplate.opsForValue().set(RedisConstants.USER_CURRENT_REGION+user.getId(),userCurrentRegionDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        return userCurrentRegionDto;
    }

    /**
     * @param user
     * @param monitor
     * @param star
     * @param deviceDataDto
     */
    private void userWashEevent(User user, Device monitor, Device star, DeviceDataDto deviceDataDto, UserCurrentRegionDto userCurrentRegionDto){
        Device device = Objects.isNull(monitor)?star:monitor;
        if (Objects.isNull(device)){
            return;
        }
//        DeviceClassify deviceClassify = device.getDeviceClassify();
        DeviceType deviceType = device.getDeviceType();
        //判断是否是洗手设备发出的事件
        if (Objects.equals(deviceType,DeviceType.ALCOHOL) || Objects.equals(deviceType,DeviceType.DISINFECTANT_GEL)
                || Objects.equals(deviceType,DeviceType.LIQUID_SOAP) || Objects.equals(deviceType,DeviceType.WASHING_FOAM)
                || Objects.equals(deviceType,DeviceType.WATER) ) {
            //记录最后一次洗手事件
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
            userLastWashDto.setMonitorId(Objects.isNull(monitor)?null:monitor.getId());
            userLastWashDto.setStarId(Objects.isNull(star)?null:star.getId());
            userLastWashDto.setDateTime(deviceDataDto.getTime());
            redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);

            //记录用户当前区域的洗手记录
            if (Objects.nonNull(userCurrentRegionDto) ){
                UserCurrentRegionDto.WashRecord washRecord = new UserCurrentRegionDto.WashRecord();
                washRecord.setDateTime(userLastWashDto.getDateTime());
                washRecord.setDeviceId(Objects.isNull(userLastWashDto.getMonitorId())?userLastWashDto.getStarId():userLastWashDto.getMonitorId());
                userCurrentRegionDto.setWashRecord(washRecord);
            }
        }else {
            //记录洗手时长
            UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
            if (Objects.nonNull(userLastWashDto)){
                Duration duration = Duration.between(userLastWashDto.getDateTime(),LocalDateTime.now());
                userLastWashDto.setTime(Long.valueOf(duration.toMillis()).intValue()/1000);
                redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
    }
}
