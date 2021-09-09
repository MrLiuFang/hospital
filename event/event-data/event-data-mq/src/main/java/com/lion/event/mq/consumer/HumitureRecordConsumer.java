package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.event.entity.HumitureRecord;
import com.lion.event.mq.consumer.utils.TagCommonUtil;
import com.lion.event.service.SystemAlarmService;
import com.lion.event.service.HumitureRecordService;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午7:57
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.HUMITURE_RECORD,selectorExpression="*",consumerGroup = TopicConstants.HUMITURE_RECORD_CONSUMER_GROUP)
@Log
public class HumitureRecordConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private TagCommonUtil tagCommonUtil;

    @Autowired
    private HumitureRecordService humitureRecordService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            HumitureRecord humitureRecord = jacksonObjectMapper.readValue(msg, HumitureRecord.class);
            HumitureRecord lastData = (HumitureRecord) redisTemplate.opsForValue().get(RedisConstants.LAST_TAG_DATA+humitureRecord.getTi());
            redisTemplate.opsForValue().set(RedisConstants.LAST_TAG_DATA+humitureRecord.getTi(),humitureRecord,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            if (Objects.nonNull(lastData)){
                Type type = Type.instance(humitureRecord.getTyp());
                if (Objects.equals(type,Type.TEMPERATURE) && Objects.equals(lastData.getT(),humitureRecord.getT())) {
                    return;
                }
                if (Objects.equals(type,Type.HUMIDITY) && Objects.equals(lastData.getH(),humitureRecord.getH())) {
                    return;
                }
            }
            humitureRecord = (HumitureRecord) tagCommonUtil.setRegionInfo(humitureRecord);
            humitureRecordService.save(humitureRecord);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
