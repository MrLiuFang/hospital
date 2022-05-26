package com.lion.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.core.Optional;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.entity.enums.AssetsState;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.expose.assets.AssetsBorrowExposeService;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.assets.AssetsFaultExposeService;
import com.lion.person.entity.person.Patient;
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

//    @Scheduled(fixedDelay = 5000)
    public void execute() {
        {
        List<Long> tagId = tagExposeService.allId(TagPurpose.PATIENT);
        if (Objects.nonNull(tagId) && tagId.size()>=0) {
            tagId.forEach(id -> {
                LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                if (Objects.nonNull(dateTime)) {
                    Duration duration = Duration.between(dateTime, LocalDateTime.now());
                    long millis = duration.toMillis();
                    if (millis >= 5000) {
                        CurrentRegionDto currentRegionDto = (CurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.LAST_REGION + String.valueOf(id));
                        Patient patient = redisUtil.getPatientByTagId(id);
                        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
                        systemAlarmDto.setDateTime(LocalDateTime.now());
                        systemAlarmDto.setType(Type.PATIENT);
                        systemAlarmDto.setTagId(id);
                        systemAlarmDto.setRegionId(Objects.nonNull(currentRegionDto) ? currentRegionDto.getRegionId() : null);
                        systemAlarmDto.setPeopleId(patient.getId());
                        systemAlarmDto.setSystemAlarmType(SystemAlarmType.TAG_LOSE);
                        try {
                            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            tagId.clear();
            tagId = null;
        }
        }

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
