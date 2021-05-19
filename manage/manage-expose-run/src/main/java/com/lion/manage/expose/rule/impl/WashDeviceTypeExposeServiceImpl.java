package com.lion.manage.expose.rule.impl;

import com.lion.core.service.BaseService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashDeviceTypeDao;
import com.lion.manage.entity.rule.WashDeviceType;
import com.lion.manage.expose.rule.WashDeviceTypeExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/19 下午8:11
 */
@DubboService(interfaceClass = WashDeviceTypeExposeService.class)
public class WashDeviceTypeExposeServiceImpl extends BaseServiceImpl<WashDeviceType> implements WashDeviceTypeExposeService {

    @Autowired
    private WashDeviceTypeDao washDeviceTypeDao;

    @Override
    public List<com.lion.manage.entity.enums.WashDeviceType> find(Long washId) {
        List<WashDeviceType> list = washDeviceTypeDao.findByWashId(washId);
        List<com.lion.manage.entity.enums.WashDeviceType> returnList = new ArrayList<>();
        list.forEach(washDeviceType -> {
            returnList.add(washDeviceType.getType());
        });
        return returnList;
    }
}
