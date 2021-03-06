package com.lion.manage.controller.region;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.device.WarningBell;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.WarningBellExposeService;
import com.lion.manage.dao.region.RegionWarningBellDao;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.RegionType;
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.entity.region.dto.*;
import com.lion.manage.entity.region.vo.DetailsRegionTypeVo;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.entity.region.vo.ListRegionTypeVo;
import com.lion.manage.entity.region.vo.ListRegionVo;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.expose.ward.WardRoomExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionCctvService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.region.RegionTypeService;
import com.lion.manage.service.region.RegionWarningBellService;
import com.lion.manage.service.rule.WashTemplateItemService;
import com.lion.manage.service.rule.WashTemplateService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
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
 * @date 2021/4/1??????11:20
 */
@RestController
@RequestMapping("/region")
@Validated
@Api(tags = {"????????????"})
public class RegionController extends BaseControllerImpl implements BaseController {

    @Autowired
    private RegionService regionService;

    @Autowired
    private RegionCctvService regionCctvService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private CctvExposeService cctvExposeService;

//    @Autowired
//    private RegionExposeObjectService regionExposeObjectService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RegionTypeService regionTypeService;

    @Autowired
    private WardRoomService wardRoomService;

    @Autowired
    private WardRoomSickbedService wardRoomSickbedService;

    @Autowired
    private WashTemplateService washTemplateService;

    @DubboReference
    private WarningBellExposeService warningBellExposeService;

    @Autowired
    private RegionWarningBellService regionWarningBellService;


    @PostMapping("/add")
    @ApiOperation(value = "????????????")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddRegionDto addRegionDto){
        regionService.add(addRegionDto);
        return ResultData.instance();
    }

    @GetMapping("/name/exist")
    @ApiOperation(value = "????????????????????????")
    public IResultData<Boolean> nameExist(@ApiParam(value = "????????????") String name){
        Region region = regionService.findByName(name);
        return ResultData.instance().setData(Objects.nonNull(region));
    }

    @GetMapping("/code/exist")
    @ApiOperation(value = "????????????????????????")
    public IResultData<Boolean> codeExist(@ApiParam(value = "????????????") String code){
        Region region = regionService.findByCode(code);
        return ResultData.instance().setData(Objects.nonNull(region));
    }

    @GetMapping("/list")
    @ApiOperation(value = "????????????")
    public IPageResultData<List<ListRegionVo>> list(@ApiParam(value = "????????????") String name,@ApiParam(value = "??????????????????id")Long washTemplateId,@ApiParam(value = "????????????id")Long regionTypeId,@ApiParam(value = "??????id")Long buildId, @ApiParam(value = "????????????id")Long buildFloorId, LionPage lionPage){
        return regionService.list(name,null,null,washTemplateId,regionTypeId,buildId,buildFloorId,lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "????????????")
    public IResultData<DetailsRegionVo> details(@NotNull(message = "{0000000}") Long id){
        return ResultData.instance().setData(regionService.details(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "????????????")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateRegionDto updateRegionDto){
        regionService.update(updateRegionDto);
        return ResultData.instance();
    }

    @PutMapping("/update/batch")
    @ApiOperation(value = "????????????????????????????????????")
    public IResultData batchUpdateWashTemplate(@RequestBody BatchUpdateWashTemplateDto batchUpdateWashTemplateDto) {
        regionService.batchUpdateWashTemplate(batchUpdateWashTemplateDto);
        return ResultData.instance();
    }

    @PutMapping("/update/coordinates")
    @ApiOperation(value = "????????????????????????")
    public IResultData updateCoordinates(@RequestBody @Validated({Validator.OtherOne.class}) UpdateRegionCoordinatesDto updateRegionCoordinatesDto){
        regionService.updateCoordinates(updateRegionCoordinatesDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "????????????")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        regionService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/type/add")
    @ApiOperation(value = "??????????????????")
    public IResultData addUserType(@RequestBody @Validated({Validator.Insert.class}) AddRegionTypeDto addRegionTypeDto){
        regionTypeService.add(addRegionTypeDto);
        return ResultData.instance();
    }

    @PutMapping("/type/update")
    @ApiOperation(value = "??????????????????")
    public IResultData updateUserType(@RequestBody @Validated({Validator.Update.class}) UpdateRegionTypeDto updateRegionTypeDto){
        regionTypeService.update(updateRegionTypeDto);
        return ResultData.instance();
    }

    @DeleteMapping("/type/delete")
    @ApiOperation(value = "??????????????????")
    public IResultData deleteUserType(@RequestBody List<DeleteDto> deleteDtoList){
        regionTypeService.delete(deleteDtoList);
        return ResultData.instance();
    }

    @GetMapping("/type/list")
    @ApiOperation(value = "??????????????????")
    public IPageResultData<List<ListRegionTypeVo>> listUserType(@ApiParam(value = "????????????") String regionTypeName, LionPage LionPage){
        return regionTypeService.list(regionTypeName, LionPage);
    }

    @GetMapping("/type/details")
    @ApiOperation(value = "??????????????????")
    public IResultData<DetailsRegionTypeVo> detailsUserType(@ApiParam(value = "??????id") @NotNull(message = "{0000000}") Long id){
        com.lion.core.Optional<RegionType> optional = regionTypeService.findById(id);
        if (optional.isPresent()) {
            DetailsRegionTypeVo vo = new DetailsRegionTypeVo();
            BeanUtils.copyProperties(optional.get(), vo);
            return ResultData.instance().setData(vo);
        }
        return ResultData.instance();
    }
}
