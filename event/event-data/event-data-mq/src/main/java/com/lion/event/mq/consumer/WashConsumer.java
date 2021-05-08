package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.DateTimeFormatterUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.event.entity.Position;
import com.lion.event.entity.Wash;
import com.lion.event.service.PositionService;
import com.lion.event.service.WashService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午8:50
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.WASH,selectorExpression="*",consumerGroup = TopicConstants.WASH_CONSUMER_GROUP)
@Log
public class WashConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private WashService washService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            Map<String,Object> map = jacksonObjectMapper.readValue(msg, Map.class);
            Wash wash = new Wash();
            wash.setPi(Objects.nonNull(map.get("pi"))?Long.valueOf(String.valueOf(map.get("pi"))):null);
            wash.setDdt(LocalDateTime.parse(String.valueOf(map.get("ddt")), DateTimeFormatter.ofPattern(DateTimeFormatterUtil.pattern(String.valueOf(map.get("ddt"))))));
            wash.setSdt(LocalDateTime.parse(String.valueOf(map.get("sdt")), DateTimeFormatter.ofPattern(DateTimeFormatterUtil.pattern(String.valueOf(map.get("sdt"))))));
            wash.setRi(Objects.nonNull(map.get("ri"))?Long.valueOf(String.valueOf(map.get("ri"))):null);
            wash.setDvi(Objects.nonNull(map.get("dvi"))?Long.valueOf(String.valueOf(map.get("dvi"))):null);

            Device device = redisUtil.getDevice(wash.getDvi());
            if (Objects.nonNull(device)){
                wash.setDvc(device.getCode());
                wash.setDvn(device.getName());
            }

            Region region = redisUtil.getRegionById(wash.getRi());
            if (Objects.nonNull(region)) {
                wash.setRn(region.getName());
                Build build = redisUtil.getBuild(region.getBuildId());
                if (Objects.nonNull(build)) {
                    wash.setBui(build.getId());
                    wash.setBun(build.getName());
                }
                BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    wash.setBfi(buildFloor.getId());
                    wash.setBfn(buildFloor.getName());
                }
                Department department = redisUtil.getDepartment(region.getDepartmentId());
                if (Objects.nonNull(department)) {
                    wash.setDi(department.getId());
                    wash.setDn(department.getName());
                }
            }
            washService.save(wash);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
