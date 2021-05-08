package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.DateTimeFormatterUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.event.entity.Event;
import com.lion.event.service.EventService;
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
 * @Date 2021/5/2 上午10:06
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.EVENT,selectorExpression="*",consumerGroup = TopicConstants.EVENT_CONSUMER_GROUP)
@Log
public class EventConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            Map<String,Object> map = jacksonObjectMapper.readValue(msg, Map.class);
            if (map.containsKey("unalarm") && Objects.equals(true,Boolean.valueOf(String.valueOf(map.get("unalarm"))))) {

            }else {
                Event event = new Event();
                event.setTyp(Integer.valueOf(String.valueOf(map.get("typ"))));
                event.setUi(String.valueOf(map.get("uuid")));
                event.setPi(Objects.nonNull(map.get("pi"))?Long.valueOf(String.valueOf(map.get("pi"))):null);
                event.setSdt(LocalDateTime.parse(String.valueOf(map.get("sdt")), DateTimeFormatter.ofPattern(DateTimeFormatterUtil.pattern(String.valueOf(map.get("sdt"))))));
                event.setIa(Boolean.valueOf(String.valueOf(map.get("ia"))));
                event.setRi(Objects.nonNull(map.get("ri"))?Long.valueOf(String.valueOf(map.get("ri"))):null);
                Region region = redisUtil.getRegionById(event.getRi());
                if (Objects.nonNull(region)) {
                    event.setRn(region.getName());
                    Build build = redisUtil.getBuild(region.getBuildId());
                    if (Objects.nonNull(build)) {
                        event.setBui(build.getId());
                        event.setBun(build.getName());
                    }
                    BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                    if (Objects.nonNull(buildFloor)) {
                        event.setBfi(buildFloor.getId());
                        event.setBfn(buildFloor.getName());
                    }
                    Department department = redisUtil.getDepartment(region.getDepartmentId());
                    if (Objects.nonNull(department)) {
                        event.setDi(department.getId());
                        event.setDn(department.getName());
                    }
                }
                eventService.save(event);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
