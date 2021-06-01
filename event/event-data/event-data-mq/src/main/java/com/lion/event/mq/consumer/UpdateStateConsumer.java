package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.UpdateStateDto;
import com.lion.common.enums.Type;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.TagRecord;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.expose.user.UserExposeService;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/31 上午10:53
 */
@Component
@RocketMQMessageListener(topic = TopicConstants.UPDATE_STATE,selectorExpression="*",consumerGroup = TopicConstants.UPDATE_STATE_CONSUMER_GROUP)
@Log
public class UpdateStateConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            UpdateStateDto updateStateDto = jacksonObjectMapper.readValue(msg, UpdateStateDto.class);
            if (Objects.equals(updateStateDto.getType(), Type.STAFF)){
                if (Objects.equals(updateStateDto.getState(),1) && findAlarm(updateStateDto.getId(),null,null,null)){
                    return;
                }
                userExposeService.updateState(updateStateDto.getId(),updateStateDto.getState());
            }else if (Objects.equals(updateStateDto.getType(), Type.TEMPERATURE) || Objects.equals(updateStateDto.getType(), Type.HUMIDITY)){
                if (Objects.equals(updateStateDto.getState(),1) && findAlarm(null,null,null,updateStateDto.getId())){
                    return;
                }
                tagExposeService.updateState(updateStateDto.getId(),updateStateDto.getState());
            }else if (Objects.equals(updateStateDto.getType(), Type.PATIENT)){
                if (Objects.equals(updateStateDto.getState(),1) && findAlarm(updateStateDto.getId(),null,null,null)){
                    return;
                }
                patientExposeService.updateState(updateStateDto.getId(),updateStateDto.getState());
            }else if (Objects.equals(updateStateDto.getType(), Type.MIGRANT)){
                if (Objects.equals(updateStateDto.getState(),1) && findAlarm(updateStateDto.getId(),null,null,null)){
                    return;
                }
                temporaryPersonExposeService.updateState(updateStateDto.getId(),updateStateDto.getState());
            }else if (Objects.equals(updateStateDto.getType(), Type.ASSET)){
                if (Objects.equals(updateStateDto.getState(),1) && findAlarm(null,updateStateDto.getId(),null,null)){
                    return;
                }
                assetsExposeService.updateState(updateStateDto.getId(),updateStateDto.getState());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Boolean findAlarm(Long pi, Long ai, Long dvi,Long ti) {
        SystemAlarm systemAlarm = systemAlarmService.findOne(pi, ai, dvi, ti);
        if (Objects.isNull(systemAlarm)) {
            return false;
        }
        return true;
    }
}
