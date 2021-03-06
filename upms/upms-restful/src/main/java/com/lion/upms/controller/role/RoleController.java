package com.lion.upms.controller.role;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.exception.BusinessException;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.role.dto.AddRoleDto;
import com.lion.upms.entity.role.dto.UpdateRoleDto;
import com.lion.upms.entity.role.vo.DetailsRoleVo;
import com.lion.upms.entity.role.vo.EditDetailsRoleVo;
import com.lion.upms.entity.role.vo.PageRoleVo;
import com.lion.upms.service.role.RoleService;
import com.lion.upms.service.role.RoleUserService;
import com.lion.upms.service.user.UserService;
import com.lion.utils.MessageI18nUtil;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;

/**
 * @author Mr.Liu
 * @Description: 角色控制层
 * @date 2021/3/22下午9:24
 */
@RestController
@RequestMapping("/role")
@Validated
@Api(tags = {"角色管理"})
public class RoleController extends BaseControllerImpl implements BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleUserService roleUserService;

    @PostMapping("/add")
    @ApiOperation(value = "新增角色")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddRoleDto addRoleDto){
        Role role = new Role();
        BeanUtil.copyProperties(addRoleDto,role, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        roleService.assertNameExist(role.getName(),null);
//        roleService.assertCodeExist(role.getCode(),null);
        roleService.save(role);
        return ResultData.instance();
    }

    @GetMapping("/list")
    @ApiOperation(value = "角色列表")
    public IPageResultData<List<PageRoleVo>> list(@ApiParam(name = "角色名称") String name, LionPage lionPage){
        return (IPageResultData) roleService.list(name,lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "角色详情")
    public IResultData<DetailsRoleVo> details(@NotNull(message = "{0000000}") Long id){
        com.lion.core.Optional<Role> optional = roleService.findById(id);
        ResultData resultData = ResultData.instance();
        if (optional.isPresent()) {
            Role role = optional.get();
            DetailsRoleVo detailsRoleVo = new DetailsRoleVo();
            BeanUtils.copyProperties(role,detailsRoleVo);
            detailsRoleVo.setUsers(userService.detailsRoleUser(role.getId()));
            resultData.setData(detailsRoleVo);
        }
        return resultData;
    }

    @GetMapping("/editDetails")
    @ApiOperation(value = "编辑角色基础信息获取详情")
    public IResultData<EditDetailsRoleVo> editDetails(@NotNull(message = "{0000000}") Long id){
        com.lion.core.Optional<Role> optional = roleService.findById(id);
        ResultData resultData = ResultData.instance();
        if (optional.isPresent()) {
            EditDetailsRoleVo editDetailsRoleVo = new EditDetailsRoleVo();
            BeanUtils.copyProperties(optional.get(), editDetailsRoleVo);
            resultData.setData(editDetailsRoleVo);
        }
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改角色")
    public IResultData update(@RequestBody @Validated UpdateRoleDto updateRoleDto){
        Role role = new Role();
        BeanUtils.copyProperties(updateRoleDto,role);
        roleService.assertNameExist(role.getName(),role.getId());
//        roleService.assertCodeExist(role.getCode(),role.getId());
        com.lion.core.Optional<Role> optional = roleService.findById(role.getId());
        if (optional.isPresent()) {
            if (Objects.equals(optional.get().getIsDefault(), true)) {
                if (StringUtils.hasText(role.getName()) && !Objects.equals(optional.get().getName(), role.getName())) {
                    BusinessException.throwException(MessageI18nUtil.getMessage("0000025"));
                }
            }
        }
        roleService.update(role);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        roleService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }


}
