package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.region.Region;
import com.lion.upms.entity.user.User;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

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
    public void position(DeviceDataDto deviceDataDto, User user, Region region) throws JsonProcessingException {
        position(Type.STAFF,user.getId(),region.getId(),null,null,deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Tag tag, Region region) throws JsonProcessingException {
        position(Type.TAG,null,region.getId(),null,tag.getId(),deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    @Override
    public void position(DeviceDataDto deviceDataDto, Assets assets, Region region) throws JsonProcessingException {
        position(Type.DEVICE,null,region.getId(),assets.getId(),null,deviceDataDto.getTime(),deviceDataDto.getSystemDateTime());
    }

    /**
     *
     * @param type
     * @param pi 员工/患者/流动人员id
     * @param ri 区域id
     * @param dvi 设备id(资产)
     * @param ti 标签id
     * @param ddt 设备产生的时间
     * @param sdt 系统接收到的时间
     * @throws JsonProcessingException
     */
    private void position(Type type, Long pi, Long ri, Long dvi, Long ti, LocalDateTime ddt, LocalDateTime sdt) throws JsonProcessingException {
        //记录位置
        Map<String,Object> map = new HashMap<>();
        map.put("typ", type);
        if (Objects.nonNull(pi)) {
            map.put("pi", pi);
        }
        if (Objects.nonNull(ri)) {
            map.put("ri", ri);
        }
        if (Objects.nonNull(dvi)) {
            map.put("dvi", dvi);
        }
        if (Objects.nonNull(ti)) {
            map.put("ti", ti);
        }
        map.put("ddt", ddt);
        map.put("sdt", sdt);
        rocketMQTemplate.syncSend(TopicConstants.POSITION, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(map)).build());
    }
}
