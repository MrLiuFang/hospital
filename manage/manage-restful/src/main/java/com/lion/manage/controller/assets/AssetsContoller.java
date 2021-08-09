package com.lion.manage.controller.assets;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.assets.dto.*;
import com.lion.manage.entity.assets.vo.*;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.entity.enums.AssetsType;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.service.assets.AssetsBorrowService;
import com.lion.manage.service.assets.AssetsFaultReportService;
import com.lion.manage.service.assets.AssetsFaultService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.role.RoleExposeService;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.catalina.authenticator.SingleSignOnSessionKey;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sun.util.resources.ga.LocaleNames_ga;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:20
 */
@RestController
@RequestMapping("/assets")
@Validated
@Api(tags = {"资产管理"})
public class AssetsContoller extends BaseControllerImpl implements BaseController {

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private AssetsBorrowService assetsBorrowService;

    @Autowired
    private AssetsFaultService assetsFaultService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentService departmentService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private RegionService regionService;

    @DubboReference
    private RoleExposeService roleExposeService;

    @Autowired
    private AssetsFaultReportService assetsFaultReportService;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private TagAssetsExposeService tagAssetsExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @PostMapping("/add")
    @ApiOperation(value = "新增资产")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class})AddAssetsDto addAssetsDto){
        assetsService.add(addAssetsDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "资产列表")
    public IPageResultData<List<ListAssetsVo>> list(@ApiParam(value = "资产名称") String name, @ApiParam(value = "资产编号") String code,@ApiParam(value = "科室id")Long departmentId,Boolean isMyDepartment,
                                                  @ApiParam(value = "资产分类") AssetsType type, @ApiParam(value = "使用状态") AssetsUseState useState, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        if (Objects.nonNull(type)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_type",type);
        }
        if (Objects.nonNull(useState)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_useState",useState);
        }
        if (Objects.equals(isMyDepartment,true)) {
            Department department = departmentUserExposeService.findDepartment(CurrentUserUtil.getCurrentUserId());
            if (Objects.nonNull(department)){
                departmentId = department.getId();
            }
        }
        if (Objects.nonNull(departmentId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Assets> page = assetsService.findNavigator(lionPage);
        List<Assets> list = page.getContent();
        List<ListAssetsVo> listAssetsVos = new ArrayList<ListAssetsVo>();
        list.forEach(assets -> {
            ListAssetsVo listAssetsVo = new ListAssetsVo();
            BeanUtils.copyProperties(assets,listAssetsVo);
            if (Objects.nonNull(assets.getBuildId())){
                Build build = buildService.findById(assets.getBuildId());
                if (Objects.nonNull(build)){
                    listAssetsVo.setPosition(build.getName());
                }
            }
            if (Objects.nonNull(assets.getBuildFloorId())){
                BuildFloor buildFloor = buildFloorService.findById(assets.getBuildFloorId());
                if (Objects.nonNull(buildFloor)){
                    listAssetsVo.setPosition(listAssetsVo.getPosition()+buildFloor.getName());
                }
            }
            if (Objects.nonNull(assets.getDepartmentId())){
                Department department = departmentService.findById(assets.getDepartmentId());
                if (Objects.nonNull(department)){
                    listAssetsVo.setDepartmentName(department.getName());
                    TagAssets tagAssets = tagAssetsExposeService.find(assets.getId());
                    if (Objects.nonNull(tagAssets)) {
                        Tag tag = tagExposeService.findById(tagAssets.getTagId());
                        if (Objects.nonNull(tag)) {
                            listAssetsVo.setTagCode(tag.getTagCode());
                        }
                    }
                }
            }
            listAssetsVos.add(listAssetsVo);
        });
        return new PageResultData(listAssetsVos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/details")
    @ApiOperation(value = "资产详情")
    public IResultData<DetailsAssetsVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(assetsService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改资产")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateAssetsDto updateAssetsDto){
        assetsService.update(updateAssetsDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除资产")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        assetsService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/borrw/add")
    @ApiOperation(value = "新增资产借用")
    public IResultData addBorrw(@RequestBody @Validated AddAssetsBorrowDto addAssetsBorrowDto){
        assetsBorrowService.add(addAssetsBorrowDto);
        return ResultData.instance();
    }


    @GetMapping("/borrw/list")
    @ApiOperation(value = "资产借用列表")
    public IPageResultData<List<ListAssetsBorrowVo>> listBorrw(@ApiParam(value = "资产名称")String name,@ApiParam(value = "登记人/借用人")Long borrowUserId,@ApiParam(value = "资产类型")AssetsType type,@ApiParam(value = "科室id") Long departmentId, @ApiParam(value = "资产id") Long assetsId, @ApiParam(value = "借用开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "借用结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                               @ApiParam(value = "是否已归还(null值 查所有)") Boolean isReturn, LionPage lionPage){
        return assetsBorrowService.list(name, borrowUserId, type, departmentId, assetsId, startDateTime, endDateTime, isReturn, lionPage);
    }

//    @GetMapping("/borrw/details")
//    @ApiOperation(value = "资产借用详情")
//    public IResultData<DetailsAssetsBorrowVo> detailsBorrw(@NotNull(message = "id不能为空") Long id){
//        ResultData resultData = ResultData.instance();
//        AssetsBorrow assetsBorrow = this.assetsBorrowService.findById(id);
//        if (Objects.nonNull(assetsBorrow)){
//            DetailsAssetsBorrowVo vo = new DetailsAssetsBorrowVo();
//            BeanUtils.copyProperties(assetsBorrow,vo);
//            if (Objects.nonNull(assetsBorrow.getBorrowUserId())){
//                User user = userExposeService.findById(assetsBorrow.getBorrowUserId());
//                if (Objects.nonNull(user)){
//                    vo.setBorrowUserName(user.getName());
//                }
//            }
//            if (Objects.nonNull(assetsBorrow.getReturnUserId())){
//                User user = userExposeService.findById(assetsBorrow.getReturnUserId());
//                if (Objects.nonNull(user)){
//                    vo.setReturnUserName(user.getName());
//                }
//            }
//            resultData.setData(vo);
//        }
//        return resultData;
//    }

    @PutMapping("/borrw/return")
    @ApiOperation(value = "归还资产")
    public IResultData returnBorrw(@RequestBody @Validated({Validator.Update.class}) ReturnAssetsBorrowDto returnAssetsBorrowDto){
        assetsBorrowService.returnAssetsBorrow(returnAssetsBorrowDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除资产借用")
    @DeleteMapping("/borrw/delete")
    public IResultData deleteBorrw(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(deleteDto -> {
            this.assetsBorrowService.deleteById(deleteDto.getId());
        });
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/fault/add")
    @ApiOperation(value = "新增资产故障")
    public IResultData addFault(@RequestBody @Validated({Validator.Insert.class}) AddAssetsFaultDto addAssetsFaultDto){
        assetsFaultService.add(addAssetsFaultDto);
        return ResultData.instance();
    }


    @GetMapping("/fault/list")
    @ApiOperation(value = "资产故障列表")
    public IPageResultData<List<ListAssetsFaultVo>> listFault(@ApiParam("科室")Long departmentId, @ApiParam("状态") AssetsFaultState state, @ApiParam("资产ID") Long assetsId,@ApiParam("故障编码")String code,@ApiParam("设备-资产编码")String assetsCode,
                                                              @ApiParam(value = "开始申报时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                              @ApiParam(value = "结束申报时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        List<Long> departmentIds = departmentExposeService.responsibleDepartment(departmentId);
        if (departmentIds.size()>0) {
            List<Assets> list = assetsService.findByDepartmentId(departmentIds);
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            list.forEach(assets -> {
                ids.add(assets.getId());
            });
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_assetsId",ids);
        }
        if (Objects.nonNull(assetsId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_assetsId",assetsId);
        }
        if (Objects.nonNull(state)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_state",state);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_code",code);
        }
        if (StringUtils.hasText(assetsCode)) {
            List<Assets> list = assetsService.find(assetsCode);
            List<Long> ids = new ArrayList<>();
            list.forEach(assets -> {
                ids.add(assets.getId());
            });
            if (ids.size()>0){
                jpqlParameter.setSearchParameter(SearchConstant.IN+"_assetsId",ids);
            }
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<AssetsFault> page = assetsFaultService.findNavigator(lionPage);
        List<AssetsFault> list = page.getContent();
        List<ListAssetsFaultVo> listAssetsFaultVos = new ArrayList<ListAssetsFaultVo>();
        list.forEach(assetsFault -> {
            ListAssetsFaultVo vo = new ListAssetsFaultVo();
            BeanUtils.copyProperties(assetsFault,vo);
            if (Objects.nonNull(assetsFault.getDeclarantUserId())) {
                User user = userExposeService.findById(assetsFault.getDeclarantUserId());
                if (Objects.nonNull(user)){
                    vo.setDeclarantUserName(user.getName());
                    vo.setDeclarantUserHeadPortrait(user.getHeadPortrait());
                    vo.setDeclarantUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                }
            }
            Assets assets = assetsService.findById(assetsFault.getAssetsId());
            if (Objects.nonNull(assets)){
                vo.setDeviceCode(assets.getCode());
                vo.setImg(assets.getImg());
                vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                vo.setName(assets.getName());
                Build build = buildService.findById(assets.getBuildId());
                if (Objects.nonNull(build)) {
                    vo.setBuildName(build.getName());
                }
                BuildFloor buildFloor = buildFloorService.findById(assets.getBuildFloorId());
                if (Objects.nonNull(buildFloor)){
                    vo.setBuildFloorName(buildFloor.getName());
                }
                Region region = regionService.findById(assets.getRegionId());
                if (Objects.nonNull(region)){
                    vo.setRegionName(region.getName());
                }
                Department department = departmentService.findById(assets.getDepartmentId());
                if (Objects.nonNull(department)){
                    vo.setDepartmentName(department.getName());
                }
            }
            listAssetsFaultVos.add(vo);
        });
        return new PageResultData(listAssetsFaultVos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/fault/details")
    @ApiOperation(value = "资产故障详情")
    public IResultData<DetailsAssetsFaultVo> detailsFault(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        AssetsFault assetsFault = this.assetsFaultService.findById(id);
        if (Objects.nonNull(assetsFault)) {
            DetailsAssetsFaultVo vo = new DetailsAssetsFaultVo();
            BeanUtils.copyProperties(assetsFault, vo);
            if (Objects.nonNull(assetsFault.getDeclarantUserId())) {
                User user = userExposeService.findById(assetsFault.getDeclarantUserId());
                if (Objects.nonNull(user)){
                    vo.setDeclarantUserName(user.getName());
                    vo.setDeclarantUserHeadPortrait(user.getHeadPortrait());
                    vo.setDeclarantUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                }
            }
            Assets assets = assetsService.findById(assetsFault.getAssetsId());
            if (Objects.nonNull(assets)){
                vo.setDeviceCode(assets.getCode());
                vo.setImg(assets.getImg());
                vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                vo.setName(assets.getName());
                Build build = buildService.findById(assets.getBuildId());
                if (Objects.nonNull(build)) {
                    vo.setBuildName(build.getName());
                }
                BuildFloor buildFloor = buildFloorService.findById(assets.getBuildFloorId());
                if (Objects.nonNull(buildFloor)){
                    vo.setBuildFloorName(buildFloor.getName());
                }
                Region region = regionService.findById(assets.getRegionId());
                if (Objects.nonNull(region)){
                    vo.setRegionName(region.getName());
                }
                Department department = departmentService.findById(assets.getDepartmentId());
                if (Objects.nonNull(department)){
                    vo.setDepartmentName(department.getName());
                }
            }
            resultData.setData(vo);
        }
        return resultData;
    }

    @PutMapping("/fault/update")
    @ApiOperation(value = "修改资产故障")
    public IResultData updateFault(@RequestBody UpdateAssetsFaultDto updateAssetsFaultDto){
        assetsFaultService.update(updateAssetsFaultDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除资产故障")
    @DeleteMapping("/fault/delete")
    public IResultData deleteFault(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(deleteDto -> {
            assetsFaultService.deleteById(deleteDto.getId());
        });
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/fault/report/add")
    @ApiOperation(value = "新增资产故障汇报")
    public IResultData addFaultReport(@RequestBody @Validated({Validator.Insert.class}) AddAssetsFaultReportDto addAssetsFaultReportDto) {
        this.assetsFaultReportService.save(addAssetsFaultReportDto);
        return ResultData.instance();
    }

    @PutMapping("/fault/report/update")
    @ApiOperation(value = "修改资产故障汇报")
    public IResultData updateFaultReport(@RequestBody @Validated({Validator.Update.class}) AddAssetsFaultReportDto addAssetsFaultReportDto) {
        this.assetsFaultReportService.update(addAssetsFaultReportDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除资产故障汇报")
    @DeleteMapping("/fault/report/delete")
    public IResultData deleteFaultReport(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(deleteDto -> {
            assetsFaultReportService.deleteById(deleteDto.getId());
        });
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/fault/report/list")
    @ApiOperation(value = "资产故障汇报列表")
    public IPageResultData<List<ListAssetsFaultReportVo>> listFault(@ApiParam("资产故障ID") Long assetsFaultId,LionPage lionPage){
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(assetsFaultId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_assetsFaultId",assetsFaultId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        return (IPageResultData<List<ListAssetsFaultReportVo>>) assetsFaultReportService.findNavigator(lionPage);
    }
}
