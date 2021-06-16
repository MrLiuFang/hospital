package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.RedisUtil;
import com.lion.event.entity.Position;
import com.lion.event.entity.RecyclingBoxRecord;
import com.lion.event.service.PositionService;
import com.lion.event.service.RecyclingBoxRecordService;
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

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:59
 */
@Component
@RocketMQMessageListener(topic = TopicConstants.RECYCLING_BOX,selectorExpression="*",consumerGroup = TopicConstants.RECYCLING_BOX_CONSUMER_GROUP)
@Log
public class RecyclingBoxConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RecyclingBoxRecordService recyclingBoxRecordService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            RecyclingBoxRecord recyclingBoxRecord = jacksonObjectMapper.readValue(msg, RecyclingBoxRecord.class);
            Region region = redisUtil.getRegionById(recyclingBoxRecord.getRi());
            if (Objects.nonNull(region)) {
                recyclingBoxRecord.setRn(region.getName());
                Build build = redisUtil.getBuild(region.getBuildId());
                if (Objects.nonNull(build)) {
                    recyclingBoxRecord.setBui(build.getId());
                    recyclingBoxRecord.setBun(build.getName());
                }
                BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    recyclingBoxRecord.setBfi(buildFloor.getId());
                    recyclingBoxRecord.setBfn(buildFloor.getName());
                }
                Department department = redisUtil.getDepartment(region.getDepartmentId());
                if (Objects.nonNull(department)) {
                    recyclingBoxRecord.setDi(department.getId());
                    recyclingBoxRecord.setDn(department.getName());
                }
            }
            recyclingBoxRecordService.save(recyclingBoxRecord);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
