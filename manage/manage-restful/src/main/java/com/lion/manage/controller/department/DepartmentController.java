package com.lion.manage.controller.department;

import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.lion.common.constants.RedisConstants;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.persistence.Validator;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentAlarm;
import com.lion.manage.entity.department.dto.AddDepartmentAlarmDto;
import com.lion.manage.entity.department.dto.AddDepartmentDto;
import com.lion.manage.entity.department.dto.UpdateDepartmentAlarmDto;
import com.lion.manage.entity.department.dto.UpdateDepartmentDto;
import com.lion.manage.entity.department.vo.DetailsDepartmentVo;
import com.lion.manage.entity.department.vo.ListDepartmentVo;
import com.lion.manage.entity.department.vo.TreeDepartmentVo;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.service.department.DepartmentAlarmService;
import com.lion.manage.service.department.DepartmentResponsibleUserService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.department.DepartmentUserService;
import com.lion.manage.service.region.RegionService;
import com.lion.upms.entity.user.User;
import com.lion.utils.CurrentUserUtil;
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
import com.lion.core.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午2:33
 */
@RestController
@RequestMapping("/department")
@Validated
@Api(tags = {"科室管理"})
public class DepartmentController extends BaseControllerImpl implements BaseController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentResponsibleUserService departmentResponsibleUserService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private DepartmentAlarmService departmentAlarmService;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DepartmentExposeService departmentExposeService;

    @PostMapping("/add")
    @ApiOperation(value = "新增科室")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddDepartmentDto addDepartmentDto){
        departmentService.add(addDepartmentDto);
        return ResultData.instance();
    }

    @GetMapping("/treeList")
    @ApiOperation(value = "科室树形列表")
    public IResultData<List<TreeDepartmentVo>> treeList(@ApiParam(value = "科室名称") String name){
        ResultData resultData = ResultData.instance();
        resultData.setData(departmentService.treeList(name));
        return resultData;
    }

    @GetMapping("/list")
    @ApiOperation(value = "科室列表")
    public IPageResultData<List<ListDepartmentVo>> list(@ApiParam(value = "科室名称") String name, LionPage lionPage){
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        List<Long> departmentIds = new ArrayList<>();
        departmentIds = departmentExposeService.responsibleDepartment(null);
        if (departmentIds.size()>0) {
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",departmentIds);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData page = (PageResultData) departmentService.findNavigator(lionPage);
        List<Department> list = page.getContent();
        List<ListDepartmentVo> listDepartmentVo = new ArrayList<ListDepartmentVo>();
        list.forEach(department -> {
            ListDepartmentVo departmentVo = new ListDepartmentVo();
            BeanUtils.copyProperties(department,departmentVo);
            departmentVo.setResponsibleUser(departmentResponsibleUserService.responsibleUser(department.getId()));
            listDepartmentVo.add(departmentVo);
        });
        return new PageResultData(listDepartmentVo, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/details")
    @ApiOperation(value = "科室详情")
    public IResultData<DetailsDepartmentVo> details(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(this.departmentService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改科室")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateDepartmentDto updateDepartmentDto){
        departmentService.update(updateDepartmentDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除科室")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(d->{
            List<Region> list = regionService.find(d.getId());
            if (list.size()>0){
                com.lion.core.Optional<Department> optional = this.departmentService.findById(d.getId());
                if (optional.isPresent()) {
                    BusinessException.throwException(optional.get().getName() + MessageI18nUtil.getMessage("2000060"));
                }
            }
        });
        departmentService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/alarm/add")
    @ApiOperation(value = "新增科室警告设置")
    public IResultData addAlarm(@RequestBody AddDepartmentAlarmDto addDepartmentAlarmDto){
        DepartmentAlarm departmentAlarm = new DepartmentAlarm();
        BeanUtils.copyProperties(addDepartmentAlarmDto,departmentAlarm);
        Long userId = CurrentUserUtil.getCurrentUserId();
        Department department = departmentUserService.findDepartment(userId);
        if (Objects.nonNull(department)) {
            departmentAlarm.setDepartmentId(department.getId());
        }
        departmentAlarm = departmentAlarmService.save(departmentAlarm);
        persistenceRedis(departmentAlarm);
        return ResultData.instance();
    }

    @PutMapping("/alarm/update")
    @ApiOperation(value = "修改科室警告设置")
    public IResultData addAlarm(@RequestBody UpdateDepartmentAlarmDto updateDepartmentAlarmDto){
        DepartmentAlarm departmentAlarm = new DepartmentAlarm();
        BeanUtils.copyProperties(updateDepartmentAlarmDto,departmentAlarm);
        departmentAlarmService.update(departmentAlarm);
        persistenceRedis(departmentAlarm);
        return ResultData.instance();
    }

//    @ApiOperation(value = "删除科室警告设置")
//    @DeleteMapping("/alarm/delete")
//    public IResultData deleteAlarm(@RequestBody List<DeleteDto> deleteDtoList){
//        deleteDtoList.forEach(d->{
//            departmentAlarmService.deleteById(d.getId());
//        });
//        ResultData resultData = ResultData.instance();
//        return resultData;
//    }

    @GetMapping("/alarm/details")
    @ApiOperation(value = "科室警告详情")
    public IResultData<DepartmentAlarm> detailsAlarm(){
        ResultData resultData = ResultData.instance();
        Long userId = CurrentUserUtil.getCurrentUserId();
        Department department = departmentUserService.findDepartment(userId);
        if (Objects.nonNull(department)) {
            DepartmentAlarm departmentAlarm = departmentAlarmService.find(department.getId());
            if (Objects.isNull(departmentAlarm)) {
                departmentAlarm = new DepartmentAlarm();
                departmentAlarm.setDepartmentId(department.getId());
                departmentAlarm = departmentAlarmService.save(departmentAlarm);
                persistenceRedis(departmentAlarm);
            }
            resultData.setData(departmentAlarm);
        }
        return resultData;
    }

    @GetMapping("/owner/department")
    @ApiOperation(value = "获取有权限的科室")
    public IResultData<List<Department>> ownerDepartment(){
        return ResultData.instance().setData(departmentService.ownerDepartment());
    }

    private void persistenceRedis(DepartmentAlarm departmentAlarm){
        redisTemplate.opsForValue().set(RedisConstants.DEPARTMENT_ALARM+departmentAlarm.getDepartmentId(),departmentAlarm,5, TimeUnit.MINUTES);
    }
}
