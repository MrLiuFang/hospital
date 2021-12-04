package com.lion.manage.controller.alarm;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.core.*;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.event.entity.dto.SetAlarmModeDto;
import com.lion.manage.entity.alarm.AlarmModeRecord;
import com.lion.manage.entity.alarm.vo.ListAlarmModeRecordVo;
import com.lion.manage.service.alarm.AlarmModeRecordService;
import com.lion.upms.entity.enums.AlarmMode;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.AssertUtil;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/26 下午8:11
 */
@RestController
@RequestMapping("/alarm")
@Validated
@Api(tags = {"警告规则切换"})
public class AlarmModeController extends BaseControllerImpl implements BaseController {

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AlarmModeRecordService alarmModeRecordService;

    @DubboReference
    private FileExposeService fileExposeService;

    @PostMapping("/set/mode")
    @ApiOperation(value = "切换洗手模式")
    public IResultData<AlarmMode> setAlarmMode(@RequestBody SetAlarmModeDto alarmMode) {
        if (Objects.nonNull(alarmMode.getAlarmMode())) {
            Long userId = CurrentUserUtil.getCurrentUserId();
            User user = userExposeService.findById(userId);
            AssertUtil.isFlase(passwordEncoder.matches(alarmMode.getPassword(),user.getPassword()), MessageI18nUtil.getMessage("3000035"));
            redisTemplate.opsForValue().set(RedisConstants.ALARM_MODE,alarmMode.getAlarmMode());
            AlarmModeRecord alarmModeRecord = new AlarmModeRecord();
            alarmModeRecord.setAlarmMode(alarmMode.getAlarmMode());
            alarmModeRecord.setUserId(userId);
            alarmModeRecordService.save(alarmModeRecord);

        }
        return alarmMode();
    }

    @GetMapping("/current/mode")
    @ApiOperation(value = "获取当前洗手模式")
    public IResultData<AlarmMode> alarmMode() {
        AlarmMode alarmMode = (AlarmMode) redisTemplate.opsForValue().get(RedisConstants.ALARM_MODE);
        return ResultData.instance().setData(Objects.isNull(alarmMode)?AlarmMode.STANDARD:alarmMode);
    }


    @GetMapping("/mode/list")
    @ApiOperation(value = "获取切换记录列表")
    public IPageResultData<List<ListAlarmModeRecordVo>> alarmModeList(@ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                      @ApiParam(value = "模式")AlarmMode alarmMode, @ApiParam(value = "操作员姓名")String name, LionPage lionPage) {

        return alarmModeRecordService.list(startDateTime, endDateTime, alarmMode, name, lionPage);
    }

    @GetMapping("/mode/list/export")
    @ApiOperation(value = "获取切换记录列表导出")
    public void alarmModeListExport(@ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                      @ApiParam(value = "模式")AlarmMode alarmMode, @ApiParam(value = "操作员姓名")String name) throws IOException, IllegalAccessException {

        alarmModeRecordService.export(startDateTime, endDateTime, alarmMode, name);
    }

}
