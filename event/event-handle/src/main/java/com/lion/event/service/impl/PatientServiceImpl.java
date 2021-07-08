package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.dto.TempLeaveMonitorDto;
import com.lion.common.enums.Type;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.event.service.PatientService;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.PatientTransferExposeService;
import com.lion.person.expose.person.RestrictedAreaExposeService;
import com.lion.person.expose.person.TempLeaveExposeService;
import com.lion.upms.entity.user.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 上午11:34
 */
@Service
public class PatientServiceImpl implements PatientService {

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private PatientTransferExposeService patientTransferExposeService;

    @DubboReference
    private TempLeaveExposeService tempLeaveExposeService;

    @DubboReference
    private RestrictedAreaExposeService restrictedAreaExposeService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Override
    public void patientEvent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, Patient patient) throws JsonProcessingException {
        CurrentRegionDto currentRegionDto = commonService.currentRegion(monitor,star);
        if (Objects.isNull(currentRegionDto)){
            return;
        }
        redisTemplate.opsForValue().set(RedisConstants.PATIENT_CURRENT_REGION,currentRegionDto,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        commonService.position(deviceDataDto,patient,Objects.nonNull(currentRegionDto)?currentRegionDto.getRegionId():null,tag );
        PatientTransfer patientTransfer = patientTransferExposeService.find(patient.getId());
        List<TempLeave> tempLeaves =tempLeaveExposeService.find(patient.getId());
        List<RestrictedArea> restrictedAreas = restrictedAreaExposeService.find(patient.getId(), PersonType.PATIENT );
        Boolean isLeaveRestrictedArea = true;
        if (Objects.isNull(restrictedAreas) || restrictedAreas.size()<=0){
            return;
        }
        for (RestrictedArea restrictedArea : restrictedAreas) {
            if (Objects.equals(restrictedArea.getRegionId(),currentRegionDto.getRegionId())) {
                isLeaveRestrictedArea = false;
            }
        }
        if (Objects.nonNull(patientTransfer) && isLeaveRestrictedArea){
            Region region = regionExposeService.findById(currentRegionDto.getRegionId());
            if (Objects.equals(patientTransfer.getNewDepartmentId(),region.departmentId)) {
                patientTransferExposeService.updateState(patient.getId(), TransferState.WAITING_TO_RECEIVE);
            }else {
                patientTransferExposeService.updateState(patient.getId(), TransferState.TRANSFERRING);
            }
            return;
        }

        if (Objects.nonNull(tempLeaves) && tempLeaves.size()>0 && isLeaveRestrictedArea) {
            for (TempLeave tempLeave :tempLeaves) {
                LocalDateTime now = LocalDateTime.now();
                if ( now.isAfter(tempLeave.getStartDateTime()) && now.isBefore(tempLeave.getEndDateTime()) ){
                    TempLeaveMonitorDto tempLeaveMonitorDto = new TempLeaveMonitorDto();
                    tempLeaveMonitorDto.setPatientId(patient.getId());
                    tempLeaveMonitorDto.setDelayDateTime(tempLeave.getEndDateTime());
                    rocketMQTemplate.syncSend(TopicConstants.TEMP_LEAVE_MONITOR, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(tempLeaveMonitorDto)).build());
                    return;
                }
            }
        }

        if (isLeaveRestrictedArea){
            SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
            systemAlarmDto.setDateTime(LocalDateTime.now());
            systemAlarmDto.setType(Type.PATIENT);
            systemAlarmDto.setTagId(tag.getId());
            systemAlarmDto.setPeopleId(patient.getId());
            systemAlarmDto.setSystemAlarmType(SystemAlarmType.CCXDFW);
            systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
            systemAlarmDto.setRegionId(currentRegionDto.getRegionId());
            rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
        }

        if (Objects.nonNull(deviceDataDto.getButtonId()) && Objects.equals(deviceDataDto.getButtonId(),1)){
            systemAlarm(tag,currentRegionDto,patient);
        }

//        if (Objects.nonNull(monitor) && Objects.equals(monitor.getDeviceClassify(), DeviceClassify.RECYCLING_BOX)) {
//            patientExposeService.updateIsWaitLeave(patient.getId(),true);
//        }
    }

    private void systemAlarm(Tag tag,  CurrentRegionDto currentRegionDto, Patient patient) throws JsonProcessingException {
        //系统内警告
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(Type.PATIENT);
        systemAlarmDto.setPeopleId(patient.getId());
        if (Objects.nonNull(tag)){
            systemAlarmDto.setTagId(tag.getId());
        }
        systemAlarmDto.setSystemAlarmType(SystemAlarmType.ZDHJ);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setRegionId(Objects.isNull(systemAlarmDto)?null:currentRegionDto.getRegionId());
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
    }


}
