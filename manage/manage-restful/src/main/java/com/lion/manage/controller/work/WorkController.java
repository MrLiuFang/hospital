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

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
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

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @PutMapping("/start/work")
    @ApiOperation(value = "上班")
    @ApiImplicitParams({@ApiImplicitParam(value = "userId")})
    public IResultData startWork(@RequestBody Map<String,Long> map) throws JsonProcessingException {
        if (map.containsKey("userId")){
            Long userId = map.get("userId");
            redisTemplate.opsForValue().set(RedisConstants.USER_WORK_STATE+userId,RedisConstants.USER_WORK_STATE_START,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            LoopWashDto loopWashDto = new LoopWashDto();
            loopWashDto.setUserId(map.get("uuid"));
            rocketMQTemplate.syncSend(TopicConstants.LOOP_WASH, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(loopWashDto)).build());
        }else {
            BusinessException.throwException("userId不能为空");
        }

        return ResultData.instance();
    }

    @PutMapping("/end/work")
    @ApiOperation(value = "下班")
    @ApiImplicitParams({@ApiImplicitParam(value = "userId")})
    public IResultData endWork(@RequestBody Map<String,Long> map){
        if (map.containsKey("userId")){
            Long userId = map.get("userId");
            redisTemplate.opsForValue().set(RedisConstants.USER_WORK_STATE+userId,RedisConstants.USER_WORK_STATE_END,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }else {
            BusinessException.throwException("userId不能为空");
        }
        return ResultData.instance();
    }
}
