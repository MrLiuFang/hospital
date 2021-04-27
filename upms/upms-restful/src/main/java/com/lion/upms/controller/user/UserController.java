package com.lion.upms.controller.user;

import cn.hutool.crypto.SecureUtil;
import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.dto.*;
import com.lion.upms.entity.user.vo.CurrentUserDetailsVo;
import com.lion.upms.entity.user.vo.DetailsUserVo;
import com.lion.upms.entity.user.vo.ListUserVo;
import com.lion.upms.service.role.RoleService;
import com.lion.upms.service.role.RoleUserService;
import com.lion.upms.service.user.UserService;
import com.lion.utils.CurrentUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 用户控制层
 * @date 2021/3/22下午9:22
 */
@RestController
@RequestMapping("/user")
@Validated
@Api(tags = {"用户管理"})
public class UserController extends BaseControllerImpl implements BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private RoleService roleService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/add")
    @ApiOperation(value = "新增用户")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddUserDto addUserDto){
        userService.add(addUserDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/list")
    @ApiOperation(value = "用户列表")
    public IPageResultData<List<ListUserVo>> list(@ApiParam(value = "科室") Long departmentId,@ApiParam(value = "用户") UserType userType,@ApiParam(value = "员工编号") Integer number,@ApiParam(value = "姓名")  String name,@ApiParam(value = "角色") Long roleId, LionPage lionPage){
        return userService.list(departmentId, userType, number, name, roleId, lionPage);
    }


    @GetMapping("/details")
    @ApiOperation(value = "用户详情(编辑时获取)")
    public IResultData<DetailsUserVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(userService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改用户")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateUserDto updateUserDto) {
        ResultData resultData = ResultData.instance();
        userService.update(updateUserDto);
        return resultData;
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        userService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @ApiOperation(value = "重置密码(用户编辑页面)")
    @PutMapping("/resetPassword")
    public IResultData resetPassword(@RequestBody @Validated ResetPasswordUserDto resetPasswordUserDto){
        User user = userService.findById(resetPasswordUserDto.getId());
        if (Objects.nonNull(user)){
            if (!StringUtils.hasText(user.getUsername())){
                BusinessException.throwException("该员工没有开通账号，不能重置密码");
            }
            user.setPassword(passwordEncoder.encode(SecureUtil.md5(user.getEmail())));
        }
        userService.update(user);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @ApiOperation(value = "修改当前登陆用户信息")
    @PutMapping("/updateCurrentUser")
    public IResultData updateCurrentUser(UpdateCurrentUserDto updateCurrentUserDto){
        Long userId = CurrentUserUtil.getCurrentUserId();
        User user = userService.findById(userId);
        user.setName(updateCurrentUserDto.getName());
        user.setHeadPortrait(updateCurrentUserDto.getHeadPortrait());
        if (StringUtils.hasText(updateCurrentUserDto.getNewPassword())){
            if (passwordEncoder.matches(updateCurrentUserDto.getOldPassword(),user.getPassword())) {
                user.setPassword(passwordEncoder.encode(SecureUtil.md5(user.getPassword())));
            }else {
                BusinessException.throwException("旧密码错误");
            }
        }
        userService.update(user);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @ApiOperation(value = "获取当前登陆用户信息")
    @GetMapping("/currentUserDetails")
    public IResultData<CurrentUserDetailsVo> currentUserDetails(){
        ResultData resultData = ResultData.instance();
        Long userId = CurrentUserUtil.getCurrentUserId();
        User user = userService.findById(userId);
        if (Objects.nonNull(user)){
            CurrentUserDetailsVo currentUserDetailsVo = new CurrentUserDetailsVo();
            BeanUtils.copyProperties(user,currentUserDetailsVo);
            Role role = roleService.findByUserId(user.getId());
            if (Objects.nonNull(role)){
                currentUserDetailsVo.setRoleName(role.getName());
                currentUserDetailsVo.setRoleId(role.getId());
                currentUserDetailsVo.setResources(role.getResources());
            }
            currentUserDetailsVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            Department department = departmentUserExposeService.findDepartment(user.getId());
            if (Objects.nonNull(department)){
                currentUserDetailsVo.setDepartmentName(department.getName());
                currentUserDetailsVo.setDepartmentId(department.getId());
            }
            resultData.setData(currentUserDetailsVo);
        }
        return resultData;
    }

}
