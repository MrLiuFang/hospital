package com.lion.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.entity.enums.SystemAlarmType;
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

    @Scheduled(fixedDelay = 5000)
    public void execute() {
        List<Long> tagId = tagExposeService.allId(TagPurpose.PATIENT);
        if (Objects.nonNull(tagId) && tagId.size()>=0) {
            tagId.forEach(id -> {
                Boolean tagLose = (Boolean) redisTemplate.opsForValue().get(RedisConstants.TAG_LOSE + String.valueOf(id));
                if (!Objects.equals(tagLose,true)) {
                    LocalDateTime dateTime = (LocalDateTime) redisTemplate.opsForValue().get(RedisConstants.LAST_DATA + String.valueOf(id));
                    if (Objects.nonNull(dateTime) ) {
                        Duration duration = Duration.between(dateTime,LocalDateTime.now());
                        long millis = duration.toMillis();
                        if (millis>=5000) {
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
                            redisTemplate.opsForValue().set(RedisConstants.TAG_LOSE + String.valueOf(id), true);
                        }
                    }
                }
            });
            tagId.clear();
            tagId = null;
        }
    }
}
