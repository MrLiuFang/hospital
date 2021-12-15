package com.lion.manage.expose.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.expose.region.RegionCctvExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/30 上午10:52
 */
@DubboService(interfaceClass = RegionCctvExposeService.class)
public class RegionCctvExposeServiceImpl extends BaseServiceImpl<RegionCctv> implements RegionCctvExposeService {

    @Autowired
    private RegionCctvDao regionCctvDao;


    @Override
    public int count(Long regionId) {
        return regionCctvDao.countByRegionId(regionId);
    }

    @Override
    public RegionCctv find(Long cctvId) {
        return regionCctvDao.findFirstByCctvId(cctvId);
    }
}
