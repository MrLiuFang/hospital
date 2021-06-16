package com.lion.manage.controller.ward;

import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.entity.ward.dto.AddWardDto;
import com.lion.manage.entity.ward.dto.UpdateWardDto;
import com.lion.manage.entity.ward.vo.DetailsWardRoomVo;
import com.lion.manage.entity.ward.vo.DetailsWardVo;
import com.lion.manage.entity.ward.vo.ListWardVo;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.manage.service.ward.WardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:21
 */
@RestController
@RequestMapping("/ward")
@Validated
@Api(tags = {"病房管理"})
public class WardController extends BaseControllerImpl implements BaseController {

    @Autowired
    private WardService wardService;

    @Autowired
    private WardRoomService wardRoomService;

    @Autowired
    private WardRoomSickbedService wardRoomSickbedService;

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/add")
    @ApiOperation(value = "新增病房")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddWardDto addWardDto){
        wardService.add(addWardDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "病房列表")
    public IPageResultData<List<ListWardVo>> list(@ApiParam(value = "病房名称") String name, @ApiParam(value = "科室id")Long departmentId,LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(departmentId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) wardService.findNavigator(lionPage);
        List<Ward> list = page.getContent();
        List<ListWardVo> listWardVos = new ArrayList<>();
        list.forEach(ward -> {
            ListWardVo listWardVo = new ListWardVo();
            BeanUtils.copyProperties(ward,listWardVo);
            if (Objects.nonNull(ward.departmentId)){
                Department department =departmentService.findById(ward.departmentId);
                if (Objects.nonNull(department)){
                    listWardVo.setDepartmentName(department.getName());
                }
            }
            List<WardRoom> wardRoomList = wardRoomService.find(ward.getId());
            listWardVo.setRoomQuantity(wardRoomList.size());
            wardRoomList.forEach(wardRoom -> {
                listWardVo.setSickbedQuantity(listWardVo.getSickbedQuantity() + wardRoomSickbedService.find(wardRoom.getId()).size());
            });
            listWardVos.add(listWardVo);
        });
        return new PageResultData(listWardVos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/details")
    @ApiOperation(value = "病房详情")
    public IResultData<DetailsWardVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        Ward ward = this.wardService.findById(id);
        if (Objects.nonNull(ward)){
            DetailsWardVo detailsWardVo = new DetailsWardVo();
            BeanUtils.copyProperties(ward,detailsWardVo);
            List<WardRoom> list = wardRoomService.find(ward.getId());
            List<DetailsWardRoomVo> detailsWardRoomVos = new ArrayList<DetailsWardRoomVo>();
            list.forEach(wardRoom -> {
                DetailsWardRoomVo detailsWardRoomVo = new DetailsWardRoomVo();
                BeanUtils.copyProperties(wardRoom,detailsWardRoomVo);
                detailsWardRoomVo.setWardRoomSickbed(wardRoomSickbedService.find(wardRoom.getId()));
                detailsWardRoomVos.add(detailsWardRoomVo);
            });
            detailsWardVo.setWardRoom(detailsWardRoomVos);
            resultData.setData(detailsWardVo);
        }
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改病房")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateWardDto updateWardDto){
        wardService.update(updateWardDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除病房")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        wardService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/room/list")
    @ApiOperation(value = "病房房间列表")
    public IPageResultData<List<WardRoom>> roomList(@ApiParam(value = "科室")Long departmentId,@ApiParam(value = "病房")Long wardId,LionPage lionPage) {
        return (IPageResultData<List<WardRoom>>) wardRoomService.list(departmentId, wardId,  lionPage);
    }

    @GetMapping("/sickbed/list")
    @ApiOperation(value = "病床列表")
    public IPageResultData<List<WardRoomSickbed>> sickbedList(@ApiParam(value = "科室")Long departmentId,@ApiParam(value = "病房")Long wardId,@ApiParam(value = "病房房间")Long wardRoomId,LionPage lionPage) {
        return (IPageResultData<List<WardRoomSickbed>>) wardRoomSickbedService.list(departmentId, wardId, wardRoomId, lionPage);
    }

}
