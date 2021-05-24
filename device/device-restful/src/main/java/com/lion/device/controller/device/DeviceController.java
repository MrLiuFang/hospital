package com.lion.device.controller.device;

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
import com.lion.device.entity.device.DeviceGroup;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.dto.AddDeviceGroupDto;
import com.lion.device.entity.device.dto.UpdateDeviceGroupDto;
import com.lion.device.entity.device.vo.DetailsDeviceGroupVo;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.device.vo.DeviceStatisticsVo;
import com.lion.device.entity.device.vo.ListDeviceGroupVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.service.device.DeviceGroupService;
import com.lion.device.service.device.DeviceService;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.dto.AddDeviceDto;
import com.lion.device.entity.device.dto.UpdateDeviceDto;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:50
 */
@RestController
@RequestMapping("/device")
@Validated
@Api(tags = {"设备管理"})
public class DeviceController extends BaseControllerImpl implements BaseController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceGroupDeviceService deviceGroupDeviceService;

    @Autowired
    private DeviceGroupService deviceGroupService;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @PostMapping("/add")
    @ApiOperation(value = "新增设备")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class})AddDeviceDto addDeviceDto){
        Device device = new Device();
        BeanUtils.copyProperties(addDeviceDto,device);
        this.deviceService.save(device);
        return ResultData.instance();
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改设备")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateDeviceDto updateDeviceDto){
        Device device = new Device();
        BeanUtils.copyProperties(updateDeviceDto,device);
        this.deviceService.update(device);
        return ResultData.instance();
    }

    @GetMapping("/list")
    @ApiOperation(value = "设备列表")
    public IPageResultData<List<Device>> list(@ApiParam(value = "设备组ID") Long deviceGroupId,@ApiParam(value = "设备名称") String name, @ApiParam(value = "设备编号") String code, @ApiParam(value = "设备大类") DeviceClassify deviceClassify,@ApiParam(value = "设备分类")  DeviceType deviceType, LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        if (Objects.nonNull(deviceClassify)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_deviceClassify",deviceClassify);
        }
        if (Objects.nonNull(deviceType)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_deviceType",deviceType);
        }
        if (Objects.nonNull(deviceGroupId)){
            List<DeviceGroupDevice> list = deviceGroupDeviceService.find(deviceGroupId);
            List<Long> ids = new ArrayList<>();
            list.forEach(deviceGroupDevice -> {
                ids.add(deviceGroupDevice.getDeviceId());
            });
            if (ids.size()>0) {
                jpqlParameter.setSearchParameter(SearchConstant.IN, ids);
            }
        }

        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<Device>>) deviceService.findNavigator(lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "设备详情")
    public IResultData<DetailsDeviceVo> details(@ApiParam(value = "设备id") @NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        Device device = deviceService.findById(id);
        DetailsDeviceVo detailsDeviceVo = new DetailsDeviceVo();
        BeanUtils.copyProperties(device,detailsDeviceVo);
        if (Objects.nonNull(device.getBuildId())){
            Build build = buildExposeService.findById(device.getBuildId());
            if (Objects.nonNull(build)){
                detailsDeviceVo.setBuildName(build.getName());
            }
        }
        if (Objects.nonNull(device.getBuildFloorId())){
            BuildFloor buildFloor = buildFloorExposeService.findById(device.getBuildFloorId());
            if (Objects.nonNull(buildFloor)){
                detailsDeviceVo.setBuildFloorName(buildFloor.getName());
                detailsDeviceVo.setMapUrl(buildFloor.getMapUrl());
            }
        }

        resultData.setData(detailsDeviceVo);
        return resultData;
    }

    @ApiOperation(value = "删除设备")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        deviceService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/group/add")
    @ApiOperation(value = "新增设备组")
    public IResultData groupAdd(@RequestBody @Validated({Validator.Insert.class})AddDeviceGroupDto addDeviceGroupDto){
        ResultData resultData = ResultData.instance();
        deviceGroupService.add(addDeviceGroupDto);
        return resultData;
    }

    @PutMapping("/group/update")
    @ApiOperation(value = "修改设备组")
    public IResultData groupUpdate(@RequestBody @Validated({Validator.Update.class})UpdateDeviceGroupDto updateDeviceGroupDto){
        ResultData resultData = ResultData.instance();
        deviceGroupService.update(updateDeviceGroupDto);
        return resultData;
    }

    @GetMapping("/group/list")
    @ApiOperation(value = "设备组列表")
    public IPageResultData<List<ListDeviceGroupVo>> groupList(@ApiParam(value = "设备组名称") String name,@ApiParam(value = "设备组编号") String code,LionPage lionPage ){
        return deviceGroupService.list(name, code, lionPage);
    }

    @GetMapping("/group/details")
    @ApiOperation(value = "设备组详情")
    public IResultData<DetailsDeviceGroupVo> groupDetails(@ApiParam(value = "设备组id") @NotNull(message = "id不能为空")Long id) {
        ResultData resultData = ResultData.instance();
        resultData.setData(deviceGroupService.details(id));
        return resultData;
    }

    @ApiOperation(value = "删除设备组")
    @DeleteMapping("/group/delete")
    public IResultData groupDelete(@RequestBody List<DeleteDto> deleteDtoList){
        deviceGroupService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/statistics")
    @ApiOperation(value = "硬件设备统计")
    public IResultData<DeviceStatisticsVo> deviceStatistics(){
        ResultData resultData = ResultData.instance();
        resultData.setData(deviceService.statistics());
        return resultData;
    }



}
