package com.lion.device.controller.tag;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.*;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagRule;
import com.lion.device.entity.tag.TagRuleUser;
import com.lion.device.entity.tag.dto.*;
import com.lion.device.entity.tag.vo.*;
import com.lion.device.service.tag.*;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:18
 */
@RestController
@RequestMapping("/tag")
@Validated
@Api(tags = {"标签管理"})
public class TagController extends BaseControllerImpl implements BaseController {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRuleService tagRuleService;

    @Autowired
    private TagRuleUserService tagRuleUserService;

    @Autowired
    private TagRuleLogService tagRuleLogService;

    @Autowired
    private TagLogService tagLogService;

    @PostMapping("/add")
    @ApiOperation(value = "新增标签")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddTagDto addTagDto){
        addTagDto.setDeviceState(State.ACTIVE);
        tagService.add(addTagDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "标签列表")
    public IPageResultData<List<ListTagVo>> list(@ApiParam(value = "是否负责的科室")Boolean isResponsibleDepartment,@ApiParam(value = "是否所有")Boolean isAll,  @ApiParam(value = "是否临时-导入")String isTmp,@ApiParam(value = "部门")Long departmentId,@ApiParam(value = "使用状态")TagUseState useState,@ApiParam(value = "状态")State state,@ApiParam(value = "电量(0=正常,1=少於90 天,2=少於30天)")Integer battery, @ApiParam(value = "标签编码") String tagCode, @ApiParam(value = "标签分类") TagType type, @ApiParam(value = "用途") TagPurpose purpose, LionPage lionPage){
        return tagService.list(isResponsibleDepartment,isAll , isTmp, departmentId, useState, state, battery, tagCode, type, purpose, lionPage);
    }



    @GetMapping("/log/list")
    @ApiOperation(value = "标签列表")
    public IPageResultData<List<ListTagLogVo>> logList(@NotNull(message = "{0000000}") @ApiParam(value = "标签ID") Long tagId,
                                                       @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                       @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                       @ApiParam(value = "操作内容(绑定/解绑)") TagLogContent content,LionPage lionPage){
        return tagLogService.list(tagId,startDateTime,endDateTime,content, lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "标签详情")
    public IResultData<DetailsTagVo> details(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(tagService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改标签")
    public IResultData update(@RequestBody UpdateTagDto updateTagDto){
        if (Objects.equals(updateTagDto.getDeviceState(),State.NOT_ACTIVE)) {
            updateTagDto.setDeviceState(null);
        }
        tagService.update(updateTagDto);
        return ResultData.instance();
    }

    @PutMapping("/update/state")
    @ApiOperation(value = "修改标签状态")
    public IResultData updateState(@RequestBody @Validated({Validator.Update.class}) UpdateTagStateDto updateTagStateDto){
        com.lion.core.Optional<Tag> optional = tagService.findById(updateTagStateDto.getId());
        if (optional.isPresent()){
            Tag tag=optional.get();
            tag.setDeviceState(updateTagStateDto.getState());
            tagService.update(tag);
        }
        return ResultData.instance();
    }

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        List<Tag> list = this.tagService.delete(deleteDtoList);
        if (list.size()>0) {
            tagService.saveAll(list);
        }
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/purpose/statistics")
    @ApiOperation(value = "标签统计")
    public IResultData<List<PurposeStatisticsVo>> purposeStatistics(){
        return ResultData.instance().setData(tagService.purposeStatistics());
    }

    @PostMapping("/rule/add")
    @ApiOperation(value = "新增标签规则")
    public IResultData ruleAdd(@RequestBody AddTagRuleDto addTagRuleDto){
        tagRuleService.add(addTagRuleDto);
        return ResultData.instance();
    }

    @PutMapping("/rule/update")
    @ApiOperation(value = "修改标签规则")
    public IResultData ruleUpdate(@RequestBody UpdateTagRuleDto updateTagRuleDto){
        tagRuleService.update(updateTagRuleDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除标签规则")
    @DeleteMapping("/rule/delete")
    public IResultData ruleDelete(@RequestBody List<DeleteDto> deleteDtoList){
        tagRuleService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/rule/list")
    @ApiOperation(value = "标签规则列表")
    public IPageResultData<List<TagRule>> ruleList(@ApiParam(value = "标签规则名称") String name, LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<TagRule>>) tagRuleService.findNavigator(lionPage);
    }

    @GetMapping("/rule/details")
    @ApiOperation(value = "标签规则详情")
    public IResultData<TagRuleDetailsVo> ruleDetails(@ApiParam(value = "id") @NotNull(message = "{0000000}") Long id){
        com.lion.core.Optional<TagRule> optional = tagRuleService.findById(id);
        if (optional.isPresent()) {
            TagRuleDetailsVo vo = new TagRuleDetailsVo();
            BeanUtils.copyProperties(optional.get(),vo);
            List<Long> userIds = new ArrayList<>();
            List<TagRuleUser> list =  tagRuleUserService.find(id);
            list.forEach(tagUser -> {
                userIds.add(tagUser.getUserId());
            });
            vo.setAllUserIds(userIds);
            return ResultData.instance().setData(vo);
        }
        return ResultData.instance();
    }

    @GetMapping("/rule/user/search")
    @ApiOperation(value = "标签规则查询可关联的用户")
    public IResultData<List<User>> ruleUserSearch(@ApiParam(value = "科室")Long departmentId,@ApiParam(value = "姓名") String name,@ApiParam(value = "用户类型") Long userTypeId, LionPage lionPage){
        return tagRuleUserService.ruleUserSearch(departmentId, name, userTypeId, lionPage);
    }

    @GetMapping("/rule/user/list")
    @ApiOperation(value = "标签规则用户列表")
    public IPageResultData<List<ListTagRuleUserVo>> ruleUserList(@NotNull(message = "{4000034}") @ApiParam(value = "标签规则id") Long tagRuleId, LionPage lionPage){
        return tagRuleUserService.list(tagRuleId, lionPage);
    }

    @GetMapping("/rule/log/list")
    @ApiOperation(value = "标签规则日志列表")
    public IPageResultData<List<ListTagRuleLogVo>> ruleLogList(@ApiParam(value = "标签规则id") Long tagRuleId,@ApiParam(value = "开始时间")LocalDateTime startDateTime,@ApiParam(value = "结束时间") LocalDateTime endDateTime, @ApiParam(value = "结束时间") TagRuleLogType actionType, LionPage lionPage){
        return tagRuleLogService.list(tagRuleId, startDateTime, endDateTime, actionType, lionPage);
    }

}
