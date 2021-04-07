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
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
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

    @PostMapping("/add")
    @ApiOperation(value = "新增标签")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddTagDto addTagDto){
        tagService.add(addTagDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "标签列表")
    public IPageResultData<List<Tag>> list(@ApiParam(value = "标签编码") String tagCode,@ApiParam(value = "标签分类") TagType type,@ApiParam(value = "用途") TagPurpose purpose, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(tagCode)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_tagCode",tagCode);
        }
        if (Objects.nonNull(type)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_type",type);
        }
        if (Objects.nonNull(purpose)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_purpose",purpose);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Tag> page = tagService.findNavigator(lionPage);
        return (IPageResultData<List<Tag>>) page;
    }

    @GetMapping("/details")
    @ApiOperation(value = "标签详情")
    public IResultData<DetailsAssetsVo> details(@NotNull(message = "id不能为空") Long id){
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

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        this.tagService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

}
