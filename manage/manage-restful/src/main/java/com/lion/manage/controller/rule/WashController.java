package com.lion.manage.controller.rule;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.dto.AddWashDto;
import com.lion.manage.entity.rule.dto.UpdateWashDto;
import com.lion.manage.entity.rule.vo.DetailsWashVo;
import com.lion.manage.service.rule.WashService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @date 2021/4/9下午5:02
 */
@RestController
@RequestMapping("/rule/wash")
@Validated
@Api(tags = {"洗手规则"})
public class WashController extends BaseControllerImpl implements BaseController {

    @Autowired
    private WashService washService;

    @PostMapping("/add")
    @ApiOperation(value = "新增洗手规则")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddWashDto addWashDto){
        washService.add(addWashDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "洗手规则列表")
    public IPageResultData<List<Wash>> list(@ApiParam(value = "名称") String name, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) washService.findNavigator(lionPage);
        return page;
    }

    @GetMapping("/details")
    @ApiOperation(value = "洗手规则详情")
    public IResultData<DetailsWashVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(washService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改洗手规则")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateWashDto updateWashDto){
        washService.update(updateWashDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除洗手规则")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        washService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }
}
