package com.lion.device.controller.tag;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagRule;
import com.lion.device.entity.tag.TagRuleLog;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.AddTagRuleDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
import com.lion.device.entity.tag.dto.UpdateTagRuleDto;
import com.lion.device.entity.tag.vo.ListTagRuleUserVo;
import com.lion.device.entity.tag.vo.ListTagVo;
import com.lion.device.service.tag.TagRuleLogService;
import com.lion.device.service.tag.TagRuleService;
import com.lion.device.service.tag.TagRuleUserService;
import com.lion.device.service.tag.TagService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.dto.AddAssetsDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsVo;
import com.lion.manage.entity.assets.vo.ListAssetsVo;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AssetsType;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
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

    @PostMapping("/add")
    @ApiOperation(value = "新增标签")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddTagDto addTagDto){
        tagService.add(addTagDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "标签列表")
    public IPageResultData<List<ListTagVo>> list(@ApiParam(value = "电量(0=正常,1=少於90 天,2=少於30天)")Integer battery, @ApiParam(value = "标签编码") String tagCode, @ApiParam(value = "标签分类") TagType type, @ApiParam(value = "用途") TagPurpose purpose, LionPage lionPage){
        return tagService.list(battery, tagCode, type, purpose, lionPage);
    }

//    @GetMapping("/details")
//    @ApiOperation(value = "标签详情")
//    public IResultData<DetailsAssetsVo> details(@NotNull(message = "id不能为空") Long id){
//        ResultData resultData = ResultData.instance();
//        resultData.setData(tagService.findById(id));
//        return resultData;
//    }

    @PutMapping("/update")
    @ApiOperation(value = "修改标签")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateTagDto updateTagDto){
        tagService.update(updateTagDto);
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
    public IResultData ruleAdd(@RequestBody @Validated({Validator.Insert.class}) AddTagRuleDto addTagRuleDto){
        tagRuleService.add(addTagRuleDto);
        return ResultData.instance();
    }

    @PutMapping("/rule/update")
    @ApiOperation(value = "修改标签规则")
    public IResultData ruleUpdate(@RequestBody @Validated({Validator.Update.class}) UpdateTagRuleDto updateTagRuleDto){
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
    public IPageResultData<List<TagRuleLog>> ruleLogList(@NotNull(message = "标签规则id不能为空") @ApiParam(value = "标签规则id") Long tagRuleId, LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(tagRuleId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_tagRuleId",tagRuleId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<TagRuleLog>>) tagRuleLogService.findNavigator(lionPage);
    }

}
