package com.lion.upms.service.user.impl;

import cn.hutool.core.util.NumberUtil;
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
import com.lion.device.expose.tag.TagLogExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.expose.service.CurrentPositionExposeService;
import com.lion.event.expose.service.SystemAlarmExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.license.License;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.license.LicenseExposeService;
import com.lion.upms.dao.role.RoleDao;
import com.lion.upms.dao.user.UserDao;
import com.lion.upms.entity.enums.Gender;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.role.RoleUser;
import com.lion.upms.entity.role.vo.DetailsRoleUserVo;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.dto.AddUserDto;
import com.lion.upms.entity.user.dto.UpdateUserDto;
import com.lion.upms.entity.user.vo.DetailsUserVo;
import com.lion.upms.entity.user.vo.ListUserVo;
import com.lion.upms.service.role.RoleService;
import com.lion.upms.service.role.RoleUserService;
import com.lion.upms.service.user.UserService;
import com.lion.upms.service.user.UserTypeService;
import com.lion.upms.utils.ExcelColumn;
import com.lion.upms.utils.ExportExcelUtil;
import com.lion.upms.utils.ImportExcelUtil;
import com.lion.utils.MapToBeanUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22??????9:02
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

    @DubboReference
    private TagLogExposeService tagLogExposeService;

    @DubboReference
    private CurrentPositionExposeService currentPositionExposeService;

    @DubboReference
    private SystemAlarmExposeService systemAlarmExposeService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserTypeService userTypeService;

    @Autowired
    private HttpServletResponse response;

    @DubboReference
    private LicenseExposeService licenseExposeService;


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
        List<License> list = licenseExposeService.findAll();
        if (Objects.nonNull(list) && list.size()>0) {
            License license = list.get(0);
            long userCount = this.count();
            if (userCount >= license.getUserNum()) {
                BusinessException.throwException("?????????????????????????????????");
            }
        }
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
            tagUserExposeService.binding(user.getId(), user.getTagCode(), addUserDto.getDepartmentId());
        }
        redisTemplate.opsForValue().set(RedisConstants.USER+user.getId(),user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public IPageResultData<List<ListUserVo>> list(Long departmentId, Long userTypeIds, Integer number, String name, Long roleId, Boolean isAdmin, LionPage lionPage) {
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
        if (Objects.nonNull(userTypeIds)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_userTypeId",userTypeIds);
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
        if (Objects.equals(isAdmin,true)){
            List<Role> roleList = roleService.find("admin","super_admin");
            List<Long> userList = new ArrayList<>();
            roleList.forEach(role -> {
                List<RoleUser> list = roleUserService.find(role.getId());
                list.forEach(roleUser -> {
                    userList.add(roleUser.getUserId());
                });
            });
            if (userList.size()<=0){
                userList.add(Long.MAX_VALUE);
            }
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",userList);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<User> page = this.findNavigator(lionPage);
        PageResultData pageResultData = new PageResultData(convertVo(page.getContent()),page.getPageable(),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public void export(Long departmentId, Long userTypeIds, Integer number, String name, Long roleId) throws IOException, IllegalAccessException {
        IPageResultData<List<ListUserVo>> pageResultData = list(departmentId,userTypeIds,number,name,roleId, null, new LionPage(0,Integer.MAX_VALUE));
        List<ListUserVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("0000026"), "name"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("0000027"), "userType.userTypeName"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("0000028"), "number"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("0000029"), "username"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("0000029"), "username"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("0000030"), "isCreateAccount"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("0000031"), "roleName"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("user.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importUser(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        for (String fileName : files.keySet()) {
            MultipartFile file = files.get(fileName);
            importUser(file.getInputStream(), fileName);
        }
    }

    private void importUser(InputStream inputStream, String fileName) throws IOException {
        if (Objects.isNull(inputStream)) {
            return;
        }
        Optional<Workbook> optional = ImportExcelUtil.getWorkbook(inputStream,fileName);
        if (!optional.isPresent()) {
            return;
        }
        Workbook wookbook = optional.get();
        Sheet sheet = wookbook.getSheetAt(0);
        int totalRowNum = sheet.getLastRowNum();
        List<String> listRowKey = new ArrayList<String>();
        listRowKey.add("username");
        listRowKey.add("password");
        listRowKey.add("name");
        listRowKey.add("email");
        listRowKey.add("gender");
        listRowKey.add("birthday");
        listRowKey.add("number");
        listRowKey.add("tagCode");
        listRowKey.add("phoneNumber");
        listRowKey.add("address");
        ImportExcelUtil.check(sheet.getRow(0),listRowKey);
        for (int i = 1; i <= totalRowNum; i++) {
            AddUserDto addUserDto = new AddUserDto();
            Row row = sheet.getRow(i);
            String username = ImportExcelUtil.getCellValue(row.getCell(0)).toString();
            String password = ImportExcelUtil.getCellValue(row.getCell(1)).toString();
            String name = ImportExcelUtil.getCellValue(row.getCell(2)).toString();
            String email = ImportExcelUtil.getCellValue(row.getCell(3)).toString();
            String gender = ImportExcelUtil.getCellValue(row.getCell(4)).toString();
            String birthday = ImportExcelUtil.getCellValue(row.getCell(5)).toString();
            String number = ImportExcelUtil.getCellValue(row.getCell(6)).toString();
            String tagCode = ImportExcelUtil.getCellValue(row.getCell(7)).toString();
            String phoneNumber = ImportExcelUtil.getCellValue(row.getCell(8)).toString();
            String address = ImportExcelUtil.getCellValue(row.getCell(9)).toString();

            addUserDto.setUsername(username);
            addUserDto.setPassword(passwordEncoder.encode(SecureUtil.md5(password)));
            addUserDto.setName(name);
            addUserDto.setEmail(email);
            addUserDto.setGender(Gender.valueOf(gender));
            addUserDto.setBirthday(LocalDate.parse(birthday));
            addUserDto.setNumber(NumberUtil.isInteger(number)?Integer.valueOf(number):null);
            addUserDto.setTagCode(tagCode);
            addUserDto.setPhoneNumber(phoneNumber);
            addUserDto.setAddress(address);

            add(addUserDto);
        }
    }




    @Override
    public DetailsUserVo details(Long id) {
        com.lion.core.Optional<User> optional = findById(id);
        if (optional.isEmpty()){
            return null;
        }
        User user = optional.get();
        DetailsUserVo detailsUserVo = new DetailsUserVo();
        BeanUtils.copyProperties(user,detailsUserVo);
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
        SystemAlarm systemAlarm =  systemAlarmExposeService.findLastByPi(user.getId());
        if (Objects.nonNull(systemAlarm)) {
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            detailsUserVo.setAlarm(systemAlarmType.getDesc());
            detailsUserVo.setAlarmType(systemAlarmExposeService.getSystemAlarmTypeCode(systemAlarm.getSat()));
            detailsUserVo.setAlarmDataTime(systemAlarm.getDt());
            detailsUserVo.setAlarmId(systemAlarm.get_id());
        }
        com.lion.core.Optional<UserType> optionalUserType = userTypeService.findById(user.getUserTypeId());
        detailsUserVo.setUserType(optionalUserType.isPresent()?optionalUserType.get():null);
        detailsUserVo.setIsCreateAccount(StringUtils.hasText(user.getUsername()));
        return detailsUserVo;
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void update(UpdateUserDto updateUserDto) {
        User user = new User();
        BeanUtils.copyProperties(updateUserDto,user);
        if (Objects.equals(updateUserDto.getIsCreateAccount(),true)){
            com.lion.core.Optional<User> optionalTmp = findById(user.getId());
            if (optionalTmp.isPresent()) {
                User tmp = optionalTmp.get();
                if (!StringUtils.hasText(tmp.getUsername()) && !StringUtils.hasText(tmp.getPassword())) {
                    user.setUsername(StringUtils.hasText(user.getEmail()) ? user.getEmail() : tmp.getEmail());
                    user.setPassword(passwordEncoder.encode(SecureUtil.md5(StringUtils.hasText(user.getEmail()) ? user.getEmail() : tmp.getEmail())));
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
        tagUserExposeService.binding(user.getId(),user.getTagCode(), updateUserDto.getDepartmentId());
        redisTemplate.opsForValue().set(RedisConstants.USER+user.getId(),user, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        Tag tag = tagExposeService.find(user.getTagCode());
        if (Objects.nonNull(tag)) {
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tag.getId());
        }
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(d->{
            com.lion.core.Optional<User> optional = this.findById(d.getId());
            if (optional.isPresent() ) {
                deleteById(d.getId());
                roleUserService.deleteByUserId(d.getId());
                departmentUserExposeService.deleteByUserId(d.getId());
                departmentResponsibleUserExposeService.deleteByUserId(d.getId());
                redisTemplate.delete(RedisConstants.USER+d.getId());
                tagUserExposeService.unbinding(optional.get().getId(),false);
                Tag tag = tagExposeService.find(optional.get().getTagCode());
                if (Objects.nonNull(tag)) {
                    redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tag.getId());
                }
            }
            currentPositionExposeService.delete(d.getId(),null,null);
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
            com.lion.core.Optional<UserType> optionalUserType = userTypeService.findById(user.getUserTypeId());
            userVo.setUserType(optionalUserType.isPresent()?optionalUserType.get():null);
            userVo.setIsCreateAccount(StringUtils.hasText(user.getUsername()));
            returnList.add(userVo);
        });
        return returnList;
    }

    private void assertEmailExist(String email, Long id) {
        User user = userDao.findFirstByEmail(email);
        if ((Objects.isNull(id) && Objects.nonNull(user)) || (Objects.nonNull(id) && Objects.nonNull(user) && !Objects.equals(user.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("0000017",new Object[]{email}));
        }
    }

    private void assertTagCode(String tagCode){
        if (StringUtils.hasText(tagCode)) {
            Tag tag = tagExposeService.find(tagCode);
            if (Objects.isNull(tag)) {
                BusinessException.throwException(MessageI18nUtil.getMessage("0000018",new Object[]{tagCode}));
            }
        }
    }

    private void assertNumberExist(Integer number, Long id) {
        User user = userDao.findFirstByNumber(number);
        if ((Objects.isNull(id) && Objects.nonNull(user)) ||(Objects.nonNull(id) && Objects.nonNull(user) && !Objects.equals(user.getId(),id))  ){
            BusinessException.throwException(MessageI18nUtil.getMessage("0000019",new Object[]{String.valueOf(number)}));
        }
    }

}
