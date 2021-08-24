package com.lion.device.controller.fault;

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
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.enums.FaultType;
import com.lion.device.entity.fault.Fault;
import com.lion.device.entity.fault.dto.AddFaultDto;
import com.lion.device.entity.fault.dto.UpdateFaultDto;
import com.lion.device.entity.fault.vo.FaultDetailsVo;
import com.lion.device.entity.fault.vo.ListFaultVo;
import com.lion.device.service.fault.FaultService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
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
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午8:49
 */
@RestController
@RequestMapping("/fault")
@Validated
@Api(tags = {"故障申报"})
public class FaultController extends BaseControllerImpl implements BaseController {

    @Autowired
    private FaultService faultService;

    @PostMapping("/add")
    @ApiOperation(value = "新增故障")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddFaultDto addFaultDto){
        faultService.save(addFaultDto);
        return ResultData.instance();
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改故障")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateFaultDto updateFaultDto){
        faultService.update(updateFaultDto);
        return ResultData.instance();
    }

    @GetMapping("/list")
    @ApiOperation(value = "故障列表")
    public IPageResultData<List<ListFaultVo>> list(@ApiParam(value = "故障类型") FaultType type, @ApiParam(value = "资产编码/其它编码") String code, LionPage lionPage){
        return faultService.list(type, code, lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "故障详情")
    public IResultData<FaultDetailsVo> details(@ApiParam(value = "故障id") @NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(faultService.details(id));
        return resultData;
    }

    @ApiOperation(value = "删除故障")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(deleteDto -> {
            faultService.deleteById(deleteDto.getId());
        });
        ResultData resultData = ResultData.instance();
        return resultData;
    }
}
