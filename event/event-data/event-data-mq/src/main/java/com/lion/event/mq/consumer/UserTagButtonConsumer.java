package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.UpdateStateDto;
import com.lion.common.dto.UserTagButtonRecordDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.event.entity.UserTagButtonRecord;
import com.lion.event.service.UserTagButtonRecordService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:24
 */
@Component
@RocketMQMessageListener(topic = TopicConstants.BUTTON_RECORD,selectorExpression="*",consumerGroup = TopicConstants.BUTTON_RECORD_CONSUMER_GROUP)
@Log
public class UserTagButtonConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private UserTagButtonRecordService userTagButtonRecordService;

    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            UserTagButtonRecordDto userTagButtonRecordDto = jacksonObjectMapper.readValue(msg, UserTagButtonRecordDto.class);
            UserTagButtonRecord userTagButtonRecord  = new UserTagButtonRecord();
            BeanUtils.copyProperties(userTagButtonRecordDto,userTagButtonRecord);
            Region region = redisUtil.getRegionById(userTagButtonRecord.getRi());
            if (Objects.nonNull(region)) {
                userTagButtonRecord.setRn(region.getName());
                Build build = redisUtil.getBuild(region.getBuildId());
                if (Objects.nonNull(build)) {
                    userTagButtonRecord.setBui(build.getId());
                    userTagButtonRecord.setBun(build.getName());
                }
                BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    userTagButtonRecord.setBfi(buildFloor.getId());
                    userTagButtonRecord.setBfn(buildFloor.getName());
                }
                Department department = redisUtil.getDepartment(region.getDepartmentId());
                if (Objects.nonNull(department)) {
                    userTagButtonRecord.setDi(department.getId());
                    userTagButtonRecord.setDn(department.getName());
                }
            }
            User user = userExposeService.findById(userTagButtonRecord.getPi());
            if (Objects.nonNull(user)) {
                userTagButtonRecord.setN(user.getName());
            }
            userTagButtonRecordService.add(userTagButtonRecord);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
