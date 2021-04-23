package com.lion.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.annotation.AuthorizationIgnore;
import com.lion.core.IResultData;
import com.lion.core.ResultData;
import com.lion.event.dto.EventDto;
import com.lion.event.entity.Event;
import com.lion.event.service.EventService;
import io.swagger.annotations.Api;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
public class EventController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private EventService eventService;

    @PostMapping("/new")
    @AuthorizationIgnore
    public String newEvent(@RequestBody List<EventDto> eventDtos) {
        eventDtos.forEach(eventDto -> {
            try {
                rocketMQTemplate.syncSend("topic", MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(eventDto)).build());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return "0";
    }

    @GetMapping("/list")
    @AuthorizationIgnore
    public IResultData<List<Event>> list() {
        return ResultData.instance().setData(eventService.findAll());
    }
}
