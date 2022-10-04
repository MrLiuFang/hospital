package com.lion.manage.controller.assets;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.entity.assets.dto.*;
import com.lion.manage.entity.assets.vo.*;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.service.assets.*;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.upms.expose.role.RoleExposeService;
import com.lion.upms.expose.user.UserExposeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
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

    @Autowired
    private AssetsTypeService assetsTypeService;

    @PostMapping("/add")
    @ApiOperation(value = "新增资产")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class})AddAssetsDto addAssetsDto){
        assetsService.add(addAssetsDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "资产列表")
    public IPageResultData<List<ListAssetsVo>> list(@ApiParam(value = "是否借用")Boolean isBorrowed,@ApiParam(value = "资产名称") String name, @ApiParam(value = "资产编号") String code,@ApiParam(value = "科室id")Long departmentId,Boolean isMyDepartment,
                                                  @ApiParam(value = "资产分类") Long assetsTypeId, @ApiParam(value = "使用状态") AssetsUseState useState, @ApiParam(value = "tagCode") String tagCode,@RequestParam(required = false) List<Long> ids, LionPage lionPage){
        return assetsService.list(isBorrowed, name, code, departmentId, isMyDepartment, assetsTypeId, useState,tagCode,ids , lionPage);
    }

    @GetMapping("/export")
    @ApiOperation(value = "导出")
    public void export(@ApiParam(value = "资产名称") String name, @ApiParam(value = "资产编号") String code,@ApiParam(value = "科室id")Long departmentId,Boolean isMyDepartment,
                       @ApiParam(value = "资产分类") Long assetsTypeId, @ApiParam(value = "使用状态") AssetsUseState useState,@RequestParam(required = false) List<Long> ids, LionPage lionPage) throws IOException, IllegalAccessException {

        assetsService.export(name, code, departmentId, isMyDepartment, assetsTypeId, useState, ids, lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "资产详情")
    public IResultData<DetailsAssetsVo> details(@NotNull(message = "{0000000}") Long id){
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
    public IPageResultData<List<ListAssetsBorrowVo>> listBorrw(@ApiParam(value = "资产名称")String name,@ApiParam(value = "登记人/借用人")Long borrowUserId,@ApiParam(value = "资产类型")Long assetsTypeId,@ApiParam(value = "科室id") Long departmentId, @ApiParam(value = "资产id") Long assetsId, @ApiParam(value = "借用开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "借用结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                               @ApiParam(value = "是否已归还(null值 查所有)") Boolean isReturn, LionPage lionPage){
        return assetsBorrowService.list(name, borrowUserId, assetsTypeId, departmentId, assetsId, startDateTime, endDateTime, isReturn, lionPage);
    }

    @GetMapping("/borrw/list/export")
    @ApiOperation(value = "资产借用列表导出")
    public void listBorrwExport(@ApiParam(value = "资产名称")String name,@ApiParam(value = "登记人/借用人")Long borrowUserId,@ApiParam(value = "资产类型")Long assetsTypeId,@ApiParam(value = "科室id") Long departmentId, @ApiParam(value = "资产id") Long assetsId, @ApiParam(value = "借用开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "借用结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                               @ApiParam(value = "是否已归还(null值 查所有)") Boolean isReturn,LionPage lionPage) throws IOException, IllegalAccessException {
        assetsBorrowService.export( name, borrowUserId, assetsTypeId, departmentId, assetsId, startDateTime, endDateTime, isReturn,lionPage );
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
                                                              @ApiParam("关键字") String keyword,
                                                              @ApiParam(value = "开始申报时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                              @ApiParam(value = "结束申报时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage){

        return assetsFaultService.list(departmentId, state, assetsId, code, assetsCode, keyword, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/fault/list/export")
    @ApiOperation(value = "资产故障列表导出")
    public void listFaultExport(@ApiParam("科室")Long departmentId, @ApiParam("状态") AssetsFaultState state, @ApiParam("资产ID") Long assetsId,@ApiParam("故障编码")String code,@ApiParam("设备-资产编码")String assetsCode,
                                                              @ApiParam("关键字") String keyword,LionPage lionPage,
                                                              @ApiParam(value = "开始申报时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                              @ApiParam(value = "结束申报时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) throws IOException, IllegalAccessException {

        assetsFaultService.export(departmentId, state, assetsId, code, assetsCode, keyword, startDateTime, endDateTime,lionPage );
    }

    @GetMapping("/fault/details")
    @ApiOperation(value = "资产故障详情")
    public IResultData<DetailsAssetsFaultVo> detailsFault(@NotNull(message = "{0000000}") Long id){
        return ResultData.instance().setData(assetsFaultService.details(id));
    }

    @PutMapping("/fault/update")
    @ApiOperation(value = "修改资产故障")
    public IResultData updateFault(@RequestBody @Validated({Validator.Update.class}) UpdateAssetsFaultDto updateAssetsFaultDto){
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

    @PostMapping("/type/add")
    @ApiOperation(value = "添加资产类型")
    public IResultData addUserType(@RequestBody @Validated({Validator.Insert.class}) AddAssetsTypeDto addAssetsTypeDto){
        assetsTypeService.add(addAssetsTypeDto);
        return ResultData.instance();
    }

    @PutMapping("/type/update")
    @ApiOperation(value = "修改资产类型")
    public IResultData updateUserType(@RequestBody @Validated({Validator.Update.class}) UpdateAssetsTypeDto updateAssetsTypeDto){
        assetsTypeService.update(updateAssetsTypeDto);
        return ResultData.instance();
    }

    @DeleteMapping("/type/delete")
    @ApiOperation(value = "删除资产类型")
    public IResultData deleteUserType(@RequestBody List<DeleteDto> deleteDtoList){
        assetsTypeService.delete(deleteDtoList);
        return ResultData.instance();
    }

    @GetMapping("/type/list")
    @ApiOperation(value = "资产类型列表")
    public IPageResultData<List<ListAssetsTypeVo>> listUserType(@ApiParam(value = "类型名称") String assetsTypeName, LionPage LionPage){
        return assetsTypeService.list(assetsTypeName, LionPage);
    }

    @GetMapping("/type/details")
    @ApiOperation(value = "资产类型详情")
    public IResultData<DetailsAssetsTypeVo> detailsUserType(@ApiParam(value = "类型id") @NotNull(message = "{0000000}") Long id){
        com.lion.core.Optional<AssetsType> optional = assetsTypeService.findById(id);
        if (optional.isPresent()) {
            DetailsAssetsTypeVo vo = new DetailsAssetsTypeVo();
            BeanUtils.copyProperties(optional.get(), vo);
            return ResultData.instance().setData(vo);
        }
        return ResultData.instance();
    }

}
