package com.lion.event.expose.service.impl;

import com.lion.event.dao.SystemAlarmDao;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.expose.service.SystemAlarmExposeService;
import com.lion.manage.entity.enums.SystemAlarmType;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@DubboService(interfaceClass = SystemAlarmExposeService.class)
public class SystemAlarmExposeServiceImpl  implements SystemAlarmExposeService {

    @Autowired
    private SystemAlarmDao systemAlarmDao;

    @Override
    public SystemAlarm findLastByAssetsId(Long assetsId) {
        return systemAlarmDao.findLastByAssetsId(assetsId);
    }

    @Override
    public SystemAlarm findLastByTagId(Long tagId) {
        return systemAlarmDao.findLastByTagId(tagId);
    }

    @Override
    public String getSystemAlarmTypeCode(Integer key) {
        SystemAlarmType systemAlarmType = SystemAlarmType.instance(key);
        if (Objects.nonNull(systemAlarmType)) {
            return systemAlarmType.getName();
        }
        return null;
    }

    @Override
    public String getSystemAlarmTypeDesc(Integer key) {
        SystemAlarmType systemAlarmType = SystemAlarmType.instance(key);
        if (Objects.nonNull(systemAlarmType)) {
            return systemAlarmType.getDesc();
        }
        return null;
    }

}
