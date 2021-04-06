package com.lion.manage.controller.assets;

import com.alibaba.druid.sql.visitor.functions.If;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.assets.dto.*;
import com.lion.manage.entity.assets.vo.*;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AssetsType;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.manage.entity.ward.vo.DetailsWardVo;
import com.lion.manage.entity.ward.vo.ListWardVo;
import com.lion.manage.service.assets.AssetsBorrowService;
import com.lion.manage.service.assets.AssetsFaultService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @PostMapping("/add")
    @ApiOperation(value = "新增资产")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class})AddAssetsDto addAssetsDto){
        assetsService.add(addAssetsDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "资产列表")
    public IPageResultData<List<ListAssetsVo>> list(@ApiParam(value = "资产名称") String name, @ApiParam(value = "资产编号") String code, @ApiParam(value = "标签编号") String tagCode,
                                                  @ApiParam(value = "资产分类") AssetsType type, @ApiParam(value = "使用状态") AssetsUseState useState, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        if (StringUtils.hasText(tagCode)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_tagCode",tagCode);
        }
        if (Objects.nonNull(type)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_type",type);
        }
        if (Objects.nonNull(useState)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_useState",useState);
        }
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
    public IResultData addBorrw(@RequestBody @Validated({Validator.Insert.class}) AddAssetsBorrowDto addAssetsBorrowDto){
        assetsBorrowService.add(addAssetsBorrowDto);
        return ResultData.instance();
    }


    @GetMapping("/borrw/list")
    @ApiOperation(value = "资产借用列表")
    public IPageResultData<List<ListAssetsBorrowVo>> listBorrw(@ApiParam(value = "资产id") Long assetsId,  LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(assetsId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_assetsId",assetsId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<AssetsBorrow> page = assetsBorrowService.findNavigator(lionPage);
        List<AssetsBorrow> list = page.getContent();
        List<ListAssetsBorrowVo> listAssetsBorrowVos = new ArrayList<ListAssetsBorrowVo>();
        list.forEach(assetsBorrow -> {
            ListAssetsBorrowVo vo = new ListAssetsBorrowVo();
            BeanUtils.copyProperties(assetsBorrow,vo);
            if (Objects.nonNull(assetsBorrow.getBorrowUserId())){
                User user = userExposeService.findById(assetsBorrow.getBorrowUserId());
                if (Objects.nonNull(user)){
                    vo.setBorrowUserName(user.getName());
                }
            }
            if (Objects.nonNull(assetsBorrow.getReturnUserId())){
                User user = userExposeService.findById(assetsBorrow.getReturnUserId());
                if (Objects.nonNull(user)){
                    vo.setReturnUserName(user.getName());
                }
            }
            listAssetsBorrowVos.add(vo);
        });
        return new PageResultData(listAssetsBorrowVos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/borrw/details")
    @ApiOperation(value = "资产借用详情")
    public IResultData<DetailsAssetsBorrowVo> detailsBorrw(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        AssetsBorrow assetsBorrow = this.assetsBorrowService.findById(id);
        if (Objects.nonNull(assetsBorrow)){
            DetailsAssetsBorrowVo vo = new DetailsAssetsBorrowVo();
            BeanUtils.copyProperties(assetsBorrow,vo);
            if (Objects.nonNull(assetsBorrow.getBorrowUserId())){
                User user = userExposeService.findById(assetsBorrow.getBorrowUserId());
                if (Objects.nonNull(user)){
                    vo.setBorrowUserName(user.getName());
                }
            }
            if (Objects.nonNull(assetsBorrow.getReturnUserId())){
                User user = userExposeService.findById(assetsBorrow.getReturnUserId());
                if (Objects.nonNull(user)){
                    vo.setReturnUserName(user.getName());
                }
            }
            resultData.setData(vo);
        }
        return resultData;
    }

    @PutMapping("/borrw/update")
    @ApiOperation(value = "修改资产借用")
    public IResultData updateBorrw(@RequestBody @Validated({Validator.Update.class}) UpdateAssetsBorrowDto updateAssetsBorrowDto){
        assetsBorrowService.update(updateAssetsBorrowDto);
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
    public IPageResultData<List<ListAssetsFaultVo>> listFault(@ApiParam("资产ID") Long assetsId, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(assetsId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_assetsId",assetsId);
        }
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
                }
            }
            resultData.setData(vo);
        }
        return resultData;
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
}
