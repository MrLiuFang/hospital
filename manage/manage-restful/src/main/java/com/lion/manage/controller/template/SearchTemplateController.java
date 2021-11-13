package com.lion.manage.controller.template;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.template.SearchTemplate;
import com.lion.manage.entity.template.dto.AddSearchTemplateDto;
import com.lion.manage.entity.template.dto.UpdateSearchTemplateDto;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.dto.AddWardDto;
import com.lion.manage.entity.ward.dto.UpdateWardDto;
import com.lion.manage.entity.ward.vo.DetailsWardRoomVo;
import com.lion.manage.entity.ward.vo.DetailsWardVo;
import com.lion.manage.entity.ward.vo.ListWardVo;
import com.lion.manage.service.template.SearchTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/search/template")
@Validated
@Api(tags = {"搜索模板"})
public class SearchTemplateController extends BaseControllerImpl implements BaseController {

    @Autowired
    private SearchTemplateService searchTemplateService;

    @PostMapping("/add")
    @ApiOperation(value = "新增搜索模板")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddSearchTemplateDto addSearchTemplateDto){
        searchTemplateService.add(addSearchTemplateDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "病房搜索模板")
    public IPageResultData<List<SearchTemplate>> list(@ApiParam(value = "搜索模板名称") String name, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<SearchTemplate>>) searchTemplateService.findNavigator(lionPage);

    }

    @GetMapping("/details")
    @ApiOperation(value = "搜索模板详情")
    public IResultData<SearchTemplate> details(@NotNull(message = "{0000000}") Long id){
        return ResultData.instance().setData(searchTemplateService.findById(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改搜索模板")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateSearchTemplateDto updateSearchTemplateDto){
        searchTemplateService.update(updateSearchTemplateDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除搜索模板")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        searchTemplateService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }
}
