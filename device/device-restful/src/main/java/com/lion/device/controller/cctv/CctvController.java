package com.lion.device.controller.cctv;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.Optional;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.cctv.dto.AddCctvDto;
import com.lion.device.entity.cctv.dto.UpdateCctvDto;
import com.lion.device.entity.cctv.vo.CctvVo;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.device.service.cctv.CctvService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionCctvExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
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
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private RegionCctvExposeService regionCctvExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @GetMapping("/list")
    @ApiOperation(value = "设备列表")
    public IPageResultData<List<CctvVo>> list(@ApiParam(value = "所属区域") String regionId, @ApiParam(value = "cctv名称") String name, @ApiParam(value = "cctvId") String cctvId, @ApiParam(value = "状态")Boolean isOnline, LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(cctvId)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_cctvId",cctvId);
        }
        if (Objects.nonNull(isOnline)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isOnline",isOnline);
        }
        if (Objects.nonNull(regionId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_regionId",regionId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Map<String, Object> sortParameter = new HashMap();
        Page<Cctv> page = cctvService.findNavigator(lionPage);
        List<Cctv> list = page.getContent();
        List<CctvVo>  returnList= new ArrayList<>();
        list.forEach(cctv -> {
            returnList.add(convertVo(cctv));
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加设备")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddCctvDto addCctvDto){
        Cctv cctv = new Cctv();
        BeanUtils.copyProperties(addCctvDto,cctv);
        this.cctvService.save(cctv);
        return ResultData.instance();
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改设备")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateCctvDto updateCctvDto){
        Cctv cctv = new Cctv();
        BeanUtils.copyProperties(updateCctvDto,cctv);
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
                RegionCctv regionCctv = regionCctvExposeService.find(Long.valueOf(id[i]));
                if (Objects.nonNull(regionCctv)) {
                    returnList.add(regionCctv.getCctvId());
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
            resultData.setData(convertVo(optional.get()));
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

    private CctvVo convertVo(Cctv cctv) {
        if (Objects.isNull(cctv)) {
            return null;
        }
        CctvVo vo = new CctvVo();
        BeanUtils.copyProperties(cctv,vo);

        com.lion.core.Optional<Build> optionalBuild = buildExposeService.findById(cctv.getBuildId());
        if (optionalBuild.isPresent()){
            vo.setBuildName(optionalBuild.get().getName());
        }

        com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorExposeService.findById(cctv.getBuildFloorId());
        if (optionalBuildFloor.isPresent()){
            vo.setBuildFloorName(optionalBuildFloor.get().getName());
        }

        com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(cctv.getRegionId());
        if (optionalRegion.isPresent()){
            vo.setRegionName(optionalRegion.get().getName());
        }

        com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(cctv.getDepartmentId());
        if (optionalDepartment.isPresent()){
            vo.setDepartmentName(optionalDepartment.get().getName());
        }

        Optional<User> createUserOptional = userExposeService.findById(cctv.getCreateUserId());
        if (createUserOptional.isPresent()) {
            vo.setCreateUserName(createUserOptional.get().getName());
            vo.setCreateUserHeadPortraitUrl(fileExposeService.getUrl(createUserOptional.get().getHeadPortrait()));
            vo.setCreateUserHeadPortrait(createUserOptional.get().getHeadPortrait());
        }
        Optional<User> updateUserOptional = userExposeService.findById(cctv.getCreateUserId());
        if (updateUserOptional.isPresent()) {
            vo.setUpdateUserName(updateUserOptional.get().getName());
            vo.setUpdateUserHeadPortraitUrl(fileExposeService.getUrl(updateUserOptional.get().getHeadPortrait()));
            vo.setUpdateUserHeadPortrait(updateUserOptional.get().getHeadPortrait());
        }


        return vo;
    }
    @PostMapping("/import")
    @ApiOperation(value = "导入")
    public IResultData importCctv(@ApiIgnore StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        this.cctvService.importCctv(multipartHttpServletRequest);
        return ResultData.instance();
    }


}
