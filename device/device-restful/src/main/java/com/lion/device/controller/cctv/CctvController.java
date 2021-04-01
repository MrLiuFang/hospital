package com.lion.device.controller.cctv;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.cctv.dto.UpdateCctvDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.dto.UpdateDeviceDto;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.service.cctv.CctvService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:36
 */
@RestController
@RequestMapping("/cctv")
@Validated
@Api(tags = {"CCTV管理"})
public class CctvController extends BaseControllerImpl implements BaseController {

    @Autowired
    private CctvService cctvService;

    @GetMapping("/list")
    @ApiOperation(value = "设备列表")
    public IPageResultData<List<Cctv>> list(@ApiParam(value = "cctv名称") String name, @ApiParam(value = "cctv编号") String code, @ApiParam(value = "型号")String model, LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<Cctv>>) cctvService.findNavigator(lionPage);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改设备")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateCctvDto updateCctvDto){
        Cctv cctv = new Cctv();
        BeanUtils.copyProperties(updateCctvDto,cctv);
        this.cctvService.update(cctv);
        return ResultData.instance();
    }



    @GetMapping("/details")
    @ApiOperation(value = "cctv详情")
    public IResultData<Device> details(@ApiParam(value = "id") @NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(cctvService.findById(id));
        return resultData;
    }
}
