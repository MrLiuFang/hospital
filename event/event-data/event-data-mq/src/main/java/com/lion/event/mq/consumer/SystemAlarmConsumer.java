package com.lion.event.mq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.dto.UpdateStateDto;
import com.lion.common.enums.SystemAlarmState;
import com.lion.common.enums.Type;
import com.lion.common.utils.MessageDelayUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.service.SystemAlarmService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Alarm;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.upms.entity.user.User;
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
 * @Description //TODO
 * @Date 2021/5/2 上午10:28
 **/
@Component
@RocketMQMessageListener(topic = TopicConstants.SYSTEM_ALARM,selectorExpression="*",consumerGroup = TopicConstants.SYSTEM_ALARM_CONSUMER_GROUP)
@Log
public class SystemAlarmConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            SystemAlarmDto systemAlarmDto = jacksonObjectMapper.readValue(msg, SystemAlarmDto.class);

            if (Objects.nonNull(systemAlarmDto.getId())) {
                Boolean b = (Boolean) redisTemplate.opsForValue().get(RedisConstants.UNALARM + systemAlarmDto.getId());
                if (Objects.equals(b, true)) {
                    log.info("系统内解除警告");
                    redisTemplate.delete(RedisConstants.UNALARM + systemAlarmDto.getId());
                    return;
                }
            }
//            SystemAlarm systemAlarm = systemAlarmService.find(systemAlarmDto.getUuid());
//            if (Objects.nonNull(systemAlarm)) {
//                if (Objects.equals(true?1:0,systemAlarm.getUa())){
//                    log.info("系统内解除警告");
//                    return;
//                }
//            }
            Duration duration = Duration.between(LocalDateTime.now(),systemAlarmDto.getDelayDateTime());
            if (systemAlarmDto.getDelayDateTime().isAfter(LocalDateTime.now()) && duration.toMillis()>1000 ){
                againAlarm(systemAlarmDto);
                return;
            }else {
                Alarm alarm = getAlarm(systemAlarmDto);
                if (Objects.isNull(alarm)) {
                    log.info("未找到警告规则，取消警告");
                    return;
                }
                if (systemAlarmDto.getCount()>1){
                    systemAlarmDto.setCount(systemAlarmDto.getCount()+1);
                    systemAlarmService.updateSdt(systemAlarmDto.getId());
                    log.info("系统内触发警告");
                }else {
                    log.info("系统内触发警告");
                    SystemAlarm newSystemAlarm = new SystemAlarm();
                    newSystemAlarm.setAi(systemAlarmDto.getAssetsId());
                    if (Objects.nonNull(systemAlarmDto.getHumidity())) {
                        newSystemAlarm.setH(systemAlarmDto.getHumidity());
                    }
                    if (Objects.nonNull(systemAlarmDto.getTemperature())) {
                        newSystemAlarm.setT(systemAlarmDto.getTemperature());
                    }
                    newSystemAlarm.setDvi(systemAlarmDto.getDeviceId());
                    newSystemAlarm.setPi(systemAlarmDto.getPeopleId());
                    if (Objects.nonNull(systemAlarmDto.getSystemAlarmType())) {
                        newSystemAlarm.setSat(systemAlarmDto.getSystemAlarmType().getKey());
                    }
                    newSystemAlarm.setTi(systemAlarmDto.getTagId());
                    if (Objects.nonNull(systemAlarmDto.getType())) {
                        newSystemAlarm.setTy(systemAlarmDto.getType().getKey());
                    }
                    newSystemAlarm.setUa(SystemAlarmState.UNTREATED);
                    newSystemAlarm.setDt(systemAlarmDto.getDateTime());
                    newSystemAlarm.setSdt( systemAlarmDto.getDateTime() );
                    if (Objects.nonNull(alarm)) {
                        newSystemAlarm.setAli(alarm.getId());
                    }
                    Region region = redisUtil.getRegionById(systemAlarmDto.getRegionId());
                    if (Objects.nonNull(region)) {
                        newSystemAlarm.setRi(region.getId());
                        newSystemAlarm.setRn(region.getName());
                        Build build = redisUtil.getBuild(region.getBuildId());
                        if (Objects.nonNull(build)) {
                            newSystemAlarm.setBui(build.getId());
                            newSystemAlarm.setBun(build.getName());
                        }
                        BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                        if (Objects.nonNull(buildFloor)) {
                            newSystemAlarm.setBfi(buildFloor.getId());
                            newSystemAlarm.setBfn(buildFloor.getName());
                        }
                        Department department = redisUtil.getDepartment(region.getDepartmentId());
                        if (Objects.nonNull(department)) {
                            newSystemAlarm.setDi(department.getId());
                            newSystemAlarm.setDn(department.getName());
                        }
                    }
                    if (Objects.equals(newSystemAlarm.getTy(),Type.STAFF.getKey())) {
                        User user = redisUtil.getUserById(newSystemAlarm.getPi()) ;
                        if (Objects.nonNull(user)) {
                            Department department = redisUtil.getDepartmentByUserId(user.getId());
                            newSystemAlarm.setSdi(department.getId());
                        }
                    }else if (Objects.equals(newSystemAlarm.getTy(),Type.PATIENT.getKey())) {
                        Patient patient = redisUtil.getPatient(newSystemAlarm.getPi());
                        if (Objects.nonNull(patient)) {
                            newSystemAlarm.setSdi(patient.getDepartmentId());
                        }
                    }else if (Objects.equals(newSystemAlarm.getTy(),Type.MIGRANT.getKey())) {
                        TemporaryPerson temporaryPerson = redisUtil.getTemporaryPerson(newSystemAlarm.getPi());
                        if (Objects.nonNull(temporaryPerson)) {
                            newSystemAlarm.setSdi(temporaryPerson.getDepartmentId());
                        }
                    }else if (Objects.equals(newSystemAlarm.getTy(),Type.ASSET.getKey())) {
                        Assets assets = redisUtil.getAssets(newSystemAlarm.getTi());
                        if (Objects.nonNull(assets)){
                            newSystemAlarm.setSdi(assets.getDepartmentId());
                        }
                    }else if (Objects.equals(newSystemAlarm.getTy(),Type.DEVICE.getKey())) {

                    }else if (Objects.equals(newSystemAlarm.getTy(),Type.HUMIDITY.getKey()) || Objects.equals(newSystemAlarm.getTy(),Type.TEMPERATURE.getKey()) ) {
                        Tag tag = redisUtil.getTagById(newSystemAlarm.getTi());
                        if (Objects.nonNull(tag)) {
                            newSystemAlarm.setSdi(tag.getDepartmentId());
                        }
                    }
                    newSystemAlarm = systemAlarmService.save(newSystemAlarm);
                    systemAlarmDto.setId(newSystemAlarm.get_id());
                    systemAlarmDto.setCount(systemAlarmDto.getCount() + 1);

                    UpdateStateDto updateStateDto = new UpdateStateDto();
                    updateStateDto.setType(systemAlarmDto.getType());
                    updateStateDto.setState(2);
                    if (Objects.equals(updateStateDto.getType(),Type.STAFF) || Objects.equals(updateStateDto.getType(),Type.PATIENT) || Objects.equals(updateStateDto.getType(),Type.MIGRANT)) {
                        updateStateDto.setId(newSystemAlarm.getPi());
                    }else if (Objects.equals(updateStateDto.getType(),Type.ASSET)) {
                        updateStateDto.setId(newSystemAlarm.getAi());
                    }else if (Objects.equals(updateStateDto.getType(),Type.DEVICE)) {
                        updateStateDto.setId(newSystemAlarm.getDvi());
                    }else if (Objects.equals(updateStateDto.getType(),Type.HUMIDITY) || Objects.equals(updateStateDto.getType(),Type.TEMPERATURE)) {
                        updateStateDto.setId(newSystemAlarm.getTi());
                    }
                    rocketMQTemplate.syncSend(TopicConstants.UPDATE_STATE, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(updateStateDto)).build());
                }
                if (Objects.nonNull(alarm) && Objects.equals(alarm.getAgain(),true )) {
                    systemAlarmDto.setDelayDateTime(LocalDateTime.now().plusMinutes(alarm.getInterval()));
                    againAlarm(systemAlarmDto);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Alarm getAlarm(SystemAlarmDto systemAlarmDto){
        Alarm alarm = null;
        if (Objects.equals(systemAlarmDto.getType(), Type.STAFF)){
            alarm = redisUtil.getAlarm(AlarmClassify.STAFF, systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.TEMPERATURE) || Objects.equals(systemAlarmDto.getType(), Type.HUMIDITY)){
            alarm = redisUtil.getAlarm(AlarmClassify.TEMPERATURE_HUMIDITY_INSTRUMENT,systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.ASSET)){
            alarm = redisUtil.getAlarm(AlarmClassify.ASSETS,systemAlarmDto.getSystemAlarmType(),null);
        }else if (Objects.equals(systemAlarmDto.getType(), Type.PATIENT)){
            Patient patient = redisUtil.getPatient(systemAlarmDto.getPeopleId());
            alarm = redisUtil.getAlarm(AlarmClassify.PATIENT,systemAlarmDto.getSystemAlarmType(),patient.getLevel());
        }
        else if (Objects.equals(systemAlarmDto.getType(), Type.DEVICE)){
            alarm = redisUtil.getAlarm(AlarmClassify.DEVICE,systemAlarmDto.getSystemAlarmType(),null);
        }
        return alarm;
    }

    private void againAlarm(SystemAlarmDto systemAlarmDto) throws JsonProcessingException {
        Integer delayLevel = MessageDelayUtil.getDelayLevel(systemAlarmDto.getDelayDateTime());
        if (delayLevel > -1) {
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build(), 1000, delayLevel);
        }
    }
}
