package com.lion.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.expose.assets.AssetsBorrowExposeService;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.assets.AssetsFaultExposeService;
import com.lion.person.entity.person.Patient;
import com.lion.person.expose.person.PatientExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class TagLoseAlarm {

    @DubboReference
    private TagExposeService tagExposeService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private AssetsFaultExposeService assetsFaultExposeService;

    @DubboReference
    private AssetsBorrowExposeService assetsBorrowExposeService;

    @DubboReference
    private PatientExposeService patientExposeService;

    @Scheduled(fixedDelay = 5000)
    public void execute() {
        List<Patient> patientList = patientExposeService.find(false);
        patientList.forEach(patient -> {
            Tag tag = redisUtil.getTag(patient.getTagCode());
            if (Objects.nonNull(tag) && Objects.nonNull(patient.getLoseTime())) {
                LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(tag.getId()));
                if (Objects.nonNull(dateTime)) {
                    Duration duration = Duration.between(dateTime, LocalDateTime.now());
                    long seconds = duration.toSeconds();
                    if (seconds >= patient.getLoseTime()) {
                        alarm(tag.getId(),patient);
                    }
                }else {
                    alarm(tag.getId(),patient);
                }
            }
        });

//        {
//            List<Long> ids = assetsExposeService.allId();
//            ids.forEach(id->{
//                LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
//                if (Objects.nonNull(dateTime)) {
//                    Duration duration = Duration.between(dateTime, LocalDateTime.now());
//                    long millis = duration.toMillis();
//                    if (millis >= 1000*60*10) {
//                        Optional<Assets> optional = assetsExposeService.findById(id);
//                        if (optional.isPresent()){
//                            Assets assets = optional.get();
//                            assets.setState(AssetsState.LOSE);
//                            assetsExposeService.update(assets);
//                            redisTemplate.opsForValue().set(RedisConstants.TAG_LOSE + String.valueOf(id), true);
//                        }
//                    }
//                }
//            });
//        }

    }

    private void alarm(Long tagId,Patient patient){
        Long _id = (Long) redisTemplate.opsForValue().get(RedisConstants.TAG_LOSE+tagId);
        if (Objects.nonNull(_id)) {
            return;
        }
        CurrentRegionDto currentRegionDto = (CurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.LAST_REGION + String.valueOf(tagId));
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(Type.PATIENT);
        systemAlarmDto.setTagId(tagId);
        systemAlarmDto.setRegionId(Objects.nonNull(currentRegionDto) ? currentRegionDto.getRegionId() : null);
        systemAlarmDto.setPeopleId(patient.getId());
        systemAlarmDto.setSystemAlarmType(SystemAlarmType.TAG_LOSE);
        try {
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
            redisTemplate.opsForValue().set(RedisConstants.TAG_LOSE+tagId,tagId,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 1000*60)
    public void execute1() {
//        List<Long> ids = assetsExposeService.allId();
//        ids.forEach(id->{
//            Optional<Assets> optional = assetsExposeService.findById(id);
//            LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
//            if (Objects.nonNull(dateTime)) {
//                Duration duration = Duration.between(dateTime, LocalDateTime.now());
//                long millis = duration.toMillis();
//                if (millis >= 1000*60*10) {
//                    if (optional.isPresent()){
//                        Assets assets = optional.get();
//                        assets.setState(AssetsState.LOSE);
//                        assetsExposeService.update(assets);
//                    }
//                }
//            }else {
//                AssetsFault assetsFault = assetsFaultExposeService.find(id, AssetsFaultState.NOT_FINISHED);
//                if (Objects.nonNull(assetsFault)) {
//                    if (optional.isPresent()){
//                        Assets assets = optional.get();
//                        assets.setState(AssetsState.REPAIR);
//                        assetsExposeService.update(assets);
//                    }
//                }else {
//                    AssetsBorrow assetsBorrow = assetsBorrowExposeService.findNotReturn(id);
//                    if (optional.isPresent()){
//                        Assets assets = optional.get();
//                        if (Objects.nonNull(assetsBorrow)) {
//                            assets.setState(AssetsState.USEING);
//                        }else {
//                            assets.setState(AssetsState.NOT_USED);
//                        }
//                        assetsExposeService.update(assets);
//                    }
//
//                }
//            }
//        });
    }

}
