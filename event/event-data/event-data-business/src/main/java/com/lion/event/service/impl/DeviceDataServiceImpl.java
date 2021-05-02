package com.lion.event.service.impl;

import com.lion.event.dao.DeviceDataDao;
import com.lion.event.entity.DeviceData;
import com.lion.event.service.DeviceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 上午11:37
 **/
@Service
public class DeviceDataServiceImpl implements DeviceDataService {

    @Autowired
    private DeviceDataDao deviceDataDao;

    @Override
    public void save(DeviceData deviceData) {
        deviceDataDao.save(deviceData);
    }
}
