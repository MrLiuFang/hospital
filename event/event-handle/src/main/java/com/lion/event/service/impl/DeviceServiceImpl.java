package com.lion.event.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.enums.TagType;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.DeviceService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    @Override
    public void deviceEevent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag) {
        if (Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH)) {

        }else if (Objects.equals(tag.getPurpose(), TagPurpose.ASSETS)){

        }
    }

    /**
     * 温湿仪时间处理
     * @param deviceDataDto
     * @param monitor
     * @param star
     * @param tag
     */
    private void thermohygrograph(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag) {
        if (Objects.equals(deviceDataDto.getTagType(), TagType.HUMIDITY)) {//湿度仪
            if (Objects.nonNull(tag.getMaxHumidity())) {
                if (deviceDataDto.getHumidity().compareTo(tag.getMaxHumidity()) == 1) {

                }
            }

            if (Objects.nonNull(tag.getMinHumidity())) {
                if (deviceDataDto.getHumidity().compareTo(tag.getMinHumidity()) == -1) {

                }
            }
        }else if (Objects.equals(deviceDataDto.getTagType(), TagType.TEMPERATUE)){//温度仪
            if (Objects.nonNull(tag.getMaxTemperature())) {
                if (deviceDataDto.getTemperature().compareTo(tag.getMaxTemperature()) == 1) {

                }
            }

            if (Objects.nonNull(tag.getMinTemperature())) {
                if (deviceDataDto.getTemperature().compareTo(tag.getMinTemperature()) == -1) {

                }
            }
        }else if (Objects.equals(deviceDataDto.getTagType(), TagType.ASSET)){//资产

        }
    }



}
