package com.lion.device.service.cctv.impl;

import cn.hutool.core.util.NumberUtil;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.cctv.CctvDao;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.cctv.vo.CctvVo;
import com.lion.device.service.ExcelColumn;
import com.lion.device.service.ExportExcelUtil;
import com.lion.device.service.ImportExcelUtil;
import com.lion.device.service.cctv.CctvService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionCctvExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.vo.ListUserVo;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:35
 */
@Service
public class CctvServiceImpl extends BaseServiceImpl<Cctv> implements CctvService {

    @Autowired
    private CctvDao cctvDao;

    @DubboReference
    private RegionExposeService regionExposeService;


    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private RegionCctvExposeService regionCctvExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private HttpServletResponse response;

    @Override
    public List<Long> allId() {
        return cctvDao.allId();
    }

    public void importCctv(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        for (String fileName : files.keySet()) {
            MultipartFile file = files.get(fileName);
            importCctv(file.getInputStream(), fileName);
        }
    }

    @Override
    public void export(String regionId, String name, String cctvId, Boolean isOnline, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<CctvVo>> pageResultData = list(regionId,name,cctvId,isOnline,lionPage);
        List<CctvVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("名稱", "name"));
        excelColumn.add(ExcelColumn.build("cctvId", "cctvId"));
        excelColumn.add(ExcelColumn.build("所屬區域", "regionName"));
        excelColumn.add(ExcelColumn.build("狀態(是否在線)", "isOnline"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("cctv.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public IPageResultData<List<CctvVo>> list(String regionId, String name, String cctvId, Boolean isOnline, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(cctvId)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_cctvId",cctvId);
        }
        if (Objects.nonNull(isOnline)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isOnline",isOnline);
            if (Objects.equals(isOnline,false)) {
                jpqlParameter.setSearchParameter(SearchConstant.EQUAL + "_isEnable", true);
            }
        }
        if (Objects.nonNull(regionId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_regionId",regionId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Map<String, Object> sortParameter = new HashMap();
        Page<Cctv> page = findNavigator(lionPage);
        List<Cctv> list = page.getContent();
        List<CctvVo>  returnList= new ArrayList<>();
        list.forEach(cctv -> {
            returnList.add(convertVo(cctv));
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    private void importCctv(InputStream inputStream, String fileName) throws IOException {

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
        listRowKey.add("設備名稱");
        listRowKey.add("cctvId");
        listRowKey.add("所屬區域");
        listRowKey.add("ip");
        listRowKey.add("端口");
        listRowKey.add("賬號");
        listRowKey.add("密碼");
        ImportExcelUtil.check(sheet.getRow(0),listRowKey);
        for (int i = 1; i <= totalRowNum; i++) {
            User user = new User();
            Row row = sheet.getRow(i);
            String name = ImportExcelUtil.getCellValue(row.getCell(0)).toString();
            String cctvId = ImportExcelUtil.getCellValue(row.getCell(1)).toString();
            String regionName = ImportExcelUtil.getCellValue(row.getCell(2)).toString();
            String ip = ImportExcelUtil.getCellValue(row.getCell(3)).toString();
            String port = ImportExcelUtil.getCellValue(row.getCell(4)).toString();
            port = port.substring(0,port.indexOf("."));
            String account = ImportExcelUtil.getCellValue(row.getCell(5)).toString();
            account = account.substring(0,account.indexOf("."));
            String password = ImportExcelUtil.getCellValue(row.getCell(6)).toString();
            password = password.substring(0,password.indexOf("."));
            Cctv cctv = new Cctv();
            cctv.setName(name);
            cctv.setCctvId(cctvId);
            com.lion.core.Optional<Region> regionOptional = regionExposeService.find(regionName);
            if (regionOptional.isPresent()) {
                Region region = regionOptional.get();
                cctv.setDepartmentId(region.departmentId);
                cctv.setRegionId(region.getId());
                cctv.setBuildId(region.getBuildId());
                cctv.setBuildFloorId(region.getBuildFloorId());
            }
            cctv.setIp(ip);
            cctv.setPort(NumberUtil.isInteger(port)?Integer.valueOf(port):null);
            cctv.setAccount(account);
            cctv.setPassword(password);
            save(cctv);
        }
    }

    public CctvVo convertVo(Cctv cctv) {
        if (Objects.isNull(cctv)) {
            return null;
        }
        CctvVo vo = new CctvVo();
        BeanUtils.copyProperties(cctv,vo);

        com.lion.core.Optional<Build> optionalBuild = buildExposeService.findById(cctv.getBuildId());
        if (optionalBuild.isPresent()){
            vo.setBuildName(optionalBuild.get().getName());
        }

        com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorExposeService.findById(cctv.getBuildFloorId());
        if (optionalBuildFloor.isPresent()){
            vo.setBuildFloorName(optionalBuildFloor.get().getName());
        }

        com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(cctv.getRegionId());
        if (optionalRegion.isPresent()){
            vo.setRegionName(optionalRegion.get().getName());
        }

        com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(cctv.getDepartmentId());
        if (optionalDepartment.isPresent()){
            vo.setDepartmentName(optionalDepartment.get().getName());
        }

        com.lion.core.Optional<User> createUserOptional = userExposeService.findById(cctv.getCreateUserId());
        if (createUserOptional.isPresent()) {
            vo.setCreateUserName(createUserOptional.get().getName());
            vo.setCreateUserHeadPortraitUrl(fileExposeService.getUrl(createUserOptional.get().getHeadPortrait()));
            vo.setCreateUserHeadPortrait(createUserOptional.get().getHeadPortrait());
        }
        com.lion.core.Optional<User> updateUserOptional = userExposeService.findById(cctv.getCreateUserId());
        if (updateUserOptional.isPresent()) {
            vo.setUpdateUserName(updateUserOptional.get().getName());
            vo.setUpdateUserHeadPortraitUrl(fileExposeService.getUrl(updateUserOptional.get().getHeadPortrait()));
            vo.setUpdateUserHeadPortrait(updateUserOptional.get().getHeadPortrait());
        }


        return vo;
    }
}
