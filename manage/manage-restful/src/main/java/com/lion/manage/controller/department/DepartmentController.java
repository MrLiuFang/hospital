package com.lion.manage.controller.department;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.dto.AddDepartmentDto;
import com.lion.manage.entity.department.dto.UpdateDepartmentDto;
import com.lion.manage.entity.department.vo.DetailsDepartmentVo;
import com.lion.manage.entity.department.vo.ListDepartmentVo;
import com.lion.manage.entity.department.vo.TreeDepartmentVo;
import com.lion.manage.entity.region.Region;
import com.lion.manage.service.department.DepartmentResponsibleUserService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
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

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:33
 */
@RestController
@RequestMapping("/department")
@Validated
@Api(tags = {"科室管理"})
public class DepartmentController extends BaseControllerImpl implements BaseController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentResponsibleUserService departmentResponsibleUserService;

    @Autowired
    private RegionService regionService;

    @PostMapping("/add")
    @ApiOperation(value = "新增科室")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddDepartmentDto addDepartmentDto){
        departmentService.add(addDepartmentDto);
        return ResultData.instance();
    }

    @GetMapping("/treeList")
    @ApiOperation(value = "科室树形列表")
    public IResultData<List<TreeDepartmentVo>> treeList(@ApiParam(value = "科室名称") String name){
        ResultData resultData = ResultData.instance();
        resultData.setData(departmentService.treeList(name));
        return resultData;
    }

    @GetMapping("/list")
    @ApiOperation(value = "科室列表")
    public IPageResultData<List<ListDepartmentVo>> list(@ApiParam(value = "科室名称") String name, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) departmentService.findNavigator(lionPage);
        List<Department> list = page.getContent();
        List<ListDepartmentVo> listDepartmentVo = new ArrayList<ListDepartmentVo>();
        list.forEach(department -> {
            ListDepartmentVo departmentVo = new ListDepartmentVo();
            BeanUtils.copyProperties(department,departmentVo);
            departmentVo.setResponsibleUser(departmentResponsibleUserService.responsibleUser(department.getId()));
            listDepartmentVo.add(departmentVo);
        });
        return new PageResultData(listDepartmentVo, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/details")
    @ApiOperation(value = "科室详情")
    public IResultData<DetailsDepartmentVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(this.departmentService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改科室")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateDepartmentDto updateDepartmentDto){
        departmentService.update(updateDepartmentDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除科室")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(d->{
            List<Region> list = regionService.find(d.getId());
            if (list.size()>0){
                Department department = this.departmentService.findById(d.getId());
                BusinessException.throwException(department.getName()+"已关联区域,所有删除操作取消");
            }
        });
        departmentService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

}
