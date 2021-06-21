package com.lion.event.service.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.device.Device;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.event.dao.DeviceDataDao;
import com.lion.event.entity.DeviceData;
import com.lion.event.service.DeviceDataService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 上午11:37
 **/
@Service
public class DeviceDataServiceImpl implements DeviceDataService {

    @Autowired
    private DeviceDataDao deviceDataDao;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Override
    public void save(DeviceData deviceData) {
        deviceDataDao.save(deviceData);
    }

    @Override
    public IPageResultData<List<DeviceData>> list(Long starId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            endDateTime = LocalDateTime.now();
            startDateTime = endDateTime.minusDays(30);
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            endDateTime = startDateTime.plusDays(30);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            startDateTime = endDateTime.minusDays(30);
        }
        Device device = deviceExposeService.findById(starId);
        if (Objects.nonNull(device)){
            return deviceDataDao.list(device.getCode(),startDateTime,endDateTime,lionPage);
        }
        return new PageResultData<>();
    }
}
