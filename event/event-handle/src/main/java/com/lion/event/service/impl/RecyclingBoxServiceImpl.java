package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.CurrentRegionDto;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.RecyclingBoxRecordDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.event.service.CommonService;
import com.lion.event.service.RecyclingBoxService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:30
 */
@Service
public class RecyclingBoxServiceImpl implements RecyclingBoxService {

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private CommonService commonService;


    @Override
    public void event(DeviceDataDto deviceDataDto, Device monitor, Device star, Tag tag, Patient patient, TemporaryPerson temporaryPerson) throws JsonProcessingException {
        if (Objects.nonNull(patient)) {
            patientExposeService.updateIsWaitLeave(patient.getId(),true);
        }else if (Objects.nonNull(temporaryPerson)){
            temporaryPersonExposeService.updateIsWaitLeave(temporaryPerson.getId(),true);
        }
        CurrentRegionDto currentRegionDto = commonService.currentRegion(monitor,star);
        RecyclingBoxRecordDto recyclingBoxRecordDto = new RecyclingBoxRecordDto();
        recyclingBoxRecordDto.setRi(Objects.nonNull(currentRegionDto)?currentRegionDto.getRegionId():null);
        recyclingBoxRecordDto.setRbc(monitor.getCode());
        recyclingBoxRecordDto.setRbn(monitor.getName());
        recyclingBoxRecordDto.setRbi(monitor.getId());
        recyclingBoxRecordDto.setTi(tag.getId());
        recyclingBoxRecordDto.setTt(tag.getType().getKey());
        recyclingBoxRecordDto.setTp(tag.getPurpose().getKey());
        recyclingBoxRecordDto.setTc(tag.getTagCode());
        recyclingBoxRecordDto.setDdt(deviceDataDto.getTime());
        recyclingBoxRecordDto.setSdt(deviceDataDto.getSystemDateTime());
        rocketMQTemplate.syncSend(TopicConstants.RECYCLING_BOX, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(recyclingBoxRecordDto)).build());
    }
}
