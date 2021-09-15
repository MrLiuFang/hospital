package com.lion.manage.controller.work;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.LoopWashDto;
import com.lion.core.IResultData;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.work.dto.WorkDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/19 下午2:18
 **/
@RestController
@RequestMapping("/work")
@Validated
@Api(tags = {"打卡"})
public class WorkController extends BaseControllerImpl implements BaseController {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @PutMapping("/start")
    @ApiOperation(value = "上班")
    public IResultData startWork(@RequestBody @Validated WorkDto workDto) throws JsonProcessingException {
        String uuid = UUID.randomUUID().toString();
        LoopWashDto loopWashDto = new LoopWashDto();
        loopWashDto.setUserId(workDto.getUserId());
        loopWashDto.setStartWashDateTime(LocalDateTime.now());
        loopWashDto.setUuid(uuid);
//        rocketMQTemplate.syncSend(TopicConstants.LOOP_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(loopWashDto)).build());
//        redisTemplate.opsForValue().set(RedisConstants.USER_WORK_STATE+workDto.getUserId(),RedisConstants.USER_WORK_STATE_START,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//        redisTemplate.opsForValue().set(RedisConstants.USER_WORK_STATE_UUID+workDto.getUserId(),uuid,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);

        return ResultData.instance();
    }

    @PutMapping("/end")
    @ApiOperation(value = "下班")
    public IResultData endWork(@RequestBody @Validated WorkDto workDto){
//        redisTemplate.opsForValue().set(RedisConstants.USER_WORK_STATE+workDto.getUserId(),RedisConstants.USER_WORK_STATE_END,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//        redisTemplate.delete(RedisConstants.USER_WORK_STATE_UUID+workDto.getUserId());
        return ResultData.instance();
    }
}
