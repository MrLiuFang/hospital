package com.lion.device.service.cctv.impl;

import cn.hutool.core.util.NumberUtil;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.cctv.CctvDao;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.service.ImportExcelUtil;
import com.lion.device.service.cctv.CctvService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.upms.entity.user.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
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
        listRowKey.add("port");
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
            String password = ImportExcelUtil.getCellValue(row.getCell(6)).toString();
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


}
