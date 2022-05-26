package com.lion.manage.controller.rule;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.Lose;
import com.lion.manage.entity.rule.dto.AddAlarmDto;
import com.lion.manage.entity.rule.dto.UpdateAlarmDto;
import com.lion.manage.entity.rule.vo.DetailsAlarmVo;
import com.lion.manage.entity.rule.vo.ListAlarmVo;
import com.lion.manage.service.rule.AlarmService;
import com.lion.manage.service.rule.LoseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:38
 */
@RestController
@RequestMapping("/rule/alarm")
@Validated
@Api(tags = {"警报规则"})
public class AlarmController extends BaseControllerImpl implements BaseController {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private LoseService loseService;

    @PostMapping("/add")
    @ApiOperation(value = "新增警报规则")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddAlarmDto addAlarmDto){
        alarmService.add(addAlarmDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "警报规则列表")
    public IPageResultData<List<ListAlarmVo>> list(@ApiParam(value = "警报内容") String content, @ApiParam(value = "警报分类") AlarmClassify classify, @ApiParam(value = "级别(仅限患者分类下的(1,2,3级))") Integer level, LionPage lionPage){
        return alarmService.list(content, classify, level, lionPage);
    }

    @GetMapping("/user/details")
    @ApiOperation(value = "获取用户警报规则")
    public IResultData<List<DetailsAlarmVo>> userDetails(Long userid){
        ResultData resultData = ResultData.instance();
        resultData.setData(alarmService.userAlarm(userid));
        return resultData;
    }

    @GetMapping("/details")
    @ApiOperation(value = "警报规则详情")
    public IResultData<DetailsAlarmVo> details(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(alarmService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改警报规则")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateAlarmDto updateAlarmDto){
        alarmService.update(updateAlarmDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除警报规则")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        alarmService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/lose/add")
    @ApiOperation(value = "新增失联警报规则")
    public IResultData addLose(@RequestBody Lose lose){
        loseService.save(lose);
        return ResultData.instance();
    }


    @GetMapping("/lose/list")
    @ApiOperation(value = "失联警报规则列表")
    public IPageResultData<List<Lose>> listLose( LionPage lionPage){
        return (IPageResultData<List<Lose>>) loseService.findNavigator(lionPage);
    }

    @GetMapping("/lose/details")
    @ApiOperation(value = "失联警报规则详情")
    public IResultData<DetailsAlarmVo> detailsLose(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(loseService.findById(id).get());
        return resultData;
    }

    @PutMapping("/lose/update")
    @ApiOperation(value = "修改失联警报规则")
    public IResultData updateLose(@RequestBody Lose lose){
        loseService.update(lose);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除失联警报规则")
    @DeleteMapping("/lose/delete")
    public IResultData deleteLose(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(deleteDto -> {
            loseService.deleteById(deleteDto.getId());
        });
        ResultData resultData = ResultData.instance();
        return resultData;
    }
}
