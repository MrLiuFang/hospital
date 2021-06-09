package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.PositionDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.region.Region;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午2:43
 **/
@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public void position(DeviceDataDto deviceDataDto, User user, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.STAFF,user.getId(), regionId,null,Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Patient patient, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.PATIENT,patient.getId(), regionId,null,Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, TemporaryPerson temporaryPerson, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.MIGRANT,temporaryPerson.getId(), regionId,null,Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Tag tag, Long regionId) throws JsonProcessingException {
        if (Objects.equals(deviceDataDto.getTagType(),Type.HUMIDITY)) {
            position(Type.HUMIDITY, null, regionId, null, tag.getId(), deviceDataDto.getTime(), deviceDataDto.getSystemDateTime());
        }else if (Objects.equals(deviceDataDto.getTagType(),Type.TEMPERATURE)) {
            position(Type.TEMPERATURE, null, regionId, null, Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(), deviceDataDto.getSystemDateTime());
        }
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Assets assets, Long regionId, Tag tag) throws JsonProcessingException {
        position(Type.ASSET,null,regionId,assets.getId(),Objects.nonNull(tag)?tag.getId():null, deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    @Override
    public CurrentRegionDto currentRegion(Device monitor, Device star) {
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
        CurrentRegionDto currentRegionDto = new CurrentRegionDto();
        currentRegionDto.setRegionId(region.getId());
        return currentRegionDto;
    }

    /**
     *
     * @param type
     * @param pi 员工/患者/流动人员id
     * @param ri 区域id
     * @param adi 设备/资产id(资产)
     * @param ti 标签id(温度)
     * @param ddt 设备产生的时间
     * @param sdt 系统接收到的时间
     * @throws JsonProcessingException
     */
    private void position(Type type, Long pi, Long ri, Long adi, Long ti, LocalDateTime ddt, LocalDateTime sdt) throws JsonProcessingException {

        PositionDto positionDto = new PositionDto();
        positionDto.setTyp(type.getKey());
        if (Objects.nonNull(pi)) {
            positionDto.setPi(pi);
        }
        if (Objects.nonNull(ri)) {
            positionDto.setRi(ri);
        }
        if (Objects.nonNull(adi)) {
            positionDto.setAdi(adi);
        }
        if (Objects.nonNull(ti)) {
            positionDto.setTi(ti);
        }
        positionDto.setDdt(ddt);
        positionDto.setSdt(sdt);

        rocketMQTemplate.syncSend(TopicConstants.POSITION, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(positionDto)).build());
    }
}
