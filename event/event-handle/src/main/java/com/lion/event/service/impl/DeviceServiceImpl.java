package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.event.service.DeviceService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.region.Region;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 上午10:53
 **/
@Service
public class DeviceServiceImpl implements DeviceService {

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

    @Override
    public void deviceEevent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag) throws JsonProcessingException {
        if (Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH) && (Objects.equals(deviceDataDto.getTagType(),Type.HUMIDITY) || Objects.equals(deviceDataDto.getTagType(),Type.TEMPERATURE)) ) {
            thermohygrograph(deviceDataDto,monitor,star,tag);
        }else if (Objects.equals(tag.getPurpose(), TagPurpose.ASSETS) && Objects.equals(deviceDataDto.getTagType(),Type.ASSET) ){
            assets(deviceDataDto,monitor,star,tag);
        }
    }

    /**
     * 资产处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     */
    private void assets(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag) throws JsonProcessingException {
        AssetsCurrentRegionDto assetsCurrentRegionDto = new AssetsCurrentRegionDto();
        CurrentRegionDto currentRegionDto = currentRegion(monitor,star);
        if (Objects.nonNull(currentRegionDto)) {
            BeanUtils.copyProperties(currentRegionDto,assetsCurrentRegionDto);
            Assets assets = redisUtil.getAssets(tag.getId());
            assetsCurrentRegionDto.setAssetsId(assets.getId());
            commonService.position(deviceDataDto,assets,currentRegionDto.getRegionId());
        }
    }

    /**
     * 温湿仪事件处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     */
    private void thermohygrograph(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag) throws JsonProcessingException {
        TagCurrentRegionDto tagCurrentRegionDto = new TagCurrentRegionDto();
        CurrentRegionDto currentRegionDto = currentRegion(monitor,star);
        if (Objects.nonNull(currentRegionDto)) {
            BeanUtils.copyProperties(currentRegionDto,tagCurrentRegionDto);
            tagCurrentRegionDto.setTagId(tag.getId());
            commonService.position(deviceDataDto,tag,currentRegionDto.getRegionId());
        }
        TagRecordDto tagRecordDto = tagRecord(tag,currentRegionDto,deviceDataDto);
        if (Objects.equals(deviceDataDto.getTagType(), Type.HUMIDITY)) {//湿度仪
            if (Objects.nonNull(tag.getMaxHumidity())) {
                if (deviceDataDto.getHumidity().compareTo(tag.getMaxHumidity()) == 1) {
                    SystemAlarm(Type.HUMIDITY,tag,null,SystemAlarmType.SDGG);
                    tagEvent(tagRecordDto,tag,deviceDataDto,SystemAlarmType.SDGG);
                }
            }

            if (Objects.nonNull(tag.getMinHumidity())) {
                if (deviceDataDto.getHumidity().compareTo(tag.getMinHumidity()) == -1) {
                    SystemAlarm(Type.HUMIDITY,tag,null,SystemAlarmType.SDGD);
                    tagEvent(tagRecordDto,tag,deviceDataDto,SystemAlarmType.SDGD);
                }
            }
        }else if (Objects.equals(deviceDataDto.getTagType(), Type.TEMPERATURE)){//温度仪
            if (Objects.nonNull(tag.getMaxTemperature())) {
                if (deviceDataDto.getTemperature().compareTo(tag.getMaxTemperature()) == 1) {
                    SystemAlarm(Type.TEMPERATURE,tag,null,SystemAlarmType.WDGG);
                    tagEvent(tagRecordDto,tag,deviceDataDto,SystemAlarmType.WDGG);
                }
            }
            if (Objects.nonNull(tag.getMinTemperature())) {
                if (deviceDataDto.getTemperature().compareTo(tag.getMinTemperature()) == -1) {
                    SystemAlarm(Type.TEMPERATURE,tag,null,SystemAlarmType.WDGD);
                    tagEvent(tagRecordDto,tag,deviceDataDto,SystemAlarmType.WDGD);
                }
            }
        }
    }

    private void tagEvent(TagRecordDto tagRecordDto,Tag tag,DeviceDataDto deviceDataDto,SystemAlarmType systemAlarmType) throws JsonProcessingException {
        TagEventDto tagEventDto = new TagEventDto();
        BeanUtils.copyProperties(tagRecordDto,tagEventDto);
        tagEventDto.setAt(systemAlarmType.getKey());
        tagEventDto.setMxh(tag.getMaxHumidity());
        tagEventDto.setMih(tag.getMinHumidity());
        tagEventDto.setMxt(tag.getMaxTemperature());
        tagEventDto.setMit(tag.getMinTemperature());
        rocketMQTemplate.syncSend(TopicConstants.TAG_EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(tagEventDto)).build());
    }

    private TagRecordDto tagRecord(Tag tag,CurrentRegionDto currentRegionDto,DeviceDataDto deviceDataDto) throws JsonProcessingException {
        TagRecordDto tagRecordDto = new TagRecordDto();
        tagRecordDto.setRi(currentRegionDto.getRegionId());
        tagRecordDto.setTyp(deviceDataDto.getTagType().getKey());
        tagRecordDto.setTi(tag.getId());
        if (Objects.nonNull(deviceDataDto.getTemperature())) {
            tagRecordDto.setT(deviceDataDto.getTemperature());
        }
        if (Objects.nonNull(deviceDataDto.getHumidity())) {
            tagRecordDto.setH(deviceDataDto.getHumidity());
        }
        rocketMQTemplate.syncSend(TopicConstants.TAG_RECORD, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(tagRecordDto)).build());
        return tagRecordDto;
    }

    private void SystemAlarm(Type type, Tag tag, Assets assets, SystemAlarmType systemAlarmType) throws JsonProcessingException {
        //系统内警告
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(type);
        if (Objects.nonNull(tag)){
            systemAlarmDto.setTagId(tag.getId());
        }
        if (Objects.nonNull(assets)){
            systemAlarmDto.setAssetsId(assets.getId());
        }
        systemAlarmDto.setSystemAlarmType(systemAlarmType);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setUuid(UUID.randomUUID().toString());
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
    }

    /**
     * 获取当前位置
     * @param monitor
     * @param star
     * @return
     * @throws JsonProcessingException
     */
    private CurrentRegionDto currentRegion(Device monitor, Device star) {
        Region monitorRegion = null;
        Region starRegion = null;
        if (Objects.nonNull(monitor) && Objects.nonNull(monitor.getId())) {
            monitorRegion = redisUtil.getRegion(monitor.getId());
        }
        if (Objects.nonNull(star) && Objects.nonNull(star.getId())) {
            starRegion = redisUtil.getRegion(star.getId());
        }
        Region region = Objects.isNull(monitorRegion)?starRegion:monitorRegion;
        if (Objects.isNull(region)){
            return null;
        }
        CurrentRegionDto currentRegionDto = new UserCurrentRegionDto();
        currentRegionDto.setRegionId(region.getId());
        return currentRegionDto;
    }

}
