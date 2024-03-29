package com.lion.manage.controller.build;

import com.lion.common.constants.RedisConstants;
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
import com.lion.manage.service.region.RegionService;
import com.lion.utils.MessageI18nUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import java.util.concurrent.TimeUnit;

import com.lion.core.Optional;

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

    @Autowired
    private RegionService regionService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ApiOperation(value = "新增建筑")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddBuildDto addBuildDto){
        Build build = new Build();
        BeanUtils.copyProperties(addBuildDto,build);
        build = buildService.save(build);
        redisTemplate.opsForValue().set(RedisConstants.BUILD+build.getId(),build,5, TimeUnit.MINUTES);
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
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
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
    public IResultData<DetailsBuildVo> details(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        com.lion.core.Optional<Build> optional = this.buildService.findById(id);
        if (optional.isPresent()){
            Build build = optional.get();
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
        redisTemplate.opsForValue().set(RedisConstants.BUILD+build.getId(),build,5, TimeUnit.MINUTES);
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
        assertBuildExist(buildFloor.getBuildId());
        buildFloor = buildFloorService.save(buildFloor);
        redisTemplate.opsForValue().set(RedisConstants.BUILD_FLOOR+buildFloor.getId(),buildFloor,5, TimeUnit.MINUTES);
        return ResultData.instance();
    }


    @GetMapping("/floor/list")
    @ApiOperation(value = "建筑楼层列表")
    public IPageResultData<List<ListBuildFloorVo>> floorList(@ApiParam(value = "楼层名称") String name,@ApiParam(value = "建筑ID") Long buildId, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(buildId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_buildId",buildId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) buildFloorService.findNavigator(lionPage);
        List<BuildFloor> list = page.getContent();
        List<ListBuildFloorVo> listBuildFloorVos = new ArrayList<ListBuildFloorVo>();
        list.forEach(buildFloor -> {
            ListBuildFloorVo buildFloorVo = new ListBuildFloorVo();
            BeanUtils.copyProperties(buildFloor,buildFloorVo);
            com.lion.core.Optional<Build> optional = buildService.findById(buildFloor.getBuildId());
            if (optional.isPresent()) {
                buildFloorVo.setBuild(optional.get());
            }
            buildFloorVo.setRegions(regionService.findByBuildFloorId(buildFloor.getId()));
            listBuildFloorVos.add(buildFloorVo);
        });
        return new PageResultData(listBuildFloorVos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/floor/details")
    @ApiOperation(value = "建筑楼层详情")
    public IResultData<DetailsBuildVo> floorDetails(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        com.lion.core.Optional<BuildFloor> optional = this.buildFloorService.findById(id);
        resultData.setData(optional.isPresent()?optional.get():null);
        return resultData;
    }

    @PutMapping("/floor/update")
    @ApiOperation(value = "修改建筑楼层")
    public IResultData floorUpdate(@RequestBody @Validated({Validator.Update.class}) UpdateBuildFloorDto updateBuildFloorDto){
        BuildFloor buildFloor =new BuildFloor();
        BeanUtils.copyProperties(updateBuildFloorDto,buildFloor);
        assertBuildExist(buildFloor.getBuildId());
        buildFloorService.update(buildFloor);
        redisTemplate.opsForValue().set(RedisConstants.BUILD_FLOOR+buildFloor.getId(),buildFloor,5, TimeUnit.MINUTES);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除建筑楼层")
    @DeleteMapping("/floor/delete")
    public IResultData floorDelete(@RequestBody List<DeleteDto> deleteDtoList){
        buildFloorService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    private void assertBuildExist(Long buildId){
        com.lion.core.Optional<Build> optional = this.buildService.findById(buildId);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000059"));
        }
    }
}
