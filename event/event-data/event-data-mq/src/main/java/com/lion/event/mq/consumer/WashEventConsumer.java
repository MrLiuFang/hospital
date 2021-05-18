package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.DateTimeFormatterUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.event.entity.WashEvent;
import com.lion.event.entity.WashRecord;
import com.lion.event.mq.consumer.utils.WashCommonUtil;
import com.lion.event.service.WashEventService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.department.DepartmentUserExposeService;
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
@RocketMQMessageListener(topic = TopicConstants.WASH_EVENT,selectorExpression="*",consumerGroup = TopicConstants.WASH_EVENT_CONSUMER_GROUP)
@Log
public class WashEventConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private WashEventService eventService;

    @Autowired
    private RedisUtil redisUtil;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Autowired
    private WashCommonUtil washCommonUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            WashEvent washEvent = jacksonObjectMapper.readValue(msg, WashEvent.class);
            washEvent = (WashEvent) washCommonUtil.setInfo(washEvent);
            eventService.save(washEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
