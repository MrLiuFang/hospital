package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.DateTimeFormatterUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.event.entity.Position;
import com.lion.event.service.PositionService;
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
 * @Date 2021/5/2 上午10:17
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.POSITION,selectorExpression="*",consumerGroup = TopicConstants.POSITION_CONSUMER_GROUP)
@Log
public class PositionConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private PositionService positionService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            Position position = jacksonObjectMapper.readValue(msg, Position.class);
            Region region = redisUtil.getRegionById(position.getRi());
            if (Objects.nonNull(region)) {
                position.setRn(region.getName());
                Build build = redisUtil.getBuild(region.getBuildId());
                if (Objects.nonNull(build)) {
                    position.setBui(build.getId());
                    position.setBun(build.getName());
                }
                BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    position.setBfi(buildFloor.getId());
                    position.setBfn(buildFloor.getName());
                }
                Department department = redisUtil.getDepartment(region.getDepartmentId());
                if (Objects.nonNull(department)) {
                    position.setDi(department.getId());
                    position.setDn(department.getName());
                }
            }
            positionService.save(position);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
