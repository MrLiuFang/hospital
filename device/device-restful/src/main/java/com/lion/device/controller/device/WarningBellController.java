package com.lion.device.controller.device;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.dto.AddWarningBellDto;
import com.lion.device.entity.device.dto.UpdateWarningBellDto;
import com.lion.device.entity.device.vo.ListWarningBellVo;
import com.lion.device.service.device.WarningBellService;
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.expose.region.RegionWarningBellExposeService;
import com.lion.upms.entity.user.vo.DetailsUserTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午2:01
 */
@RestController
@RequestMapping("/warning/bell")
@Validated
@Api(tags = {"警示铃"})
public class WarningBellController extends BaseControllerImpl implements BaseController {

    @Autowired
    private WarningBellService warningBellService;

    @DubboReference
    private RegionWarningBellExposeService warningBellExposeService;

    @PostMapping("/add")
    @ApiOperation(value = "添加警示铃")
    public IResultData addUserType(@RequestBody @Validated({Validator.Insert.class}) AddWarningBellDto addWarningBellDto){
        warningBellService.add(addWarningBellDto);
        return ResultData.instance();
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改警示铃")
    public IResultData updateUserType(@RequestBody @Validated({Validator.Update.class}) UpdateWarningBellDto updateWarningBellDto){
        warningBellService.update(updateWarningBellDto);
        return ResultData.instance();
    }

    @GetMapping("/dind")
    @ApiOperation(value = "警示铃是否绑定区域")
    public IResultData<Boolean> isBind(@ApiParam(value = "警示铃id") @NotNull(message = "{0000000}") Long id){
        RegionWarningBell regionWarningBell = warningBellExposeService.find(id);
        return ResultData.instance().setData(Objects.nonNull(regionWarningBell));
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除警示铃")
    public IResultData deleteUserType(@RequestBody List<DeleteDto> deleteDtoList){
        warningBellService.delete(deleteDtoList);
        return ResultData.instance();
    }

    @GetMapping("/list")
    @ApiOperation(value = "警示铃列表")
    public IPageResultData<List<ListWarningBellVo>> listUserType(@ApiParam(value = "名称") String name, @ApiParam(value = "设备编码") String code, @ApiParam(value = "设备id") String warningBellId, @ApiParam(value = "所属科室")Long departmentId, LionPage LionPage){
        return warningBellService.list(name, code, warningBellId, departmentId, LionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "警示铃详情")
    public IResultData<DetailsUserTypeVo> detailsUserType(@ApiParam(value = "类型id") @NotNull(message = "{0000000}") Long id){
        return ResultData.instance().setData(warningBellService.details(id));
    }
}
