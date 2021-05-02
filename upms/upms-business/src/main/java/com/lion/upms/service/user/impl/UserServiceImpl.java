package com.lion.upms.service.user.impl;

import cn.hutool.crypto.SecureUtil;
import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.upms.dao.role.RoleDao;
import com.lion.upms.dao.user.UserDao;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.role.RoleUser;
import com.lion.upms.entity.role.vo.DetailsRoleUserVo;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.dto.AddUserDto;
import com.lion.upms.entity.user.dto.UpdateUserDto;
import com.lion.upms.entity.user.vo.DetailsUserVo;
import com.lion.upms.entity.user.vo.ListUserVo;
import com.lion.upms.service.role.RoleService;
import com.lion.upms.service.role.RoleUserService;
import com.lion.upms.service.user.UserService;
import com.lion.utils.MapToBeanUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:02
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Autowired
    private UserDao userDao;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleUserService roleUserService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private TagUserExposeService tagUserExposeService;

    @Override
    public User findUser(String username) {
        return userDao.findFirstByUsername(username);
    }

    @Override
    public List<DetailsRoleUserVo> detailsRoleUser(Long roleId) {
        List<DetailsRoleUserVo> returnList = new ArrayList<DetailsRoleUserVo>();
        List<User> list = userDao.findUserByRoleId(roleId);
        list.forEach(user -> {
            DetailsRoleUserVo detailsRoleUserVo = new DetailsRoleUserVo();
            BeanUtils.copyProperties(user,detailsRoleUserVo);
            Department department = departmentUserExposeService.findDepartment(user.getId());
            if (Objects.nonNull(department)) {
                detailsRoleUserVo.setDepartmentName(department.getName());
            }
            detailsRoleUserVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            returnList.add(detailsRoleUserVo);
        });
        return returnList;
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void add(AddUserDto addUserDto) {
        User user = new User();
        BeanUtils.copyProperties(addUserDto,user);
        if (Objects.nonNull(addUserDto.getIsCreateAccount()) && addUserDto.getIsCreateAccount()){
            user.setUsername(user.getEmail());
            user.setPassword(passwordEncoder.encode(SecureUtil.md5(user.getEmail())));
        }
        assertEmailExist(user.getEmail(),null);
        assertNumberExist(user.getNumber(),null);
        assertTagCode(user.getTagCode());
        user = this.save(user);
        roleUserService.relationRole(user.getId(),addUserDto.getRoleId());
        departmentUserExposeService.relationDepartment(user.getId(),addUserDto.getDepartmentId());
        departmentResponsibleUserExposeService.relationDepartment(user.getId(),addUserDto.getResponsibleDepartmentIds());
        if (StringUtils.hasText(user.getTagCode())) {
            tagUserExposeService.binding(user.getId(), user.getTagCode());
        }
        redisTemplate.opsForValue().set(RedisConstants.USER+user.getId(),user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public IPageResultData<List<ListUserVo>> list(Long departmentId, UserType userType, Integer number, String name, Long roleId, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(departmentId)){
            List<Long> userList = departmentUserExposeService.findAllUser(departmentId);
            if (Objects.nonNull(userList) && userList.size()<=0){
                userList.add(Long.MAX_VALUE);
            }
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",userList);
        }
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(number)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_number",number);
        }
        if (Objects.nonNull(userType)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_userType",userType);
        }
        if (Objects.nonNull(roleId)){
            List<RoleUser> list = roleUserService.find(roleId);
            List<Long> userList = new ArrayList<>();
            list.forEach(roleUser -> {
                userList.add(roleUser.getUserId());
            });
            if (userList.size()<=0){
                userList.add(Long.MAX_VALUE);
            }
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",userList);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<User> page = this.findNavigator(lionPage);
        PageResultData pageResultData = new PageResultData(convertVo(page.getContent()),page.getPageable(),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public DetailsUserVo details(Long id) {
        User user = findById(id);
        DetailsUserVo detailsUserVo = new DetailsUserVo();
        Role role = roleDao.findByUserId(user.getId());
        if (Objects.nonNull(role)){
            detailsUserVo.setRoleName(role.getName());
            detailsUserVo.setRoleId(role.getId());
        }
        detailsUserVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
        Department department = departmentUserExposeService.findDepartment(user.getId());
        if (Objects.nonNull(department)){
            detailsUserVo.setDepartmentName(department.getName());
            detailsUserVo.setDepartmentId(department.getId());
        }
        List<Department> list = departmentResponsibleUserExposeService.findDepartment(user.getId());
        if (Objects.nonNull(list) && list.size()>0){
            List<DetailsUserVo.ResponsibleDepartmentVo> responsibleDepartment = new ArrayList<DetailsUserVo.ResponsibleDepartmentVo>();
            list.forEach(d -> {
                DetailsUserVo.ResponsibleDepartmentVo responsibleDepartmentVo = new DetailsUserVo.ResponsibleDepartmentVo();
                responsibleDepartmentVo.setDepartmentId(d.getId());
                responsibleDepartmentVo.setDepartmentName(d.getName());
                List<Map<String,Object>> list1 = departmentResponsibleUserExposeService.responsibleUser(d.getId());
                if (Objects.nonNull(list1) && list1.size()>0){
                    List<DetailsUserVo.ResponsibleUserVo> responsibleUser = new ArrayList<>();
                    list1.forEach(map->{
                        DetailsUserVo.ResponsibleUserVo responsibleUserVo = new DetailsUserVo.ResponsibleUserVo();
                        MapToBeanUtil.convert(responsibleUserVo,map);
                        responsibleUser.add(responsibleUserVo);
                    });
                    responsibleDepartmentVo.setResponsibleUser(responsibleUser);
                }
                responsibleDepartment.add(responsibleDepartmentVo);
            });
            detailsUserVo.setResponsibleDepartment(responsibleDepartment);
        }
        BeanUtils.copyProperties(user,detailsUserVo);
        return detailsUserVo;
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void update(UpdateUserDto updateUserDto) {
        User user = new User();
        BeanUtils.copyProperties(updateUserDto,user);
        if (Objects.equals(true,updateUserDto.getIsCreateAccount())){
            User tmp = findById(user.getId());
            if (!StringUtils.hasText(tmp.getPassword())) {
                if (!StringUtils.hasText(tmp.getEmail()) && !StringUtils.hasText(user.getEmail())) {
                    BusinessException.throwException("该用户没有email无法创建账号");
                }else {
                    user.setUsername(StringUtils.hasText(user.getEmail())?user.getEmail():tmp.getEmail());
                    user.setPassword(passwordEncoder.encode(SecureUtil.md5(StringUtils.hasText(user.getEmail())?user.getEmail():tmp.getEmail())));
                }
            }
        }else {
            user.setUsername("");
            user.setPassword("");
        }
        assertEmailExist(user.getEmail(),user.getId());
        assertNumberExist(user.getNumber(),user.getId());
        assertTagCode(user.getTagCode());
        this.update(user);
        roleUserService.relationRole(user.getId(),updateUserDto.getRoleId());
        departmentUserExposeService.relationDepartment(user.getId(),updateUserDto.getDepartmentId());
        departmentResponsibleUserExposeService.relationDepartment(user.getId(),updateUserDto.getResponsibleDepartmentIds());
        tagUserExposeService.binding(user.getId(),user.getTagCode());
        redisTemplate.opsForValue().set(RedisConstants.USER+user.getId(),user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(d->{
            User user = this.findById(d.getId());
            if (Objects.nonNull(user) ) {
                deleteById(d.getId());
                roleUserService.deleteByUserId(d.getId());
                departmentUserExposeService.deleteByUserId(d.getId());
                departmentResponsibleUserExposeService.deleteByUserId(d.getId());
                redisTemplate.delete(RedisConstants.USER+d.getId());
                tagUserExposeService.unbinding(user.getId(),false);
            }
        });
    }

    private List<ListUserVo> convertVo(List<User> list){
        List<ListUserVo> returnList = new ArrayList<ListUserVo>();
        list.forEach(user -> {
            ListUserVo userVo = new ListUserVo();
            BeanUtils.copyProperties(user,userVo);
            Department department = departmentUserExposeService.findDepartment(user.getId());
            if (Objects.nonNull(department)){
                userVo.setDepartmentName(department.getName());
            }
            Role role = roleDao.findByUserId(user.getId());
            if (Objects.nonNull(role)){
                userVo.setRoleName(role.getName());
            }
            userVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            returnList.add(userVo);
        });
        return returnList;
    }

    private void assertEmailExist(String email, Long id) {
        User user = userDao.findFirstByEmail(email);
        if ((Objects.isNull(id) && Objects.nonNull(user)) || (Objects.nonNull(id) && Objects.nonNull(user) && !Objects.equals(user.getId(),id)) ){
            BusinessException.throwException("该邮箱已存在");
        }
    }

    private void assertTagCode(String tagCode){
        if (StringUtils.hasText(tagCode)) {
            Tag tag = tagExposeService.find(tagCode);
            if (Objects.isNull(tag)) {
                BusinessException.throwException("标签不存在");
            }
        }
    }

    private void assertNumberExist(Integer number, Long id) {
        User user = userDao.findFirstByNumber(number);
        if ((Objects.isNull(id) && Objects.nonNull(user)) ||(Objects.nonNull(id) && Objects.nonNull(user) && !Objects.equals(user.getId(),id))  ){
            BusinessException.throwException("该工号已存在");
        }
    }

}
