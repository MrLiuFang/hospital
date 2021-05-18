package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.tag.Tag;
import com.lion.event.mq.consumer.common.WashCommon;
import com.lion.event.service.CommonService;
import com.lion.event.service.UserWashService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Wash;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/2 上午10:45
 **/
@Service
public class UserWashServiceImpl implements UserWashService {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private CommonService commonService;

    @Autowired
    private WashCommon washCommon;


    @Override
    public void userWashEevent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, User user) throws JsonProcessingException {
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
        String uuid = UUID.randomUUID().toString();
        userWashEevent(user,monitor,star, deviceDataDto,userCurrentRegionDto);

        //判断是否从X区域进入X区域，如果是就进行新的洗手事件监控
        if (Objects.nonNull(userCurrentRegionDto) && userCurrentRegionDto.getCurrentRegionEvent()==1 && !Objects.equals(userCurrentRegionDto.getRegionId(),userCurrentRegionDto.getPreviousRegionId())) {
            List<Wash> list = redisUtil.getWash(userCurrentRegionDto.getRegionId());
            RegionWashMonitorDelayDto regionWashMonitorDelayDto = new RegionWashMonitorDelayDto();
            regionWashMonitorDelayDto.setUserId(user.getId());
            regionWashMonitorDelayDto.setRegionId(userCurrentRegionDto.getRegionId());
            regionWashMonitorDelayDto.setUuid(userCurrentRegionDto.getUuid());
            if (Objects.nonNull(list) && list.size() > 0) {
                list.forEach(wash -> {
                    //如果是全部用户
                    if (wash.getIsAllUser()) {
                        try {
//                            log.info("推送延迟检测命令");
                            rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashMonitorDelayDto)).build());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Wash wash1 = redisUtil.getWash(userCurrentRegionDto.getRegionId(), user.getId());
                        if (Objects.nonNull(wash1)) {
                            try {
//                                log.info("推送延迟检测命令");
                                rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashMonitorDelayDto)).build());
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 记录员工当前位置
     * @param user
     * @param monitorRegion
     * @param starRegion
     * @param deviceDataDto
     * @return
     * @throws JsonProcessingException
     */
    private UserCurrentRegionDto recordUserCurrentRegion(User user, Region monitorRegion, Region starRegion, DeviceDataDto deviceDataDto) throws JsonProcessingException {
        Region region = Objects.isNull(monitorRegion)?starRegion:monitorRegion;
        if (Objects.isNull(region)){
            return null;
        }
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+user.getId());
        String uuid = UUID.randomUUID().toString();
        if (Objects.isNull(userCurrentRegionDto)){
            userCurrentRegionDto  = new UserCurrentRegionDto();
            userCurrentRegionDto.setFirstEntryTime(deviceDataDto.getTime());
            userCurrentRegionDto.setUuid(uuid);
            position(deviceDataDto,user,region);
        }else  if (Objects.nonNull(region) && !Objects.equals(region.getId(),userCurrentRegionDto.getRegionId())) {//判断是否从X区域进入X区域
            userCurrentRegionDto.setFirstEntryTime(deviceDataDto.getTime());
            userCurrentRegionDto.setPreviousRegionId(userCurrentRegionDto.getRegionId());
            userCurrentRegionDto.setWashRecord(null);
            userCurrentRegionDto.setCurrentRegionEvent(0);
            userCurrentRegionDto.setUuid(uuid);
            position(deviceDataDto,user,region);
        }
        userCurrentRegionDto.setCurrentRegionEvent(userCurrentRegionDto.getCurrentRegionEvent()+1);
        userCurrentRegionDto.setUserId(user.getId());
        userCurrentRegionDto.setRegionId(region.getId());
        redisTemplate.opsForValue().set(RedisConstants.USER_CURRENT_REGION+user.getId(),userCurrentRegionDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        return userCurrentRegionDto;
    }

    private void position(DeviceDataDto deviceDataDto,User user, Region region) throws JsonProcessingException {
        //记录位置
        commonService.position(deviceDataDto,user,region.getId());
    }

    /**
     * @param user
     * @param monitor
     * @param star
     * @param deviceDataDto
     */
    private void userWashEevent(User user, Device monitor, Device star, DeviceDataDto deviceDataDto, UserCurrentRegionDto userCurrentRegionDto) throws JsonProcessingException {
        Device device = Objects.isNull(monitor)?star:monitor;
        if (Objects.isNull(device)){
            return;
        }
//        DeviceClassify deviceClassify = device.getDeviceClassify();
        DeviceType deviceType = device.getDeviceType();
        //判断是否是洗手设备发出的事件
        if (Objects.equals(deviceType,DeviceType.ALCOHOL) || Objects.equals(deviceType,DeviceType.DISINFECTANT_GEL)
                || Objects.equals(deviceType,DeviceType.LIQUID_SOAP) || Objects.equals(deviceType,DeviceType.WASHING_FOAM)
                || Objects.equals(deviceType,DeviceType.WATER)) {
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
            userLastWashDto.setSystemDateTime(deviceDataDto.getSystemDateTime());
            userLastWashDto.setDateTime(deviceDataDto.getTime());
            redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);

            //记录用户当前区域的洗手记录
            if (Objects.nonNull(userCurrentRegionDto) ){
                UserCurrentRegionDto.WashRecord washRecord = new UserCurrentRegionDto.WashRecord();
                washRecord.setDateTime(userLastWashDto.getDateTime());
                washRecord.setDeviceId(Objects.isNull(userLastWashDto.getMonitorId())?userLastWashDto.getStarId():userLastWashDto.getMonitorId());
                userCurrentRegionDto.setWashRecord(washRecord);
            }

            //记录洗手
            WashRecordDto washRecordDto = washCommon.init(user.getId(),Objects.isNull(userCurrentRegionDto)?null:userCurrentRegionDto.getRegionId()
                    ,device.getId(),userCurrentRegionDto.getUuid(),deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
            rocketMQTemplate.syncSend(TopicConstants.WASH_RECORD, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(washRecordDto)).build());
        }else {
            //记录洗手时长
            UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
            if (Objects.nonNull(userLastWashDto)){
                Duration duration = Duration.between(userLastWashDto.getDateTime(), LocalDateTime.now());
                userLastWashDto.setTime(Long.valueOf(duration.toMillis()).intValue()/1000);
                redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
    }
}
