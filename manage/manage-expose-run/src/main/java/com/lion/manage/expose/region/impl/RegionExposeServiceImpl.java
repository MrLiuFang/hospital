package com.lion.manage.expose.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.entity.region.Region;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午2:58
 **/
@DubboService(interfaceClass = RegionExposeService.class)
public class RegionExposeServiceImpl extends BaseServiceImpl<Region> implements RegionExposeService {

    @Autowired
    private RegionDao regionDao;

    @Override
    public Region find(Long deviceGroupId) {
        return regionDao.findFirstByDeviceGroupId(deviceGroupId);
    }

    @Override
    @Transactional
    public void deleteDeviceGroup(Long deviceGroupId) {
        regionDao.deleteDeviceGroup(deviceGroupId);
    }
}
