package com.lion.manage.expose.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.region.RegionWarningBellDao;
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.expose.region.RegionWarningBellExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/25 下午7:55
 */
@DubboService(interfaceClass = RegionWarningBellExposeService.class)
public class RegionWarningBellExposeServiceImpl extends BaseServiceImpl<RegionWarningBell> implements RegionWarningBellExposeService {

    @Autowired
    private RegionWarningBellDao regionWarningBellDao;

    @Override
    public RegionWarningBell find(Long warningBellId) {
        return regionWarningBellDao.findFirstByWarningBellId(warningBellId);
    }
}
