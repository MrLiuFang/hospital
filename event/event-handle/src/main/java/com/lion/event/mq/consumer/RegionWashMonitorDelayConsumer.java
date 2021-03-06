package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.RegionWashMonitorDelayDto;
import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.utils.MessageDelayUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.vo.ListWashTemplateItemVo;
import lombok.extern.java.Log;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //洗手监控延迟检测
 * @Date 2021/4/25 下午3:52
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.REGION_WASH_DELAY,selectorExpression="*",consumerGroup = TopicConstants.REGION_WASH_DELAY_CONSUMER_GROUP)
@Log
public class RegionWashMonitorDelayConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {

            byte[] body = messageExt.getBody();
            String msg = new String(body);
            RegionWashMonitorDelayDto regionWashMonitorDelayDto = jacksonObjectMapper.readValue(msg, RegionWashMonitorDelayDto.class);
            if (Objects.nonNull(regionWashMonitorDelayDto)) {
                UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION + regionWashMonitorDelayDto.getUserId());
                if (Objects.nonNull(userCurrentRegionDto)) {
                    //判断是否从X区域离开 （比如进入A区域之后5分钟需要检测是否洗手，如果在五分钟之内离开A区域则不进行洗手监控）
                    if (!Objects.equals(userCurrentRegionDto.getRegionId(), regionWashMonitorDelayDto.getRegionId())) {
                        log.info("离开之前的区域，取消洗手监控");
                        return;
                    } else {
                        Region region = redisUtil.getRegionById(userCurrentRegionDto.getRegionId());
                        ListWashTemplateItemVo washTemplateItemVo = redisUtil.getWashTemplate(region.getWashTemplateId());
                        if (Objects.isNull(washTemplateItemVo)) {
                            return;
                        }
                        //延迟推送洗手监控命令（采用MQ消息延迟推送机制）该逻辑属于循环延迟-用于进入后X分钟
                        if (Objects.nonNull(regionWashMonitorDelayDto.getDelayDateTime())) {
                            Duration duration = Duration.between(LocalDateTime.now(), regionWashMonitorDelayDto.getDelayDateTime());
                            long millis = duration.toMillis();
                            if (millis <= 1000) {
                                //推送洗手检测命令
                                rocketMQTemplate.syncSend(TopicConstants.REGION_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashMonitorDelayDto)).build());
//                                after(washTemplateItemVo,regionWashMonitorDelayDto);
                            } else {
                                Integer delayLevel = MessageDelayUtil.getDelayLevel(regionWashMonitorDelayDto.getDelayDateTime());
                                if (delayLevel > -1) {
                                    //log.info("推送延迟检测命令(循环延迟)");
                                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashMonitorDelayDto)).build(), 1000, delayLevel);
                                }
                            }
                            return;
                        }

                        //延迟推送洗手监控命令 循环延迟的第一次处理 根据洗手规则的检测时间BeforeEnteringTime/AfterEnteringTime来设置延迟推送洗手检测命令
                        //监控进入区域前洗手监控
                        if (Objects.nonNull(washTemplateItemVo.getBeforeTime())) {
                            try {
                                rocketMQTemplate.syncSend(TopicConstants.REGION_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashMonitorDelayDto)).build());
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                        after(washTemplateItemVo,regionWashMonitorDelayDto);

                    }
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private void after(ListWashTemplateItemVo washTemplateItemVo,RegionWashMonitorDelayDto regionWashMonitorDelayDto){
        //监控进入区域前后洗手监控
        if ( Objects.nonNull(washTemplateItemVo.getAfterTime()) && washTemplateItemVo.getAfterTime()>0) {
            //进入X区域之后X几分钟检测是否洗手（延迟推送洗手检测命令）
            LocalDateTime delayDateTime = LocalDateTime.now().plusMinutes(washTemplateItemVo.getAfterTime());
            regionWashMonitorDelayDto.setDelayDateTime(delayDateTime);
            try {
                LocalDateTime localDateTime =LocalDateTime.now().plusSeconds(washTemplateItemVo.getAfterTime());
                regionWashMonitorDelayDto.setDelayDateTime(localDateTime);
                Integer delayLevel = MessageDelayUtil.getDelayLevel(localDateTime);
                if (delayLevel > -1) {
                    rocketMQTemplate.syncSend(TopicConstants.REGION_WASH_DELAY, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(regionWashMonitorDelayDto)).build(), 2000, delayLevel);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
