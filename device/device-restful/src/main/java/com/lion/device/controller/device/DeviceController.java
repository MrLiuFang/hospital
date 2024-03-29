package com.lion.device.controller.device;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.device.Device;
//import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.dto.*;
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
import com.lion.event.expose.service.CurrentPositionExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private CurrentPositionExposeService currentPositionExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ApiOperation(value = "新增设备")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class})AddDeviceDto addDeviceDto){
        Device device = new Device();
//        addDeviceDto.setDeviceState(State.NOT_USED);
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

    @GetMapping("/dind")
    @ApiOperation(value = "设备是否绑定区域")
    public IResultData<List<Long>> isBind(@ApiParam(value = "设备id-逗号隔开") @NotNull(message = "{0000000}") String ids){
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
    @ApiOperation(value = "设备列表")
    public IPageResultData<List<Device>> list( @ApiParam(value = "是否临时-导入") @RequestParam(defaultValue = "false",required = false)String isTmp,@ApiParam(value = "区域ID")Long regionId,@ApiParam(value = "是否绑定区域")Boolean isBind, @ApiParam(value = "电量")Integer battery, @ApiParam(value = "设备组ID") Long deviceGroupId,@ApiParam(value = "设备名称") String name, @ApiParam(value = "设备编号") String code, @ApiParam(value = "设备大类") DeviceClassify deviceClassify,@ApiParam(value = "设备分类")  DeviceType deviceType, LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(isTmp) && Objects.equals("true",isTmp.toLowerCase())){
            jpqlParameter.setSearchParameter(SearchConstant.IS_NULL+"_name",null);
        }else if((Objects.nonNull(isTmp) && Objects.equals("false",isTmp.toLowerCase()))){
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
    @ApiOperation(value = "设备详情")
    public IResultData<DetailsDeviceVo> details(@ApiParam(value = "设备id") @NotNull(message = "{0000000}") Long id){
        return ResultData.instance().setData(deviceService.details(id));
    }

    @ApiOperation(value = "删除设备")
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
    @ApiOperation(value = "新增设备组")
    public IResultData groupAdd(@RequestBody @Validated({Validator.Insert.class}) AddDeviceGroupDto addDeviceGroupDto){
        ResultData resultData = ResultData.instance();
        deviceGroupService.add(addDeviceGroupDto);
        return resultData;
    }

    @PutMapping("/group/update")
    @ApiOperation(value = "修改设备组")
    public IResultData groupUpdate(@RequestBody @Validated({Validator.Update.class}) UpdateDeviceGroupDto updateDeviceGroupDto){
        ResultData resultData = ResultData.instance();
        deviceGroupService.update(updateDeviceGroupDto);
        return resultData;
    }

    @GetMapping("/group/list")
    @ApiOperation(value = "设备组列表")
    public IPageResultData<List<ListDeviceGroupVo>> groupList(@ApiParam(value = "设备组名称") String name, @ApiParam(value = "设备组编号") String code, LionPage lionPage ){
        return deviceGroupService.list(name, code, lionPage);
    }

    @GetMapping("/group/details")
    @ApiOperation(value = "设备组详情")
    public IResultData<DetailsDeviceGroupVo> groupDetails(@ApiParam(value = "设备组id") @NotNull(message = "{0000000}")Long id) {
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

    @GetMapping("/state")
    @ApiOperation(value = "硬件状态(根据是否故障和电量排序)")
    public IPageResultData<List<Device>> deviceState(LionPage lionPage) {
        return (IPageResultData<List<Device>>) deviceService.deviceState(lionPage);
    }

    @PutMapping("/replace")
    @ApiOperation(value = "替换设备")
    public IResultData replace(@RequestBody ReplaceDeviceDto replaceDeviceDto){
//        deviceService.replace(replaceDeviceDto);
        Optional<Device> deviceOptionalOld = deviceService.findById(replaceDeviceDto.getOldId());
        if (deviceOptionalOld.isEmpty()) {
            BusinessException.throwException("被替换的设备无效");
        }
        Device oldDevice = deviceOptionalOld.get();
        Optional<Device> deviceOptionalNew = deviceService.findById(replaceDeviceDto.getNewId());
        if (deviceOptionalNew.isEmpty()) {
            BusinessException.throwException("替换的设备无效");
        }
        Device newDevice = deviceOptionalNew.get();
        if (Objects.isNull(newDevice.getCode())){
            BusinessException.throwException("替换的设备是临时设备");
        }
        if (Objects.nonNull(newDevice.getRegionId())) {
            BusinessException.throwException("替换的设备已经绑定区域");
        }
        newDevice.setRegionId(oldDevice.getRegionId());
        newDevice.setDeviceState(State.USED);
//        newDevice.setCode(replaceDeviceDto.getCode());
//        newDevice.setName(replaceDeviceDto.getName());
//        newDevice.setImg(replaceDeviceDto.getImg());
        deviceService.delete(oldDevice);
        deviceService.update(newDevice);
        redisTemplate.delete(RedisConstants.DEVICE+oldDevice.getId());
        redisTemplate.delete(RedisConstants.DEVICE_CODE+oldDevice.getCode());
        redisTemplate.delete(RedisConstants.DEVICE_REGION+oldDevice.getId());
        currentPositionExposeService.delete(null,oldDevice.getId(),null);
        Device newDevice1 = new Device();
        newDevice1.setDeviceState(State.NOT_USED);
        newDevice1.setCode(oldDevice.getCode());
        newDevice1.setDeviceType(oldDevice.getDeviceType());
        newDevice1.setDeviceClassify(oldDevice.getDeviceClassify());
        deviceService.save1(newDevice1);
        return ResultData.instance();
    }

}
