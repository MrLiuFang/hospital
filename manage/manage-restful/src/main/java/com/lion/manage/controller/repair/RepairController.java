package com.lion.manage.controller.repair;

import com.lion.core.IResultData;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.manage.entity.repair.Repair;
import com.lion.manage.service.repair.RepairService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/30 下午3:01
 */
@RestController
@RequestMapping("/repair")
@Validated
@Api(tags = {"维修通知"})
public class RepairController extends BaseControllerImpl implements BaseController {

    @Autowired
    private RepairService repairService;

    @PostMapping("/add")
    @ApiOperation(value = "新增维修通知")
    public IResultData add(@RequestBody Repair repair){
        repairService.save(repair);
        return ResultData.instance();
    }
}
