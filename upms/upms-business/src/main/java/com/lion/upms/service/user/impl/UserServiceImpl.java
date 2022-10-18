package com.lion.upms.service.user.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import com.lion.manage.expose.department.DepartmentExposeService;
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
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MapToBeanUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.commons.collections4.CollectionUtils;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    private final String FONT = "simsun.ttc";


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
                BusinessException.throwException("用户数已超过授权用户数");
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
        redisTemplate.opsForValue().set(RedisConstants.USER+user.getId(),user, 5, TimeUnit.MINUTES);
    }

    @Override
    public IPageResultData<List<ListUserVo>> list(Long departmentId, Long userTypeIds, Integer number, String name, Long roleId, Boolean isAdmin, String ids, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        List<Long> __ids = new ArrayList<>();
        if (StringUtils.hasText(ids)) {
            String[] str =ids.split(",");
            for (String id : str) {
                __ids.add(Long.valueOf(id));
            }
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
        List<Long> _ids = new ArrayList<>();
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
            _ids = userList;
        }

        if (_ids.size()>0 && __ids.size()>0) {
            _ids = (List<Long>) CollectionUtils.intersection(_ids, __ids);
        }else {
            _ids = __ids;
        }
        if (Objects.nonNull(departmentId)){
            List<Long> userList = departmentUserExposeService.findAllUser(departmentId);
            if (Objects.nonNull(userList) && userList.size()<=0){
                userList.add(Long.MAX_VALUE);
            }
            _ids = (List<Long>) CollectionUtils.intersection(_ids, userList);
        }
        if (Objects.nonNull(_ids) && _ids.size()>0){
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",_ids);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<User> page = this.findNavigator(lionPage);
        PageResultData pageResultData = new PageResultData(convertVo(page.getContent()),page.getPageable(),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public void export(Long departmentId, Long userTypeIds, Integer number, String name, Long roleId, String ids, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListUserVo>> pageResultData = list(departmentId,userTypeIds,number,name,roleId, null,ids , lionPage);
        List<ListUserVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("姓名", "name"));
        excelColumn.add(ExcelColumn.build("類型", "userType.userTypeName"));
        excelColumn.add(ExcelColumn.build("科室", "departmentName"));
        excelColumn.add(ExcelColumn.build("職員編號", "number"));
        excelColumn.add(ExcelColumn.build("賬號", "username"));
        excelColumn.add(ExcelColumn.build("賬號啓用", "isCreateAccount"));
        excelColumn.add(ExcelColumn.build("角色", "roleName"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("user.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public void exportPdf(Long departmentId, Long userTypeIds, Integer number, String name, Long roleId, LionPage lionPage) throws IOException, DocumentException {
        IPageResultData<List<ListUserVo>> pageResultData = list(departmentId,userTypeIds,number,name,roleId, null,null , lionPage);
        List<ListUserVo> list = pageResultData.getData();
        BaseFont bfChinese = BaseFont.createFont(FONT+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("staff.pdf", "UTF-8"));
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, servletOutputStream);
        String userName = CurrentUserUtil.getCurrentUserUsername();
        writer.setPageEvent(new PdfPageEventHelper(FONT,userName));
        document.open();
        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[]{20, 20, 20, 20, 20});
        table.setWidthPercentage(100);
        PdfPCell cellTitle = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000002"), new Font(bfChinese,24)));
        cellTitle.setColspan(5);
        cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTitle);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PdfPCell cellTitle1 = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000003")+":" +simpleDateFormat.format(new Date()), new Font(bfChinese)));
        cellTitle1.setColspan(5);
        table.addCell(cellTitle1);
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("0000026"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("0000028"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000006"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000007"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000008"), fontChinese));
        for (ListUserVo user : list) {
            table.addCell(new Paragraph(user.getName(), fontChinese));
            table.addCell(new Paragraph(Objects.isNull(user.getNumber())?"":String.valueOf(user.getNumber()), fontChinese));
            table.addCell(new Paragraph(user.getDepartmentName(), fontChinese));
            table.addCell(new Paragraph(user.getUserType().getUserTypeName(), fontChinese));
            table.addCell(new Paragraph(user.getGender().getDesc(), fontChinese));
        }
        document.add(table);
        document.close();
        servletOutputStream.flush();
        servletOutputStream.close();
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
        List<License> list = licenseExposeService.findAll();
        if (Objects.nonNull(list) && list.size()>0) {
            License license = list.get(0);
            long userCount = this.count();
            if (userCount >= license.getUserNum()) {
                BusinessException.throwException("用户数已超过授权用户数");
            }
        }
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
        listRowKey.add("姓名");
        listRowKey.add("類型");
        listRowKey.add("科室");
        listRowKey.add("職員編號");
        listRowKey.add("賬號");
        listRowKey.add("賬號啓用");
        listRowKey.add("角色");
        listRowKey.add("聯繫電話");
        listRowKey.add("生日");
        listRowKey.add("性別");
        ImportExcelUtil.check(sheet.getRow(0),listRowKey);
        for (int i = 1; i <= totalRowNum; i++) {
            User user = new User();
            Row row = sheet.getRow(i);
            String name = ImportExcelUtil.getCellValue(row.getCell(0)).toString();
            String userTypeName = ImportExcelUtil.getCellValue(row.getCell(1)).toString();
            String departmentName = ImportExcelUtil.getCellValue(row.getCell(2)).toString();
            String number = ImportExcelUtil.getCellValue(row.getCell(3)).toString();
            number = number.substring(0,number.indexOf("."));
            String username = ImportExcelUtil.getCellValue(row.getCell(4)).toString();
            String isCreateAccount = ImportExcelUtil.getCellValue(row.getCell(5)).toString();
            String roleName = ImportExcelUtil.getCellValue(row.getCell(6)).toString();
            String phoneNumber = ImportExcelUtil.getCellValue(row.getCell(7)).toString();
            String birthday = ImportExcelUtil.getCellValue(row.getCell(8)).toString();
            String gender = ImportExcelUtil.getCellValue(row.getCell(9)).toString();

            if (StringUtils.hasText(isCreateAccount) && Objects.equals(isCreateAccount.toLowerCase().trim(),"true")) {
                Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
                Matcher m = p.matcher(username);
                boolean b = m.matches();
                if (!b) {
                    BusinessException.throwException(username+"--账号请输入正确的邮箱");
                }
                user.setUsername(username);
                user.setEmail(username);
                user.setPassword(passwordEncoder.encode(SecureUtil.md5(username)));
            }
            user.setPhoneNumber(phoneNumber.substring(0,phoneNumber.indexOf(".")));
            user.setBirthday(LocalDate.parse(birthday));
            if (Objects.equals(gender,"男")) {
                user.setGender(Gender.MAN);
            }else if (Objects.equals(gender,"女")) {
                user.setGender(Gender.WOMAN);
            }
            user.setName(name);
            user.setGender(Gender.MAN);
            if (!NumberUtil.isInteger(number)) {
                BusinessException.throwException(number+"--员工编码只能是数字");
            }
            user.setNumber(NumberUtil.isInteger(number)?Integer.valueOf(number):null);
            if (StringUtils.hasText(userTypeName)) {
                Optional<UserType> optionalUserType = userTypeService.find(userTypeName);
                if (optionalUserType.isPresent()) {
                    user.setUserTypeId(optionalUserType.get().getId());
                }
            }
            assertNumberExist(NumberUtil.isInteger(number)?Integer.valueOf(number):null,null);
            assertEmailExist(username,null);
            user = save(user);
            if (StringUtils.hasText(departmentName)) {
                com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.find(departmentName);
                if (optionalDepartment.isPresent()){
                    departmentUserExposeService.relationDepartment(user.getId(),optionalDepartment.get().getId());
                }
            }
            if (StringUtils.hasText(roleName)) {
                Role role = roleDao.findFirstByName(roleName);
                if (Objects.nonNull(role)){
                    roleUserService.relationRole(user.getId(),role.getId());
                }
            }
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
            detailsUserVo.setCctv(systemAlarm.getCctvUrl());
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
        redisTemplate.opsForValue().set(RedisConstants.USER+user.getId(),user, 5, TimeUnit.MINUTES);
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
