package com.lion.manage.controller.ward;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.HavingMonitor;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.entity.ward.dto.AddWardDto;
import com.lion.manage.entity.ward.dto.UpdateWardDto;
import com.lion.manage.entity.ward.vo.*;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.ward.WardExposeService;
import com.lion.manage.expose.ward.WardRoomExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.manage.service.ward.WardService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
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
import java.util.*;

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

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private ObjectMapper objectMapper;

    @DubboReference
    private DeviceExposeService deviceExposeService;

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
                com.lion.core.Optional<Department> optional =departmentService.findById(ward.departmentId);
                if (optional.isPresent()){
                    listWardVo.setDepartmentName(optional.get().getName());
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
    public IResultData<DetailsWardVo> details(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        com.lion.core.Optional<Ward> optional = this.wardService.findById(id);
        if (optional.isPresent()){
            Ward ward = optional.get();
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
    public IPageResultData<List<ListWardRoomVo>> roomList(@ApiParam(value = "科室")Long departmentId, @ApiParam(value = "病房")Long wardId,@ApiParam(value = "病房编码")String code,  LionPage lionPage) {
        return (IPageResultData<List<ListWardRoomVo>>) wardRoomService.list(departmentId, wardId,code , lionPage);
    }

    @GetMapping("/sickbed/list")
    @ApiOperation(value = "病床列表")
    public IPageResultData<List<ListWardRoomSickbedVo>> sickbedList(@ApiParam(value = "是否被使用")Boolean isUse,@ApiParam(value = "床位编码")String bedCode, @ApiParam("是否本科室") Boolean isMyDepartment, @ApiParam(value = "科室")Long departmentId, @ApiParam(value = "病房")Long wardId, @ApiParam(value = "病房房间")Long wardRoomId, LionPage lionPage) {
        if (Objects.equals(isMyDepartment,true)) {
            Department department = departmentUserExposeService.findDepartment(CurrentUserUtil.getCurrentUserId());
            if (Objects.nonNull(department)) {
                departmentId = department.getId();
            }
        }
        return (IPageResultData<List<ListWardRoomSickbedVo>>) wardRoomSickbedService.list(isUse, bedCode, departmentId, wardId, wardRoomId, lionPage);
    }

    @GetMapping("/region")
    @ApiOperation(value = "获取病床/病房所在的区域")
    public IResultData<DetailsRegionVo> region(@ApiParam(value = "病床id")Long wardRoomSickbedId, @ApiParam(value = "病房id")Long wardRoomId) {
        Region region = getRegion(wardRoomSickbedId,wardRoomId);
        if (Objects.nonNull(region)) {
            return ResultData.instance().setData(regionService.details(region.getId()));
        }
        return ResultData.instance();
    }

    @GetMapping("/having/monitor")
    @ApiOperation(value = "获取病床/病房所在的区域有没有定位设备(邮件通知复用之前的维修通知接口)")
    public IResultData<HavingMonitor> havingMonitor(@ApiParam(value = "病床id")Long wardRoomSickbedId, @ApiParam(value = "病房id")Long wardRoomId) throws JsonProcessingException {
        Region region = getRegion(wardRoomSickbedId,wardRoomId);
//        [{"code":"STAR_AP"},{"code":"MONITOR"},{"code":"VIRTUAL_WALL","count":"2"},{"code":"LF_EXCITER"},{"code":"HAND_WASHING"},{"code":"RECYCLING_BOX"}]
        if (Objects.isNull(region)){
            return ResultData.instance().setData(HavingMonitor.NOT_BINDING);
        }
        String json = region.getDeviceQuantityDefinition();
        if (StringUtils.hasText(json)) {
            List<LinkedHashMap> list = objectMapper.readValue(json,List.class);
            for (LinkedHashMap linkedHashMap :list ){
                if (linkedHashMap.containsKey("count") && Objects.nonNull(linkedHashMap.get("count"))) {
                    int count = Integer.valueOf(String.valueOf(linkedHashMap.get("count")));
                    if (count >0){
                        if (linkedHashMap.containsKey("code")) {
                            DeviceClassify classify = DeviceClassify.valueOf(String.valueOf(linkedHashMap.get("code")));
                            if (Objects.nonNull(classify)) {
                                int regionDeviceCount = deviceExposeService.count(classify,region.getId());
                                if (regionDeviceCount<count) {
                                    return ResultData.instance().setData(HavingMonitor.NOT_DEFINITION);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ResultData.instance();
    }

    @DubboReference
    private WardRoomSickbedExposeService wardRoomSickbedExposeService;

    @DubboReference
    private WardRoomExposeService wardRoomExposeService;

    @DubboReference
    private WardExposeService wardExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @GetMapping("/tag/sickbed/department")
    @ApiOperation(value = "判断标签和病床是否在同一科室-不在同一科室抛异常")
    public IResultData tagSickbedDepartment(@ApiParam(value = "病床id") Long wardRoomSickbedId,@ApiParam(value = "标签id")Long tagId) {
        com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedExposeService.findById(wardRoomSickbedId);
        if (optionalWardRoomSickbed.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000035"));
        }
        com.lion.core.Optional<WardRoom> optionalWardRoom = wardRoomExposeService.findById(optionalWardRoomSickbed.get().getWardRoomId());
        if (optionalWardRoom.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000036"));
        }
        com.lion.core.Optional<Ward> optionalWard = wardExposeService.findById(optionalWardRoom.get().getWardId());
        if (optionalWard.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000037"));
        }
        com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(tagId);
        if (optionalTag.isPresent()) {
            Tag tag = optionalTag.get();
            if (!Objects.equals(optionalWard.get().getDepartmentId(), tag.getDepartmentId())) {
                com.lion.core.Optional<Department> optionalTagDepartment = departmentExposeService.findById(tag.getDepartmentId());
                com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(optionalWard.get().getDepartmentId());
                if (optionalTagDepartment.isPresent() && optionalDepartment.isPresent()) {
                    BusinessException.throwException(MessageI18nUtil.getMessage("4000026", new Object[]{optionalDepartment.get().getName(), optionalTagDepartment.get().getName()}));
                }
            }
        }
        return ResultData.instance();
    }

    private Region getRegion(Long wardRoomSickbedId,Long wardRoomId){
        Region region =null;
        if (Objects.nonNull(wardRoomSickbedId)) {
            com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedService.findById(wardRoomSickbedId);
            if (optionalWardRoomSickbed.isPresent()) {
                com.lion.core.Optional<Region> optionalRegion = regionService.findById(optionalWardRoomSickbed.get().getRegionId());
                if (optionalRegion.isPresent()) {
                    region = optionalRegion.get();
                }
                return region;
            }
        }
        if ( Objects.nonNull(wardRoomId)){
            com.lion.core.Optional<WardRoom> optionalWardRoom = wardRoomService.findById(wardRoomId);
            if (optionalWardRoom.isPresent()) {
                com.lion.core.Optional<Region> optionalRegion = regionService.findById(optionalWardRoom.get().getRegionId());
                if (optionalRegion.isPresent()) {
                    region = optionalRegion.get();
                }
                return region;
            }
        }
        return region;
    }

//    public static void main(String agrs[]) throws JsonProcessingException {
//        String json = "[{\"code\":\"STAR_AP\"},{\"code\":\"MONITOR\"},{\"code\":\"VIRTUAL_WALL\",\"count\":\"2\"},{\"code\":\"LF_EXCITER\"},{\"code\":\"HAND_WASHING\"},{\"code\":\"RECYCLING_BOX\"}]";
//        List export = new ObjectMapper().readValue(json,List.class);
//        export.forEach(o->{
//            LinkedHashMap linkedHashMap = (LinkedHashMap) o;
//            System.out.println(linkedHashMap);
//        });
//    }

}
