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
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.license.License;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.license.LicenseExposeService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.user.QUser;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.dto.*;
import com.lion.upms.entity.user.vo.*;
import com.lion.upms.service.role.RoleService;
import com.lion.upms.service.role.RoleUserService;
import com.lion.upms.service.user.UserService;
import com.lion.upms.service.user.UserTypeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: ???????????????
 * @date 2021/3/22??????9:22
 */
@RestController
@RequestMapping("/user")
@Validated
@Api(tags = {"????????????"})
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

    @Autowired
    private UserTypeService userTypeService;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @DubboReference
    private LicenseExposeService licenseExposeService;

    @PostMapping("/add")
    @ApiOperation(value = "????????????")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddUserDto addUserDto){
        userService.add(addUserDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/list")
    @ApiOperation(value = "????????????")
    public IPageResultData<List<ListUserVo>> list(@ApiParam(value = "???????????????") Boolean isMyDepartment, @ApiParam(value = "??????") Long departmentId,@ApiParam(value = "????????????") Long userTypeId,@ApiParam(value = "????????????") Integer number,@ApiParam(value = "??????")  String name,@ApiParam(value = "??????") Long roleId, @ApiParam(value = "???????????????") Boolean isAdmin, LionPage lionPage){
        if (Objects.equals(isMyDepartment,true)) {
            Department department = departmentUserExposeService.findDepartment(CurrentUserUtil.getCurrentUserId());
            if (Objects.nonNull(department)) {
                departmentId = department.getId();
            }
        }
        return userService.list(departmentId, userTypeId, number, name, roleId,isAdmin , lionPage);
    }


    @GetMapping("/details")
    @ApiOperation(value = "????????????(???????????????)")
    public IResultData<DetailsUserVo> details(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(userService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "????????????")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateUserDto updateUserDto) {
        ResultData resultData = ResultData.instance();
        userService.update(updateUserDto);
        return resultData;
    }

    @ApiOperation(value = "????????????")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        userService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @ApiOperation(value = "????????????(??????????????????)")
    @PutMapping("/resetPassword")
    public IResultData resetPassword(@RequestBody @Validated ResetPasswordUserDto resetPasswordUserDto){
        com.lion.core.Optional<User> optional = userService.findById(resetPasswordUserDto.getId());
        User user = null;
        if (optional.isPresent()){
            user = optional.get();
            if (!StringUtils.hasText(user.getUsername())){
                BusinessException.throwException(MessageI18nUtil.getMessage("0000014"));
            }
            user.setPassword(passwordEncoder.encode(SecureUtil.md5(user.getEmail())));
        }
        userService.update(user);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @ApiOperation(value = "??????????????????????????????")
    @PutMapping("/updateCurrentUser")
    public IResultData updateCurrentUser(@RequestBody UpdateCurrentUserDto updateCurrentUserDto){
        Long userId = CurrentUserUtil.getCurrentUserId();
        com.lion.core.Optional<User> optional = userService.findById(userId);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setName(updateCurrentUserDto.getName());
            user.setHeadPortrait(updateCurrentUserDto.getHeadPortrait());
            if (StringUtils.hasText(updateCurrentUserDto.getNewPassword())) {
                if (passwordEncoder.matches(updateCurrentUserDto.getOldPassword(), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(updateCurrentUserDto.getNewPassword()));
                } else {
                    BusinessException.throwException(MessageI18nUtil.getMessage("0000015"));
                }
            }
            userService.update(user);
        }
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @ApiOperation(value = "??????????????????????????????")
    @GetMapping("/currentUserDetails")
    public IResultData<CurrentUserDetailsVo> currentUserDetails(){
        ResultData resultData = ResultData.instance();
        Long userId = CurrentUserUtil.getCurrentUserId();
        com.lion.core.Optional<User> optional = userService.findById(userId);
        if (optional.isPresent()){
            User user = optional.get();
            CurrentUserDetailsVo currentUserDetailsVo = new CurrentUserDetailsVo();
            BeanUtils.copyProperties(user,currentUserDetailsVo);
            Role role = roleService.findByUserId(user.getId());
            if (Objects.nonNull(role)){
                currentUserDetailsVo.setRoleName(role.getName());
                currentUserDetailsVo.setRoleId(role.getId());
                currentUserDetailsVo.setResources(role.getResources());
                List<License> list = licenseExposeService.findAll();
                if (list.size()>0){
                    License license = list.get(0);
                    if (Objects.nonNull(license.getEffectivTime())) {
                        currentUserDetailsVo.setIsE(license.getEffectivTime().isBefore(LocalDate.now()));
                    }
                }

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

    @PostMapping("/type/add")
    @ApiOperation(value = "??????????????????")
    public IResultData addUserType(@RequestBody @Validated({Validator.Insert.class}) AddUserTypeDto addUserTypeDto){
        userTypeService.add(addUserTypeDto);
        return ResultData.instance();
    }

    @PutMapping("/type/update")
    @ApiOperation(value = "??????????????????")
    public IResultData updateUserType(@RequestBody @Validated({Validator.Update.class}) UpdateUserTypeDto updateUserTypeDto){
        userTypeService.update(updateUserTypeDto);
        return ResultData.instance();
    }

    @DeleteMapping("/type/delete")
    @ApiOperation(value = "??????????????????")
    public IResultData deleteUserType(@RequestBody List<DeleteDto> deleteDtoList){
        userTypeService.delete(deleteDtoList);
        return ResultData.instance();
    }

    @GetMapping("/type/list")
    @ApiOperation(value = "??????????????????")
    public IPageResultData<List<ListUserTypeVo>> listUserType(@ApiParam(value = "????????????") String userTypeName, LionPage LionPage){
        return userTypeService.list(userTypeName, LionPage);
    }

    @GetMapping("/type/details")
    @ApiOperation(value = "??????????????????")
    public IResultData<DetailsUserTypeVo> detailsUserType(@ApiParam(value = "??????id") @NotNull(message = "{0000000}") Long id){
        com.lion.core.Optional<UserType> optional = userTypeService.findById(id);
        DetailsUserTypeVo vo = new DetailsUserTypeVo();
        if (optional.isPresent()) {
            UserType userType = optional.get();
            BeanUtils.copyProperties(userType, vo);
            return ResultData.instance().setData(vo);
        }
        return ResultData.instance();
    }

    @GetMapping("/export")
    @ApiOperation(value = "??????")
    public void export(@ApiParam(value = "???????????????") Boolean isMyDepartment, @ApiParam(value = "??????") Long departmentId,@ApiParam(value = "????????????") Long userTypeId,@ApiParam(value = "????????????") Integer number,@ApiParam(value = "??????")  String name,@ApiParam(value = "??????") Long roleId) throws IOException, IllegalAccessException {
        if (Objects.equals(isMyDepartment,true)) {
            Department department = departmentUserExposeService.findDepartment(CurrentUserUtil.getCurrentUserId());
            if (Objects.nonNull(department)) {
                departmentId = department.getId();
            }
        }
        userService.export(departmentId,userTypeId,number,name,roleId);
    }

    @PostMapping("/import")
    @ApiOperation(value = "??????")
    public IResultData importUser(@ApiIgnore StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        userService.importUser(multipartHttpServletRequest);
        return ResultData.instance();
    }


    @GetMapping("/test")
    public IResultData test(){

        QUser qUser = QUser.user;
        List<User> list = jpaQueryFactory.selectFrom(qUser).fetch();
        return ResultData.instance().setData(list);
    }

}
