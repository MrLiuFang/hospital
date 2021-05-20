package com.lion.manage.expose.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.assets.AssetsDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.service.assets.AssetsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 上午9:50
 **/
@DubboService
public class AssetsExposeServiceImpl extends BaseServiceImpl<Assets> implements AssetsExposeService {

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private AssetsDao assetsDao;

    @Override
    public Assets find(Long tagId) {
        return assetsService.findByTagId(tagId);
    }

    @Override
    public List<Map<String, Object>> count(Long buildFloorId) {
        return assetsDao.groupRegionCount(buildFloorId);
    }

    @Override
    public Integer countByDepartmentId(Long departmentId) {
        return assetsDao.countByDepartmentId(departmentId);
    }
}
