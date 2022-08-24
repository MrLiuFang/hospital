package com.lion.manage.expose.region.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.event.entity.vo.DepartmentRegionInfoVo;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.vo.ListRegionVo;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/4/24 下午2:58
 **/
@DubboService(interfaceClass = RegionExposeService.class)
public class RegionExposeServiceImpl extends BaseServiceImpl<Region> implements RegionExposeService {

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private RegionService regionService;

    @Autowired
    private DepartmentService departmentService;

//    @Override
//    public Region find(Long deviceGroupId) {
//        return regionDao.findFirstByDeviceGroupId(deviceGroupId);
//    }

//    @Override
//    @Transactional
//    public void deleteDeviceGroup(Long deviceGroupId) {
//        regionDao.deleteDeviceGroup(deviceGroupId);
//    }

    @Override
    public List<Region> findByBuildFloorId(Long buildFloorId) {
        return regionDao.findByBuildFloorId(buildFloorId);
    }

    @Override
    public List<Region> findByDepartmentId(Long departmentId) {
        return regionDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Region> findByDepartmentIds(List<Long> departmentIds) {
        return regionDao.findByDepartmentIdIn(departmentIds);
    }

    @Override
    public PageResultData<List<Region>> find(LionPage lionPage) {
        Page<Region> page = findNavigator(lionPage);
        PageResultData pageResultData = new PageResultData(page.getContent(),new LionPage(page.getNumber(),page.getSize()),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public List<ListRegionVo> find(String keyword, Long departmentId) {
        List<Long> list = new ArrayList<>();
        list.add(departmentId);
        IPageResultData<List<ListRegionVo>>  pageResultData = regionService.list(keyword,keyword,list,null,null,null,null,new LionPage(0,Integer.MAX_VALUE));
        return pageResultData.getData();
    }
}
