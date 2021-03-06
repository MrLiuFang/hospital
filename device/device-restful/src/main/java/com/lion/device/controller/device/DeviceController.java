package com.lion.device.controller.device;

import com.lion.common.expose.file.FileExposeService;
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
//import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.dto.AddDeviceDto;
import com.lion.device.entity.device.dto.AddDeviceGroupDto;
import com.lion.device.entity.device.dto.UpdateDeviceDto;
import com.lion.device.entity.device.dto.UpdateDeviceGroupDto;
import com.lion.device.entity.device.vo.DetailsDeviceGroupVo;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.device.vo.DeviceStatisticsVo;
import com.lion.device.entity.device.vo.ListDeviceGroupVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.State;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.service.device.DeviceGroupService;
import com.lion.device.service.device.DeviceService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
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
import com.lion.core.Optional;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31??????1:50
 */
@RestController
@RequestMapping("/device")
@Validated
@Api(tags = {"????????????"})
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

    @DubboReference
    private FileExposeService fileExposeService;

    @PostMapping("/add")
    @ApiOperation(value = "????????????")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class})AddDeviceDto addDeviceDto){
        Device device = new Device();
        addDeviceDto.setDeviceState(State.ACTIVE);
        BeanUtils.copyProperties(addDeviceDto,device);
        this.deviceService.save(device);
        return ResultData.instance();
    }

    @PutMapping("/update")
    @ApiOperation(value = "????????????")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateDeviceDto updateDeviceDto){
        Device device = new Device();
        if (Objects.equals(updateDeviceDto.getDeviceState(),State.NOT_ACTIVE)) {
            updateDeviceDto.setDeviceState(null);
        }
        BeanUtils.copyProperties(updateDeviceDto,device);
        this.deviceService.update(device);
        return ResultData.instance();
    }

    @GetMapping("/dind")
    @ApiOperation(value = "????????????????????????")
    public IResultData<List<Long>> isBind(@ApiParam(value = "??????id-????????????") @NotNull(message = "{0000000}") String ids){
        List<Long> returnList = new ArrayList<>();
        String[] id = ids.split(",");
        if (Objects.nonNull(id) && id.length>0) {
            for (int i = 0 ; i<id.length ;i ++){
                com.lion.core.Optional<Device> optional = deviceService.findById(Long.valueOf(id[i]));
                if (optional.isPresent()){
                    Device device = optional.get();
                    if (Objects.nonNull(device.getRegionId()) && !Objects.equals(DeviceClassify.STAR_AP,device.getDeviceClassify())) {
                        returnList.add(device.getId());
                    }
                }
            }
        }
        return ResultData.instance().setData(returnList);
    }

    @GetMapping("/list")
    @ApiOperation(value = "????????????")
    public IPageResultData<List<Device>> list( @ApiParam(value = "????????????-??????")Boolean isTmp,@ApiParam(value = "??????ID")Long regionId,@ApiParam(value = "??????????????????")Boolean isBind, @ApiParam(value = "??????")Integer battery, @ApiParam(value = "?????????ID") Long deviceGroupId,@ApiParam(value = "????????????") String name, @ApiParam(value = "????????????") String code, @ApiParam(value = "????????????") DeviceClassify deviceClassify,@ApiParam(value = "????????????")  DeviceType deviceType, LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.equals(true,isTmp)){
            jpqlParameter.setSearchParameter(SearchConstant.IS_NULL+"_name",null);
        }else {
            jpqlParameter.setSearchParameter(SearchConstant.IS_NOT_NULL+"_name",null);
        }
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(battery)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_battery",battery);
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
        if (Objects.equals(isBind,true)) {
            jpqlParameter.setSearchParameter(SearchConstant.IS_NOT_NULL+"_regionId",null);
        }else if (Objects.equals(isBind,false)) {
            jpqlParameter.setSearchParameter(SearchConstant.IS_NULL+"_regionId",null);
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
        if (Objects.nonNull(regionId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_regionId",regionId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<Device>>) deviceService.findNavigator(lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "????????????")
    public IResultData<DetailsDeviceVo> details(@ApiParam(value = "??????id") @NotNull(message = "{0000000}") Long id){
        return ResultData.instance().setData(deviceService.details(id));
    }

    @ApiOperation(value = "????????????")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        List<Device> list = deviceService.delete(deleteDtoList);
        if (list.size()>0) {
            deviceService.saveAll(list);
        }
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/group/add")
    @ApiOperation(value = "???????????????")
    public IResultData groupAdd(@RequestBody @Validated({Validator.Insert.class}) AddDeviceGroupDto addDeviceGroupDto){
        ResultData resultData = ResultData.instance();
        deviceGroupService.add(addDeviceGroupDto);
        return resultData;
    }

    @PutMapping("/group/update")
    @ApiOperation(value = "???????????????")
    public IResultData groupUpdate(@RequestBody @Validated({Validator.Update.class}) UpdateDeviceGroupDto updateDeviceGroupDto){
        ResultData resultData = ResultData.instance();
        deviceGroupService.update(updateDeviceGroupDto);
        return resultData;
    }

    @GetMapping("/group/list")
    @ApiOperation(value = "???????????????")
    public IPageResultData<List<ListDeviceGroupVo>> groupList(@ApiParam(value = "???????????????") String name, @ApiParam(value = "???????????????") String code, LionPage lionPage ){
        return deviceGroupService.list(name, code, lionPage);
    }

    @GetMapping("/group/details")
    @ApiOperation(value = "???????????????")
    public IResultData<DetailsDeviceGroupVo> groupDetails(@ApiParam(value = "?????????id") @NotNull(message = "{0000000}")Long id) {
        ResultData resultData = ResultData.instance();
        resultData.setData(deviceGroupService.details(id));
        return resultData;
    }

    @ApiOperation(value = "???????????????")
    @DeleteMapping("/group/delete")
    public IResultData groupDelete(@RequestBody List<DeleteDto> deleteDtoList){
        deviceGroupService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/statistics")
    @ApiOperation(value = "??????????????????")
    public IResultData<DeviceStatisticsVo> deviceStatistics(){
        ResultData resultData = ResultData.instance();
        resultData.setData(deviceService.statistics());
        return resultData;
    }

    @GetMapping("/state")
    @ApiOperation(value = "????????????(?????????????????????????????????)")
    public IPageResultData<List<Device>> deviceState(LionPage lionPage) {
        return (IPageResultData<List<Device>>) deviceService.deviceState(lionPage);
    }

}
