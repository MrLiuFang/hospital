package com.lion.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.annotation.AuthorizationIgnore;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.DeviceDataDto;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/14上午9:33
 */
@RestController
@RequestMapping("")
@Validated
@Api(tags = {"事件"})
@Log4j2
public class EventController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;



    @PostMapping("/new")
    @AuthorizationIgnore
    public String newEvent(@RequestBody List<DeviceDataDto> deviceDataDtos) {
        log.info("收到事件数据");
        deviceDataDtos.forEach(deviceDataDto -> {
            try {
                rocketMQTemplate.syncSend(TopicConstants.EVENT, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(deviceDataDto)).build());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return "0";
    }

}
