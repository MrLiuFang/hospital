package com.lion.device.controller.cctv;

import com.lion.core.*;
import com.lion.core.Optional;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.cctv.dto.AddCctvDto;
import com.lion.device.entity.cctv.dto.UpdateCctvDto;
import com.lion.device.entity.cctv.vo.CctvVo;
import com.lion.device.service.cctv.CctvService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionCctvExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

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

    @DubboReference
    private RegionCctvExposeService regionCctvExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @GetMapping("/list")
    @ApiOperation(value = "设备列表")
    public IPageResultData<List<CctvVo>> list(@ApiParam(value = "所属区域") String regionId, @ApiParam(value = "cctv名称") String name, @ApiParam(value = "cctvId") String cctvId, @ApiParam(value = "状态")Boolean isOnline, LionPage lionPage){
        return cctvService.list(regionId, name, cctvId, isOnline,null , lionPage);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加设备")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddCctvDto addCctvDto){
        Cctv cctv = new Cctv();
        BeanUtils.copyProperties(addCctvDto,cctv);
        Optional<Region> regionOptional = regionExposeService.findById(addCctvDto.getRegionId());
        if (regionOptional.isPresent()) {
            Region region = regionOptional.get();
            cctv.setBuildId(region.getBuildId());
            cctv.setBuildFloorId(region.getBuildFloorId());
            cctv.setRegionId(region.getId());
            cctv.setDepartmentId(region.getDepartmentId());
        }
        this.cctvService.save(cctv);
        return ResultData.instance();
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改设备")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateCctvDto updateCctvDto){
        Cctv cctv = new Cctv();
        BeanUtils.copyProperties(updateCctvDto,cctv);
        Optional<Region> regionOptional = regionExposeService.findById(updateCctvDto.getRegionId());
        if (regionOptional.isPresent()) {
            Region region = regionOptional.get();
            cctv.setBuildId(region.getBuildId());
            cctv.setBuildFloorId(region.getBuildFloorId());
            cctv.setRegionId(region.getId());
            cctv.setDepartmentId(region.getDepartmentId());
        }
        this.cctvService.update(cctv);
        return ResultData.instance();
    }

    @GetMapping("/dind")
    @ApiOperation(value = "CCTV是否绑定区域")
    public IResultData<List<Long>> isBind(@ApiParam(value = "cctvid-逗号隔开") @NotBlank(message = "{0000000}") String ids) {
        List<Long> returnList = new ArrayList<>();
        String[] id = ids.split(",");
        if (Objects.nonNull(id) && id.length > 0) {
            for (int i = 0; i < id.length; i++) {
                Optional<Cctv> cctvOptional = cctvService.findById(id[i]);
                if (cctvOptional.isPresent()) {
                    returnList.add(cctvOptional.get().getId());
                }
            }
        }
        return ResultData.instance().setData(returnList);
    }

    @GetMapping("/details")
    @ApiOperation(value = "cctv详情")
    public IResultData<CctvVo> details(@ApiParam(value = "id") @NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        com.lion.core.Optional<Cctv> optional = cctvService.findById(id);
        if (optional.isPresent()) {
            resultData.setData(cctvService.convertVo(optional.get()));
        }
        return resultData;
    }

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(deleteDto -> {
            cctvService.deleteById(deleteDto.getId());
        });
        return ResultData.instance();
    }


    @PostMapping("/import")
    @ApiOperation(value = "导入")
    public IResultData importCctv(@ApiIgnore StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        this.cctvService.importCctv(multipartHttpServletRequest);
        return ResultData.instance();
    }

    @GetMapping("/export")
    @ApiOperation(value = "导出")
    public void export(@ApiParam(value = "所属区域") String regionId, @ApiParam(value = "cctv名称") String name, @ApiParam(value = "cctvId") String cctvId, @ApiParam(value = "状态")Boolean isOnline, String ids, LionPage lionPage) throws IOException, IllegalAccessException {
        cctvService.export(regionId, name, cctvId, isOnline, ids, lionPage);
    }


}
