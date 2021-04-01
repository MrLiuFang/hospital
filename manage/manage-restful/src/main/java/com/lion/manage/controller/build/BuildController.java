package com.lion.manage.controller.build;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.build.dto.AddBuildDto;
import com.lion.manage.entity.build.dto.AddBuildFloorDto;
import com.lion.manage.entity.build.dto.UpdateBuildFloorDto;
import com.lion.manage.entity.build.vo.DetailsBuildVo;
import com.lion.manage.entity.build.dto.UpdateBuildDto;
import com.lion.manage.entity.build.vo.ListBuildFloorVo;
import com.lion.manage.entity.build.vo.ListBuildVo;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.vo.ListDepartmentVo;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * @date 2021/4/1上午11:19
 */
@RestController
@RequestMapping("/build")
@Validated
@Api(tags = {"建筑管理"})
public class BuildController extends BaseControllerImpl implements BaseController {

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @PostMapping("/add")
    @ApiOperation(value = "新增建筑")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddBuildDto addBuildDto){
        Build build = new Build();
        BeanUtils.copyProperties(addBuildDto,build);
        buildService.save(build);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "建筑列表")
    public IPageResultData<List<ListBuildVo>> list(@ApiParam(value = "建筑名称") String name, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) buildService.findNavigator(lionPage);
        List<Build> list = page.getContent();
        List<ListBuildVo> listBuildVos = new ArrayList<ListBuildVo>();
        list.forEach(build -> {
            ListBuildVo buildVo = new ListBuildVo();
            BeanUtils.copyProperties(build,buildVo);
            buildVo.setTotalFloors(buildFloorService.find(build.getId()).size());
            listBuildVos.add(buildVo);
        });
        return new PageResultData(listBuildVos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/details")
    @ApiOperation(value = "建筑详情")
    public IResultData<DetailsBuildVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        Build build = this.buildService.findById(id);
        if (Objects.nonNull(build)){
            DetailsBuildVo detailsBuildVo = new DetailsBuildVo();
            BeanUtils.copyProperties(build,detailsBuildVo);
            detailsBuildVo.setBuildFloors(this.buildFloorService.find(build.getId()));
            resultData.setData(detailsBuildVo);
        }
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改建筑")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateBuildDto updateBuildDto){
        Build build =new Build();
        BeanUtils.copyProperties(updateBuildDto,build);
        buildService.update(build);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除建筑")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        buildService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }


    @PostMapping("/floor/add")
    @ApiOperation(value = "新增建筑楼层")
    public IResultData floorAdd(@RequestBody @Validated({Validator.Insert.class}) AddBuildFloorDto addBuilidfFloorDto){
        BuildFloor buildFloor = new BuildFloor();
        BeanUtils.copyProperties(addBuilidfFloorDto,buildFloor);
        Build build = this.buildService.findById(buildFloor.getBuildId());
        if (Objects.isNull(build)){
            BusinessException.throwException("关联的建筑不存在");
        }
        buildFloorService.save(buildFloor);
        return ResultData.instance();
    }


    @GetMapping("/floor/list")
    @ApiOperation(value = "建筑楼层列表")
    public IPageResultData<List<ListBuildVo>> floorList(@ApiParam(value = "楼层名称") String name,@ApiParam(value = "建筑ID") Long buildId, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(buildId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_buildId",buildId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) buildFloorService.findNavigator(lionPage);
        List<BuildFloor> list = page.getContent();
        List<ListBuildFloorVo> listBuildFloorVos = new ArrayList<ListBuildFloorVo>();
        list.forEach(buildFloor -> {
            ListBuildFloorVo buildFloorVo = new ListBuildFloorVo();
            BeanUtils.copyProperties(buildFloor,buildFloorVo);
            Build build = buildService.findById(buildFloor.getBuildId());
            if (Objects.nonNull(build)) {
                buildFloorVo.setBuildName(build.getName());
            }
            listBuildFloorVos.add(buildFloorVo);
        });
        return new PageResultData(listBuildFloorVos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/floor/details")
    @ApiOperation(value = "建筑楼层详情")
    public IResultData<DetailsBuildVo> floorDetails(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        BuildFloor buildFloor = this.buildFloorService.findById(id);
        resultData.setData(buildFloor);
        return resultData;
    }

    @PutMapping("/floor/update")
    @ApiOperation(value = "修改建筑楼层")
    public IResultData floorUpdate(@RequestBody @Validated({Validator.Update.class}) UpdateBuildFloorDto updateBuildFloorDto){
        BuildFloor buildFloor =new BuildFloor();
        BeanUtils.copyProperties(updateBuildFloorDto,buildFloor);
        Build build = this.buildService.findById(buildFloor.getBuildId());
        if (Objects.isNull(build)){
            BusinessException.throwException("关联的建筑不存在");
        }
        buildFloorService.update(buildFloor);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除建筑楼层")
    @DeleteMapping("/floor/delete")
    public IResultData floorDelete(@RequestBody List<DeleteDto> deleteDtoList){
        buildFloorService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }
}
