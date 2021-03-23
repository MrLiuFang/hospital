package com.lion.manage.controller.department;

import com.lion.core.IResultData;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.dto.AddDepartmentDto;
import com.lion.manage.service.department.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:33
 */
@RestController
@RequestMapping("/department/")
@Validated
@Api(tags = {"科室管理"})
public class DepartmentController extends BaseControllerImpl implements BaseController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/add")
    @ApiOperation(value = "新增科室",notes = "新增科室")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddDepartmentDto addDepartmentDto){
        departmentService.add(addDepartmentDto);
        return ResultData.instance();
    }
}
