package com.lion.manage.expose.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashDeviceDao;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.expose.rule.WashDeviceExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午8:51
 **/
@DubboService(interfaceClass = WashDeviceExposeService.class)
public class WashDeviceExposeServiceImpl extends BaseServiceImpl<WashDevice> implements WashDeviceExposeService {

    @Autowired
    private WashDeviceDao washDeviceDao;

    @Override
    public List<WashDevice> find(Long washId) {
        return washDeviceDao.findByWashId(washId);
    }
}
