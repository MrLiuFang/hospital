package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.utils.DateTimeFormatterUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.event.entity.WashEvent;
import com.lion.event.entity.WashRecord;
import com.lion.event.mq.consumer.utils.WashCommonUtil;
import com.lion.event.service.WashRecordService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.upms.entity.user.User;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.geom.PathIterator;
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
@RocketMQMessageListener(topic = TopicConstants.WASH_RECORD,selectorExpression="*",consumerGroup = TopicConstants.WASH_RECORD_CONSUMER_GROUP)
@Log
public class WashRecordConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private WashRecordService washService;

    @Autowired
    private WashCommonUtil washCommonUtil;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            WashRecord washRecord = jacksonObjectMapper.readValue(msg, WashRecord.class);
            washRecord = (WashRecord) washCommonUtil.setInfo(washRecord);
            washService.save(washRecord);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
