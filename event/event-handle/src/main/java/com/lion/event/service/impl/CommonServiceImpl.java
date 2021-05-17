package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.PositionDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.manage.entity.assets.Assets;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.swing.text.Position;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    public void position(DeviceDataDto deviceDataDto, User user, Long regionId) throws JsonProcessingException {
        position(Type.STAFF,user.getId(), regionId,null,null,null,deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Tag tag, Long regionId) throws JsonProcessingException {
        if (Objects.equals(deviceDataDto.getTagType(),Type.TEMPERATURE)) {
            position(Type.HUMIDITY, null, regionId, null, tag.getId(), null, deviceDataDto.getTime(), deviceDataDto.getSystemDateTime());
        }else if (Objects.equals(deviceDataDto.getTagType(),Type.TEMPERATURE)) {
            position(Type.TEMPERATURE, null, regionId, null, null, tag.getId(), deviceDataDto.getTime(), deviceDataDto.getSystemDateTime());
        }
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Assets assets, Long regionId) throws JsonProcessingException {
        position(Type.ASSET,null,regionId,assets.getId(),null,null,deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    /**
     *
     * @param type
     * @param pi 员工/患者/流动人员id
     * @param ri 区域id
     * @param adi 设备/资产id(资产)
     * @param thi 标签id(温度)
     * @param tti 标签id
     * @param ddt 设备产生的时间
     * @param sdt 系统接收到的时间
     * @throws JsonProcessingException
     */
    private void position(Type type, Long pi, Long ri, Long adi, Long thi,Long tti, LocalDateTime ddt, LocalDateTime sdt) throws JsonProcessingException {

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
        if (Objects.nonNull(thi)) {
            positionDto.setTi(thi);
        }
        if (Objects.nonNull(tti)) {
            positionDto.setTi(tti);
        }
        positionDto.setDdt(ddt);
        positionDto.setSdt(sdt);

        rocketMQTemplate.syncSend(TopicConstants.POSITION, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(positionDto)).build());
    }
}
