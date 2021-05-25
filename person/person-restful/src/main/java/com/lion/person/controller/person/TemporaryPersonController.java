package com.lion.person.controller.person;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.person.dto.*;
import com.lion.person.entity.person.vo.ListTemporaryPersonVo;
import com.lion.person.entity.person.vo.TemporaryPersonDetailsVo;
import com.lion.person.service.person.TemporaryPersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:20
 */
@RestController
@RequestMapping("/temporary/person")
@Validated
@Api(tags = {"流动人员"})
public class TemporaryPersonController extends BaseControllerImpl implements BaseController {

    @Autowired
    private TemporaryPersonService temporaryPersonService;

    @PostMapping("/add")
    @ApiOperation(value = "新增流动人员")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddTemporaryPersonDto addTemporaryPersonDto){
        temporaryPersonService.add(addTemporaryPersonDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "流动人员列表")
    public IPageResultData<List<ListTemporaryPersonVo>> list(@ApiParam(value = "姓名")String name, @ApiParam(value = "是否登出") Boolean isLeave, LionPage lionPage){
        return temporaryPersonService.list(name, isLeave, lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "流动人员详情")
    public IResultData<TemporaryPersonDetailsVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(temporaryPersonService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改流动人员")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateTemporaryPersonDto updateTemporaryPersonDto){
        temporaryPersonService.update(updateTemporaryPersonDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除流动人员")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        temporaryPersonService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PutMapping("/leave")
    @ApiOperation(value = "患者登出")
    public IResultData leave(@RequestBody @Validated TemporaryPersonLeaveDto temporaryPersonLeaveDto){
        temporaryPersonService.leave(temporaryPersonLeaveDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }
}
