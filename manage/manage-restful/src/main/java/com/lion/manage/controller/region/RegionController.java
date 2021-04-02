package com.lion.manage.controller.region;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.dto.AddBuildDto;
import com.lion.manage.entity.build.dto.UpdateBuildDto;
import com.lion.manage.entity.build.vo.DetailsBuildVo;
import com.lion.manage.entity.build.vo.ListBuildVo;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.RegionExposeObject;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.service.region.RegionCctvService;
import com.lion.manage.service.region.RegionExposeObjectService;
import com.lion.manage.service.region.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
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
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:20
 */
@RestController
@RequestMapping("/region")
@Validated
@Api(tags = {"区域管理"})
public class RegionController extends BaseControllerImpl implements BaseController {

    @Autowired
    private RegionService regionService;

    @Autowired
    private RegionCctvService regionCctvService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @Autowired
    private RegionExposeObjectService regionExposeObjectService;

    @PostMapping("/add")
    @ApiOperation(value = "新增区域")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddRegionDto addRegionDto){
        regionService.add(addRegionDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "区域列表")
    public IPageResultData<List<Region>> list(@ApiParam(value = "区域名称") String name, @ApiParam(value = "建筑id")Long buildId, @ApiParam(value = "建筑楼层id")Long buildFloorId, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(buildId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_buildId",buildId);
        }
        if (Objects.nonNull(buildFloorId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_buildFloorId",buildFloorId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) regionService.findNavigator(lionPage);
        return page;
    }

    @GetMapping("/details")
    @ApiOperation(value = "区域详情")
    public IResultData<DetailsRegionVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        Region region = this.regionService.findById(id);
        if (Objects.nonNull(region)){
            DetailsRegionVo detailsRegionVo = new DetailsRegionVo();
            BeanUtils.copyProperties(region,detailsRegionVo);
            detailsRegionVo.setDevices(deviceExposeService.findByDeviceGruopId(region.getDeviceGroupId()));
            List<RegionCctv> list = regionCctvService.find(region.getId());
            List<Long> cctvIds = new ArrayList<>();
            list.forEach(regionCctv -> {
                cctvIds.add(regionCctv.getCctvId());
            });
            if (cctvIds.size()>0) {
                detailsRegionVo.setCctvs(cctvExposeService.find(cctvIds));
            }
            detailsRegionVo.setExposeObjects(regionExposeObjectService.find(region.getId()));
            resultData.setData(detailsRegionVo);
        }
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改区域")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateRegionDto updateRegionDto){
        regionService.update(updateRegionDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除区域")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        regionService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }
}
