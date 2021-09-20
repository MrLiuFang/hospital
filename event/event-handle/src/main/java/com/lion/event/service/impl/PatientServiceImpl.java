package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.SystemAlarmDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.event.service.PatientService;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.person.entity.enums.LogType;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.expose.person.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
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

    @DubboReference
    private PatientLogExposeService patientLogExposeService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void patientEvent(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, Patient patient) throws JsonProcessingException {
        CurrentRegionDto currentRegionDto = commonService.currentRegion(monitor,star);
        if (Objects.isNull(currentRegionDto)){
            return;
        }
        Region region = redisUtil.getRegionById(currentRegionDto.getRegionId());
        patientLogExposeService.add(Objects.nonNull(region)?region.getName():"", LogType.IN_REGION,Objects.nonNull(patient)?patient.getId():null,Objects.nonNull(patient)?patient.getId():null);
        redisTemplate.opsForValue().set(RedisConstants.PATIENT_CURRENT_REGION,currentRegionDto,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        PatientTransfer patientTransfer = patientTransferExposeService.find(patient.getId());
        commonService.position(deviceDataDto,patient,Objects.nonNull(currentRegionDto)?currentRegionDto.getRegionId():null,tag, patientTransfer);
        if (Objects.equals(deviceDataDto.getButtonId(),1)){
            systemAlarm(tag,SystemAlarmType.ZDHJ,currentRegionDto,patient);
        }else if (Objects.equals(deviceDataDto.getButtonId(),4)){
            systemAlarm(tag,SystemAlarmType.WJSQQXBQ,currentRegionDto,patient);
        }
//        List<TempLeave> tempLeaves =tempLeaveExposeService.find(patient.getId());
//        List<RestrictedArea> restrictedAreas = restrictedAreaExposeService.find(patient.getId(), PersonType.PATIENT );
        Boolean isLeaveRestrictedArea = true;
//        if (Objects.isNull(restrictedAreas) || restrictedAreas.size()<=0){
//            return;
//        }
//        for (RestrictedArea restrictedArea : restrictedAreas) {
//            if (Objects.equals(restrictedArea.getRegionId(),currentRegionDto.getRegionId())) {
//                isLeaveRestrictedArea = false;
//            }
//        }
        if (Objects.nonNull(patientTransfer) && isLeaveRestrictedArea){
            if (Objects.equals(patientTransfer.getNewDepartmentId(),region.departmentId)) {
                patientTransferExposeService.updateState(patient.getId(), TransferState.WAITING_TO_RECEIVE);
            }else {
                patientTransferExposeService.updateState(patient.getId(), TransferState.TRANSFERRING);
            }
            return;
        }

//        if (Objects.nonNull(tempLeaves) && tempLeaves.size()>0 && isLeaveRestrictedArea) {
//            for (TempLeave tempLeave :tempLeaves) {
//                LocalDateTime now = LocalDateTime.now();
//                if ( now.isAfter(tempLeave.getStartDateTime()) && now.isBefore(tempLeave.getEndDateTime()) ){
//                    TempLeaveMonitorDto tempLeaveMonitorDto = new TempLeaveMonitorDto();
//                    tempLeaveMonitorDto.setPatientId(patient.getId());
//                    tempLeaveMonitorDto.setDelayDateTime(tempLeave.getEndDateTime());
//                    rocketMQTemplate.syncSend(TopicConstants.TEMP_LEAVE_MONITOR, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(tempLeaveMonitorDto)).build());
//                    return;
//                }
//            }
//        }

        if (isLeaveRestrictedArea){
            systemAlarm(tag,SystemAlarmType.CCXDFW,currentRegionDto,patient);
        }

//        if (Objects.nonNull(monitor) && Objects.equals(monitor.getDeviceClassify(), DeviceClassify.RECYCLING_BOX)) {
//            patientExposeService.updateIsWaitLeave(patient.getId(),true);
//        }
    }

    private void systemAlarm(Tag tag, SystemAlarmType systemAlarmType, CurrentRegionDto currentRegionDto, Patient patient) throws JsonProcessingException {
        SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
        systemAlarmDto.setDateTime(LocalDateTime.now());
        systemAlarmDto.setType(Type.PATIENT);
        if (Objects.nonNull(tag)){
            systemAlarmDto.setTagId(tag.getId());
        }
        systemAlarmDto.setPeopleId(patient.getId());
        systemAlarmDto.setSystemAlarmType(systemAlarmType);
        systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
        systemAlarmDto.setRegionId(Objects.isNull(currentRegionDto)?null:currentRegionDto.getRegionId());
        rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
    }


}
