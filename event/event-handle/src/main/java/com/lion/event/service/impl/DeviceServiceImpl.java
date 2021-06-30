package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.*;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.event.service.DeviceService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.expose.assets.AssetsBorrowExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CommonService commonService;

    @DubboReference
    private AssetsBorrowExposeService assetsBorrowExposeService;

    @Override
    public void deviceEevent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag) throws JsonProcessingException {
        if (Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH)  ) {
            thermohygrograph(deviceDataDto,monitor,star,tag);
        }else if (Objects.equals(tag.getPurpose(), TagPurpose.ASSETS) ){
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
        CurrentRegionDto currentRegionDto = commonService.currentRegion(monitor,star);
        Assets assets = redisUtil.getAssets(tag.getId());
        if (Objects.isNull(assets) || Objects.isNull(currentRegionDto)) {
            return;
        }
        if (Objects.nonNull(currentRegionDto)) {
            commonService.position(deviceDataDto,assets,currentRegionDto.getRegionId(), tag);
        }
        if (!Objects.equals(assets.getRegionId(),currentRegionDto.getRegionId())) {
            AssetsBorrow assetsBorrow = assetsBorrowExposeService.findNotReturn(assets.getId());
            if (Objects.isNull(assetsBorrow)) {
                systemAlarm(Type.ASSET,tag,assets,SystemAlarmType.WSQCCSSQY,currentRegionDto,deviceDataDto );
            }
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
        CurrentRegionDto currentRegionDto = commonService.currentRegion(monitor, star);
        if (Objects.nonNull(currentRegionDto)) {
            commonService.position(deviceDataDto,tag,currentRegionDto.getRegionId());
        }
        humitureRecord(tag,currentRegionDto,deviceDataDto);
        if (Objects.nonNull(tag.getMaxHumidity()) && Objects.nonNull(deviceDataDto.getHumidity())) {
            if (deviceDataDto.getHumidity().compareTo(tag.getMaxHumidity()) == 1) {
                systemAlarm(Type.HUMIDITY,tag,null,SystemAlarmType.SDGDG,currentRegionDto,deviceDataDto );
            }
        }
        if (Objects.nonNull(tag.getMinHumidity()) && Objects.nonNull(deviceDataDto.getHumidity())) {
            if (deviceDataDto.getHumidity().compareTo(tag.getMinHumidity()) == -1) {
                systemAlarm(Type.HUMIDITY,tag,null,SystemAlarmType.SDGDG,currentRegionDto,deviceDataDto );
            }
        }
        if (Objects.nonNull(tag.getMaxTemperature()) && Objects.nonNull(deviceDataDto.getTemperature())) {
            if (deviceDataDto.getTemperature().compareTo(tag.getMaxTemperature()) == 1) {
                systemAlarm(Type.HUMIDITY,tag,null,SystemAlarmType.WDGDG,currentRegionDto,deviceDataDto );
            }
        }
        if (Objects.nonNull(tag.getMinTemperature()) && Objects.nonNull(deviceDataDto.getTemperature())) {
            if (deviceDataDto.getTemperature().compareTo(tag.getMinTemperature()) == -1) {
                systemAlarm(Type.HUMIDITY,tag,null,SystemAlarmType.WDGDG,currentRegionDto,deviceDataDto );
            }
        }
    }


    private HumitureRecordDto humitureRecord(Tag tag, CurrentRegionDto currentRegionDto, DeviceDataDto deviceDataDto) throws JsonProcessingException {
        HumitureRecordDto humitureRecordDto = new HumitureRecordDto();
        humitureRecordDto.setRi(Objects.nonNull(currentRegionDto)?currentRegionDto.getRegionId():null);
        humitureRecordDto.setTyp(deviceDataDto.getTagType().getKey());
        humitureRecordDto.setDdt(deviceDataDto.getTime());
        humitureRecordDto.setSdt(deviceDataDto.getSystemDateTime());
        humitureRecordDto.setTi(tag.getId());
        if (Objects.nonNull(deviceDataDto.getTemperature())) {
            humitureRecordDto.setT(deviceDataDto.getTemperature());
        }
        if (Objects.nonNull(deviceDataDto.getHumidity())) {
            humitureRecordDto.setH(deviceDataDto.getHumidity());
        }
        rocketMQTemplate.syncSend(TopicConstants.HUMITURE_RECORD, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(humitureRecordDto)).build());
        return humitureRecordDto;
    }

    private void systemAlarm(Type type, Tag tag, Assets assets, SystemAlarmType systemAlarmType,CurrentRegionDto currentRegionDto,DeviceDataDto deviceDataDto) throws JsonProcessingException {
        //系统内警告
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(type);
        if (Objects.nonNull(tag)){
            systemAlarmDto.setTagId(tag.getId());
            if (Objects.nonNull(deviceDataDto.getTemperature())) {
                systemAlarmDto.setTemperature(deviceDataDto.getTemperature());
            }
            if (Objects.nonNull(deviceDataDto.getHumidity())) {
                systemAlarmDto.setHumidity(deviceDataDto.getHumidity());
            }
        }
        if (Objects.nonNull(assets)){
            systemAlarmDto.setAssetsId(assets.getId());
        }
        systemAlarmDto.setSystemAlarmType(systemAlarmType);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setRegionId(Objects.isNull(systemAlarmDto)?null:currentRegionDto.getRegionId());
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
    }

}
