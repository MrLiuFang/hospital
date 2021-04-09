package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashDeviceDao;
import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.service.rule.WashDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午5:00
 */
@Service
public class WashDeviceServiceImpl extends BaseServiceImpl<WashDevice> implements WashDeviceService {

    @Autowired
    private WashDeviceDao washDeviceDao;

    @Override
    public void add(List<WashDeviceType> deviceType, Long washId) {
        if (Objects.nonNull(washId)){
            washDeviceDao.deleteByWashId(washId);
        }
        deviceType.forEach(type->{
            WashDevice washDevice = new WashDevice();
            washDevice.setType(type);
            washDevice.setWashId(washId);
            save(washDevice);
        });
    }

    @Override
    public int delete(Long washId) {
        return washDeviceDao.deleteByWashId(washId);
    }

    @Override
    public List<WashDevice> find(Long washId) {
        return washDeviceDao.findByWashId(washId);
    }
}
