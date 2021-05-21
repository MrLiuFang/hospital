package com.lion.device.controller.tag;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagRule;
import com.lion.device.entity.tag.TagRuleUser;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.entity.tag.dto.*;
import com.lion.device.entity.tag.vo.*;
import com.lion.device.service.tag.*;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
        tagService.add(addTagDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "标签列表")
    public IPageResultData<List<ListTagVo>> list( @ApiParam(value = "使用状态")TagUseState useState,@ApiParam(value = "电量(0=正常,1=少於90 天,2=少於30天)")Integer battery, @ApiParam(value = "标签编码") String tagCode, @ApiParam(value = "标签分类") TagType type, @ApiParam(value = "用途") TagPurpose purpose, LionPage lionPage){
        return tagService.list(useState, battery, tagCode, type, purpose, lionPage);
    }

    @GetMapping("/log/list")
    @ApiOperation(value = "标签列表")
    public IPageResultData<List<ListTagLogVo>> logList(@NotNull(message = "标签id不能为空") @ApiParam(value = "标签ID") Long tagId,
                                                       @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                       @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                       @ApiParam(value = "操作内容(绑定/解绑)") TagLogContent content,LionPage lionPage){
        return tagLogService.list(tagId,startDateTime,endDateTime,content, lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "标签详情")
    public IResultData<Tag> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(tagService.findById(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改标签")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateTagDto updateTagDto){
        tagService.update(updateTagDto);
        return ResultData.instance();
    }

    @PutMapping("/update/state")
    @ApiOperation(value = "修改标签状态")
    public IResultData updateState(@RequestBody @Validated({Validator.Update.class}) UpdateTagStateDto updateTagStateDto){
        Tag tag = tagService.findById(updateTagStateDto.getId());
        if (Objects.nonNull(tag)){
            tag.setState(updateTagStateDto.getState());
            tagService.update(tag);
        }
        return ResultData.instance();
    }

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        this.tagService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
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
        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<TagRule>>) tagRuleService.findNavigator(lionPage);
    }

    @GetMapping("/rule/details")
    @ApiOperation(value = "标签规则详情")
    public IResultData<TagRuleDetailsVo> ruleDetails(@ApiParam(value = "id") @NotNull(message = "id不能为空") Long id){
        TagRule tagRule = tagRuleService.findById(id);
        if (Objects.nonNull(tagRule)) {
            TagRuleDetailsVo vo = new TagRuleDetailsVo();
            BeanUtils.copyProperties(tagRule,vo);
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
    public IResultData<List<User>> ruleUserSearch(@ApiParam(value = "科室")Long departmentId,@ApiParam(value = "姓名") String name,@ApiParam(value = "用户类型") UserType userType, LionPage lionPage){
        return tagRuleUserService.ruleUserSearch(departmentId, name, userType, lionPage);
    }

    @GetMapping("/rule/user/list")
    @ApiOperation(value = "标签规则用户列表")
    public IPageResultData<List<ListTagRuleUserVo>> ruleUserList(@NotNull(message = "标签规则id不能为空") @ApiParam(value = "标签规则id") Long tagRuleId, LionPage lionPage){
        return tagRuleUserService.list(tagRuleId, lionPage);
    }

    @GetMapping("/rule/log/list")
    @ApiOperation(value = "标签规则日志列表")
    public IPageResultData<List<ListTagRuleLogVo>> ruleLogList(@NotNull(message = "标签规则id不能为空") @ApiParam(value = "标签规则id") Long tagRuleId, LionPage lionPage){
        return tagRuleLogService.list(tagRuleId, lionPage);
    }

}
